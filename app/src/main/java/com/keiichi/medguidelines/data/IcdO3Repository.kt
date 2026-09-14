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

private const val LEVEL_PREFERRED = "Preferred"
private const val LEVEL_SYNONYM = "Synonym"
private const val LEVEL_RELATED = "Related"

// 日本語病名末尾などに付与される "※1"～"※6"、"★"、"☆" の注釈記号は表示・検索の対象から除く
private val referenceMarkerRegex = Regex("\\s*(※[1-6]|[★☆])")

private fun stripReferenceMarkers(text: String): String = text.replace(referenceMarkerRegex, "").trim()

data class IcdO3AlternateTerm(
    val level: String,
    val termEn: String,
    val termJa: String
)

data class IcdO3SearchResult(
    val id: Long,
    val code: String,
    val termEn: String,
    val termJa: String,
    val isObsolete: Boolean,
    val isFavorite: Boolean,
    val alternateTerms: List<IcdO3AlternateTerm>
)

class IcdO3Repository(private val icdO3Dao: IcdO3Dao) {

    /**
     * 複数ワードによるAND検索。
     * Synonym/Relatedの行がヒットした場合は、同一コードのPreferred行を代表カードとして解決する。
     */
    fun searchMulti(words: List<String>): Flow<List<IcdO3SearchResult>> {
        val flow = when (words.size) {
            0 -> icdO3Dao.search("%%")
            1 -> icdO3Dao.search("%${words[0]}%")
            2 -> icdO3Dao.searchMulti("%${words[0]}%", "%${words[1]}%", "%%")
            else -> icdO3Dao.searchMulti("%${words[0]}%", "%${words[1]}%", "%${words[2]}%")
        }
        return flow.map { resolveRepresentatives(it) }
    }

    fun getFavorites(): Flow<List<IcdO3SearchResult>> =
        icdO3Dao.getFavorites().map { attachAlternateTerms(it) }

    suspend fun updateFavorite(id: Long, isFavorite: Boolean) {
        withContext(Dispatchers.IO) { icdO3Dao.updateFavorite(id, isFavorite) }
    }

    private suspend fun resolveRepresentatives(rows: List<IcdO3Entity>): List<IcdO3SearchResult> {
        val representativeByCode = LinkedHashMap<String, IcdO3Entity>()
        val codesNeedingLookup = LinkedHashSet<String>()
        for (row in rows) {
            if (row.level == LEVEL_SYNONYM || row.level == LEVEL_RELATED) {
                if (!representativeByCode.containsKey(row.code)) {
                    codesNeedingLookup.add(row.code)
                }
            } else {
                representativeByCode[row.code] = row
            }
        }
        if (codesNeedingLookup.isNotEmpty()) {
            val preferredRows = icdO3Dao.getPreferredByCodes(codesNeedingLookup.toList())
            for (preferred in preferredRows) {
                representativeByCode.putIfAbsent(preferred.code, preferred)
            }
        }
        return attachAlternateTerms(representativeByCode.values.toList())
    }

    private suspend fun attachAlternateTerms(representatives: List<IcdO3Entity>): List<IcdO3SearchResult> {
        if (representatives.isEmpty()) return emptyList()
        val alternatesByCode = icdO3Dao.getAlternateTermsByCodes(representatives.map { it.code })
            .groupBy { it.code }
        return representatives.map { rep ->
            IcdO3SearchResult(
                id = rep.id,
                code = rep.code,
                termEn = rep.termEn,
                termJa = rep.termJa,
                isObsolete = rep.isObsolete,
                isFavorite = rep.isFavorite,
                alternateTerms = alternatesByCode[rep.code].orEmpty().map {
                    IcdO3AlternateTerm(it.level, it.termEn, it.termJa)
                }
            )
        }
    }

    suspend fun populateDatabaseFromCsvIfEmpty(context: Context, resourceId: Int) {
        withContext(Dispatchers.IO) {
            try {
                if (icdO3Dao.getCount() == 0) {
                    Log.d("IcdO3", "Loading ICD-O-3 master data from CSV...")
                    val inputStream: InputStream = context.resources.openRawResource(resourceId)

                    // CSVの列: 1:code, 2:level, 3:termEn, 4:termJa, 5:obsolete
                    val headerNames = (1..5).map { it.toString() }
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
                        val termEn = row[2]?.toString()?.trim() ?: ""
                        val termJa = stripReferenceMarkers(row[3]?.toString()?.trim() ?: "")
                        val obsolete = row[4]?.toString()?.trim() == "1"

                        IcdO3Entity(
                            code = code,
                            level = level,
                            termEn = termEn,
                            termJa = termJa,
                            isObsolete = obsolete,
                            normalizedTermEn = normalizeTextForSearch(termEn),
                            normalizedTermJa = normalizeTextForSearch(termJa)
                        )
                    }

                    icdO3Dao.insertAll(entityList)
                    Log.d("IcdO3", "Successfully inserted ${entityList.size} items.")
                }
            } catch (e: Exception) {
                Log.e("IcdO3", "Failed to populate database from $resourceId", e)
            }
        }
    }
}
