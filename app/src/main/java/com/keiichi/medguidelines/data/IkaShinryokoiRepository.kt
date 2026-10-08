package com.keiichi.medguidelines.data

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.sqlite.db.SimpleSQLiteQuery
import com.keiichi.medguidelines.ui.component.normalizeTextForSearch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.ParserOptions
import org.jetbrains.kotlinx.dataframe.api.rows
import org.jetbrains.kotlinx.dataframe.io.ColType
import org.jetbrains.kotlinx.dataframe.io.readCSV
import java.io.InputStream
import java.nio.charset.Charset

// 旧実装でお気に入り・並び順をDataStoreに保存していたキー（DBへの移行時のみ使用）
private val LIST_PAIRED_DATA_KEY_IKA = stringPreferencesKey("list_paired_data")

private const val NUMBER_OF_COLUMNS = 50
private const val KANJI_MEISHO_INDEX = 4
private const val KANA_MEISHO_INDEX = 6
private const val TENSU_SHIKIBETSU_INDEX = 10
private const val TENSU_INDEX = 11

class IkaShinryokoiRepository(private val ikaShinryokoiDao: IkaShinryokoiDao) {

    fun getAll(): Flow<List<PairedTextItem>> =
        ikaShinryokoiDao.getAll().map { list -> list.map { it.toPairedTextItem() } }

    /**
     * 複数ワードによるAND検索（各ワードは漢字名称・カナ名称のどちらかに含まれればヒット）。
     * wordsは正規化済みであること。空の場合は全件を返す。
     */
    fun searchMulti(words: List<String>): Flow<List<PairedTextItem>> {
        if (words.isEmpty()) return getAll()
        val where = words.joinToString(" AND ") {
            "(normalizedKanji LIKE ? ESCAPE '\\' OR normalizedKana LIKE ? ESCAPE '\\')"
        }
        val args = words.flatMap { word ->
            val pattern = "%${escapeLike(word)}%"
            listOf(pattern, pattern)
        }
        val query = SimpleSQLiteQuery(
            "SELECT * FROM ika_shinryokoi_master WHERE $where ORDER BY isFavorite DESC, sortOrder ASC",
            args.toTypedArray()
        )
        return ikaShinryokoiDao.search(query).map { list -> list.map { it.toPairedTextItem() } }
    }

    suspend fun updateFavorite(id: Int, isFavorite: Boolean) {
        withContext(Dispatchers.IO) { ikaShinryokoiDao.setFavorite(id, isFavorite) }
    }

    suspend fun populateDatabaseFromCsvIfEmpty(context: Context, resourceId: Int) {
        withContext(Dispatchers.IO) {
            try {
                if (ikaShinryokoiDao.getCount() == 0) {
                    Log.d("IkaShinryokoi", "Loading ika shinryokoi master data from CSV...")
                    val inputStream: InputStream = context.resources.openRawResource(resourceId)
                    val headerNames = (1..NUMBER_OF_COLUMNS).map { it.toString() }
                    val columnTypes: Map<String, ColType> = headerNames.associateWith { ColType.String }

                    val df = inputStream.use { stream ->
                        DataFrame.readCSV(
                            stream = stream,
                            header = headerNames,
                            charset = Charset.forName("Shift-JIS"),
                            colTypes = columnTypes,
                            parserOptions = ParserOptions()
                        )
                    }

                    val entityList = df.rows().mapIndexed { index, row ->
                        val kanjiMeisho = row[KANJI_MEISHO_INDEX]?.toString()
                        val kanaMeisho = row[KANA_MEISHO_INDEX]?.toString()
                        IkaShinryokoiEntity(
                            id = index,
                            kanjiMeisho = kanjiMeisho,
                            kanaMeisho = kanaMeisho,
                            tensuShikibetsu = row[TENSU_SHIKIBETSU_INDEX]?.toString() ?: "",
                            tensu = row[TENSU_INDEX]?.toString() ?: "",
                            normalizedKanji = normalizeTextForSearch(kanjiMeisho ?: ""),
                            normalizedKana = normalizeTextForSearch(kanaMeisho ?: ""),
                            sortOrder = index.toLong()
                        )
                    }

                    ikaShinryokoiDao.insertAll(applySavedOrderFromDataStore(context, entityList))
                    Log.d("IkaShinryokoi", "Successfully inserted ${entityList.size} items.")
                }
            } catch (e: Exception) {
                Log.e("IkaShinryokoi", "Failed to populate database from $resourceId", e)
            }
        }
    }

    /**
     * 旧実装でDataStoreに保存していたお気に入り・並び順を初期データに反映し、DataStoreからは削除する。
     */
    private suspend fun applySavedOrderFromDataStore(
        context: Context,
        entityList: List<IkaShinryokoiEntity>
    ): List<IkaShinryokoiEntity> {
        val jsonString = context.dataStore.data.first()[LIST_PAIRED_DATA_KEY_IKA] ?: return entityList
        val result = try {
            val savedList = Json.decodeFromString<List<PairedTextItem>>(jsonString)
            val byId = entityList.associateBy { it.id }
            val reordered = savedList.mapIndexedNotNull { position, saved ->
                byId[saved.originalIndex]?.copy(
                    isFavorite = saved.isFavorite,
                    sortOrder = position.toLong()
                )
            }
            // 件数が一致しない（マスタが更新された）場合は旧実装と同様に保存内容を破棄する
            if (savedList.size == entityList.size && reordered.size == entityList.size) reordered else entityList
        } catch (e: kotlinx.serialization.SerializationException) {
            entityList
        }
        context.dataStore.edit { it.remove(LIST_PAIRED_DATA_KEY_IKA) }
        return result
    }

    private fun escapeLike(text: String): String =
        text.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_")

    private fun IkaShinryokoiEntity.toPairedTextItem() = PairedTextItem(
        kanjiMeisho = kanjiMeisho,
        tensuShikibetsu = tensuShikibetsu,
        tensu = tensu,
        kanaMeisho = kanaMeisho,
        originalIndex = id,
        isFavorite = isFavorite,
        kanjiText = normalizedKanji,
        kanaText = normalizedKana
    )
}
