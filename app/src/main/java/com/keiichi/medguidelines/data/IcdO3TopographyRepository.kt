package com.keiichi.medguidelines.data

import android.content.Context
import android.util.Log
import com.keiichi.medguidelines.ui.component.normalizeTextForSearch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.ParserOptions
import org.jetbrains.kotlinx.dataframe.api.rows
import org.jetbrains.kotlinx.dataframe.io.ColType
import org.jetbrains.kotlinx.dataframe.io.readCSV
import java.io.InputStream
import java.nio.charset.Charset

private const val TERM_TYPE_PREFERRED = "優先用語"

data class IcdO3TopographyAlternateTerm(
    val termEn: String,
    val termJa: String
)

data class IcdO3TopographySearchResult(
    val id: Long,
    val code: String,
    val level: String,
    val termEn: String,
    val termJa: String,
    val needsReview: Boolean,
    val isFavorite: Boolean,
    val alternateTerms: List<IcdO3TopographyAlternateTerm>
)

class IcdO3TopographyRepository(private val icdO3TopographyDao: IcdO3TopographyDao) {

    /**
     * 複数ワードによるAND検索。
     * 同義語がヒットした場合は、同一コードの優先用語を代表カードとして解決する。
     */
    fun searchMulti(words: List<String>): Flow<List<IcdO3TopographySearchResult>> {
        val flow = when (words.size) {
            0 -> icdO3TopographyDao.search("%%")
            1 -> icdO3TopographyDao.search("%${words[0]}%")
            2 -> icdO3TopographyDao.searchMulti("%${words[0]}%", "%${words[1]}%", "%%")
            else -> icdO3TopographyDao.searchMulti("%${words[0]}%", "%${words[1]}%", "%${words[2]}%")
        }
        return flow.map { resolveRepresentatives(it) }
    }

    fun getFavorites(): Flow<List<IcdO3TopographySearchResult>> =
        icdO3TopographyDao.getFavorites().map { attachAlternateTerms(it) }

    suspend fun updateFavorite(id: Long, isFavorite: Boolean) {
        withContext(Dispatchers.IO) { icdO3TopographyDao.updateFavorite(id, isFavorite) }
    }

    private suspend fun resolveRepresentatives(rows: List<IcdO3TopographyEntity>): List<IcdO3TopographySearchResult> {
        val representativeByCode = LinkedHashMap<String, IcdO3TopographyEntity>()
        val codesNeedingLookup = LinkedHashSet<String>()
        for (row in rows) {
            if (row.termType != TERM_TYPE_PREFERRED) {
                if (!representativeByCode.containsKey(row.code)) {
                    codesNeedingLookup.add(row.code)
                }
            } else {
                representativeByCode[row.code] = row
            }
        }
        if (codesNeedingLookup.isNotEmpty()) {
            val preferredRows = icdO3TopographyDao.getPreferredByCodes(codesNeedingLookup.toList())
            for (preferred in preferredRows) {
                representativeByCode.putIfAbsent(preferred.code, preferred)
            }
        }
        return attachAlternateTerms(representativeByCode.values.toList())
    }

    private suspend fun attachAlternateTerms(representatives: List<IcdO3TopographyEntity>): List<IcdO3TopographySearchResult> {
        if (representatives.isEmpty()) return emptyList()
        val alternatesByCode = icdO3TopographyDao.getAlternateTermsByCodes(representatives.map { it.code })
            .groupBy { it.code }
        return representatives.map { rep ->
            IcdO3TopographySearchResult(
                id = rep.id,
                code = rep.code,
                level = rep.level,
                termEn = rep.termEn,
                termJa = rep.termJa,
                needsReview = rep.needsReview,
                isFavorite = rep.isFavorite,
                alternateTerms = alternatesByCode[rep.code].orEmpty().map {
                    IcdO3TopographyAlternateTerm(it.termEn, it.termJa)
                }
            )
        }
    }

    /**
     * DBが空、または取り込み済みのCSVと[resourceId]が異なる（マスタが更新された）場合にCSVから取り込む。
     * 更新時は旧データを全て破棄し、お気に入りのみコードと用語種別で引き継ぐ。
     */
    suspend fun populateDatabaseFromCsvIfNeeded(context: Context, resourceId: Int) {
        withContext(Dispatchers.IO) {
            try {
                if (needsMasterReload(context, "icd_o3_topography", resourceId, icdO3TopographyDao.getCount())) {
                    Log.d("IcdO3Topography", "Loading ICD-O-3 topography master data from CSV...")
                    val inputStream: InputStream = context.resources.openRawResource(resourceId)

                    // CSVの列: 1:code, 2:level, 3:term_type, 4:japanese, 5:english, 6:needs_review
                    val headerNames = (1..6).map { it.toString() }
                    val columnTypes: Map<String, ColType> = headerNames.associateWith { ColType.String }

                    val df = inputStream.use { stream ->
                        DataFrame.readCSV(
                            stream = stream,
                            header = headerNames,
                            charset = Charset.forName("UTF-8"),
                            delimiter = ',',
                            colTypes = columnTypes,
                            skipLines = 1, // CSV先頭の見出し行をスキップ
                            parserOptions = ParserOptions()
                        )
                    }

                    val entityList = df.rows().mapNotNull { row ->
                        val code = row[0]?.toString()?.trim() ?: ""
                        if (code.isBlank()) return@mapNotNull null
                        val level = row[1]?.toString()?.trim() ?: ""
                        val termType = row[2]?.toString()?.trim() ?: ""
                        val termJa = row[3]?.toString()?.trim() ?: ""
                        val termEn = row[4]?.toString()?.trim() ?: ""
                        val needsReview = !(row[5]?.toString()?.trim().isNullOrBlank())

                        IcdO3TopographyEntity(
                            code = code,
                            level = level,
                            termType = termType,
                            termEn = termEn,
                            termJa = termJa,
                            needsReview = needsReview,
                            normalizedTermEn = normalizeTextForSearch(termEn),
                            normalizedTermJa = normalizeTextForSearch(termJa)
                        )
                    }

                    val favoriteKeys = icdO3TopographyDao.getFavoriteRows().map { it.code to it.termType }.toSet()
                    icdO3TopographyDao.replaceAll(
                        entityList.map { it.copy(isFavorite = (it.code to it.termType) in favoriteKeys) }
                    )
                    markMasterLoaded(context, "icd_o3_topography", resourceId)
                    Log.d("IcdO3Topography", "Successfully inserted ${entityList.size} items.")
                }
            } catch (e: Exception) {
                Log.e("IcdO3Topography", "Failed to populate database from $resourceId", e)
            }
        }
    }
}
