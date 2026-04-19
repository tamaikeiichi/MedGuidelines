package com.keiichi.medguidelines.data

import android.content.Context
import android.util.Log
import com.keiichi.medguidelines.ui.component.normalizeTextForSearch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.ParserOptions
import org.jetbrains.kotlinx.dataframe.api.rows
import org.jetbrains.kotlinx.dataframe.io.ColType
import org.jetbrains.kotlinx.dataframe.io.readCSV
import java.io.InputStream
import java.nio.charset.Charset
import kotlinx.coroutines.flow.Flow

class ShyobyomeiRepository(private val shobyomeiDao: ShobyomeiDao) {

    fun search(query: String) = shobyomeiDao.search("%$query%")

    /**
     * 複数ワードによるAND検索
     */
    fun searchMulti(words: List<String>) = when (words.size) {
        0 -> shobyomeiDao.search("%%")
        1 -> shobyomeiDao.search("%${words[0]}%")
        2 -> shobyomeiDao.searchMulti("%${words[0]}%", "%${words[1]}%", "%%")
        else -> shobyomeiDao.searchMulti("%${words[0]}%", "%${words[1]}%", "%${words[2]}%")
    }

    suspend fun updateFavorite(code: String, isFavorite: Boolean) {
        withContext(Dispatchers.IO) {
            shobyomeiDao.updateFavorite(code, isFavorite)
        }
    }

    fun getFavorites(): Flow<List<ShobyomeiEntity>> = shobyomeiDao.getFavorites()

    suspend fun populateDatabaseFromCsvIfEmpty(context: Context, resourceId: Int) {
        withContext(Dispatchers.IO) {
            try {
                if (shobyomeiDao.getCount() == 0) {
                    Log.d("IkaShiryokoi", "Loading master data from CSV (quoted)...")
                    val inputStream: InputStream = context.resources.openRawResource(resourceId)

                    // 列名は仮の名前 (1, 2, 3...) を割り当て
                    val headerNames = (1..50).map { it.toString() }
                    val columnTypes: Map<String, ColType> = headerNames.associateWith { ColType.String }

                    val df = inputStream.use { stream ->
                        DataFrame.readCSV(
                            stream = stream,
                            header = headerNames,
                            charset = Charset.forName("MS932"), // 日本のマスターは通常 Shift-JIS
                            delimiter = ',',                    // カンマ区切り
                            // quoteChar はデフォルトで '"' なので、ダブルクォーテーション囲みは自動で処理されます
                            colTypes = columnTypes,
                            skipLines = 0,
                            parserOptions = ParserOptions()
                        )
                    }

                    // 傷病名マスター (b.txt) の標準的な列定義 (1開始):
                    // 1:変更区分, 2:コード, 3:ICD10_1, 4:ICD10_2, 5:名称, 6:カナ
                    val entityList = df.rows().map { row ->
                        val code = row[2]?.toString() ?: ""           // 2列目: 傷病名コード
                        val icd1 = row[15]?.toString() ?: ""           // 3列目: ICD10-1
                        val icd2 = row[16]?.toString() ?: ""           // 4列目: ICD10-2
                        val name = row[7]?.toString() ?: ""           // 5列目: 漢字名称
                        val kana = row[9]?.toString() ?: ""           // 6列目: カナ名称

                        ShobyomeiEntity(
                            code = code,
                            name = name,
                            kanaName = kana,
                            icd_10_1 = icd1,
                            icd_10_2 = icd2,
                            normalizedName = normalizeTextForSearch(name),
                            normalizedKanaName = normalizeTextForSearch(kana),
                            isFavorite = false
                        )
                    }.filter { it.code.isNotBlank() }

                    shobyomeiDao.insertAll(entityList)
                    Log.d("IkaShiryokoi", "Successfully inserted ${entityList.size} items.")
                }
            } catch (e: Exception) {
                Log.e("IkaShiryokoi", "Failed to populate database from $resourceId", e)
            }
        }
    }
    suspend fun getDebugNames(): List<String> = shobyomeiDao.getDebugNames()
}
