// C:/Users/tamaikeiichi/StudioProjects/MedGuidelines/app/src/main/java/com/keiichi/medguidelines/data/DpcRepository.kt

package com.keiichi.medguidelines.data

import android.content.Context
import android.util.Log
import com.keiichi.medguidelines.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.ParserOptions
import org.jetbrains.kotlinx.dataframe.api.rows
import org.jetbrains.kotlinx.dataframe.io.ColType
import org.jetbrains.kotlinx.dataframe.io.NameRepairStrategy
import org.jetbrains.kotlinx.dataframe.io.readCSV
import org.jetbrains.kotlinx.dataframe.io.readExcel
import java.io.InputStream
import java.nio.charset.Charset
import kotlin.collections.get
import kotlin.toString

class DpcRepository(private val dpcDao: DpcDao) {
    val excelResourceId = R.raw.dpc001348055
    // 検索クエリに基づいてICDマスターを検索する
    // DAOのメソッドがFlowを返すので、そのままViewModelに渡す
    fun searchIcd(query: String) = dpcDao.searchIcd("%$query%")

    /**
     * データベースにデータが存在しない場合、Excelから読み込んで挿入する
     */

    /**
     * MDCコードと分類コードがbyotaiマスターに存在するかチェックする。
     * @param mdcCode チェックするMDCコード
     * @param bunruiCode チェックする分類コード
     * @return 存在すればtrue
     */
    suspend fun checkMdcAndBunruiExist(mdcCode: String, bunruiCode: String): Boolean {
        return withContext(Dispatchers.IO) {
            val mdcExists = dpcDao.getUniqueMdc(mdcCode).isNotEmpty()
            val bunruiExists = dpcDao.getUniqueBunrui(bunruiCode).isNotEmpty()
            mdcExists && bunruiExists
        }
    }

    /**
     * MDCコードと分類コードに一致する病態名のリストを取得する。
     * @param mdcCode 検索するMDCコード
     * @param bunruiCode 検索する分類コード
     * @return 病態名のリスト
     */
    suspend fun getByotaiNames(mdcCode: String, bunruiCode: String): List<String> {
        return withContext(Dispatchers.IO) {
            dpcDao.getByotaiNames(mdcCode, bunruiCode)
        }
    }

    /**
     * 病態名から対応する病態コードを取得する。
     * @param byotaiName 検索する病態名
     * @return 病態コード。見つからなければnull。
     */
    suspend fun getByotaiCodeByName(byotaiName: String): String? {
        return withContext(Dispatchers.IO) {
            dpcDao.getByotaiCodeByName(byotaiName)
        }
    }

    /**
     * データベースにデータが存在しない場合、Excelから読み込んで挿入する。
     * @param context アプリケーションコンテキスト
     */
    suspend fun populateDatabaseFromExcelIfEmpty(context: Context) {
        withContext(Dispatchers.IO) {
            try {
                // ICDテーブルのチェックと投入 (CSVからまとめて読み込む)
                if (dpcDao.getIcdCount() == 0) {
                    val headerNames = (1..4).map { it.toString() }
                    val columnTypes: Map<String, ColType> = headerNames.associateWith { ColType.String }
                    val inputStream: InputStream = context.resources.openRawResource(R.raw.dpc001348055_4)
                    Log.d("tamaiDpc", "icd reading from CSV...")
                    // DataFrameを格納する変数を宣言
                    val icdDf: DataFrame<*>
                    inputStream.use { stream ->
                        // CSVの全データをDataFrameとして一括で読み込む
                        icdDf = DataFrame.readCSV(
                            stream = stream,
                            header = headerNames,
                            charset = Charset.forName("Shift-JIS"),
                            colTypes = columnTypes, // Specify that all columns should be read as String
                            parserOptions = ParserOptions() // Keep default or adjust as needed
                        )
                    }
                    // 読み込んだDataFrameをIcdEntityのリストに変換する
                    val icdList = icdDf.rows().map { row ->
                        IcdEntity(
                            mdcCode = row[0]?.toString(),
                            bunruiCode = row[1]?.toString(),
                            icdName = row[2]?.toString(),
                            icdCode = row[3]?.toString()
                        )
                    }
                    // 変換したリストをデータベースに挿入する
                    dpcDao.insertAllIcd(icdList)
                    Log.d("tamaiDpc", "icd inserted from CSV.")
                }

            // Byotaiテーブルのチェックと投入
                if (dpcDao.getByotaiCount() == 0) {
                    val headerNames = (1..10).map { it.toString() }
                    val columnTypes: Map<String, ColType> = headerNames.associateWith { ColType.String }
                    val inputStream: InputStream = context.resources.openRawResource(R.raw.dpc001348055_3)
                    Log.d("tamaiDpc", "byotai reading")
                    // DataFrameを格納する変数を宣言
                    val byotaiDf: DataFrame<*>
                    inputStream.use { stream ->
                        // CSVの全データをDataFrameとして一括で読み込む
                        byotaiDf = DataFrame.readCSV(
                            stream = stream,
                            header = headerNames,
                            charset = Charset.forName("Shift-JIS"),
                            colTypes = columnTypes, // Specify that all columns should be read as String
                            parserOptions = ParserOptions() // Keep default or adjust as needed
                        )
                    }

                val byotaiList = byotaiDf.rows().map { row ->
                    ByotaiEntity(
                        mdcCode = row[0]?.toString(),
                        bunruiCode = row[1]?.toString(),
                        byotaiCode = row[2]?.toString(),
                        byotaiKubunMeisho = row[7]?.toString()
                    )
                }
                dpcDao.insertAllByotai(byotaiList)
            }
            // --- ここからBunruiテーブルのチェックと投入処理を追加 ---
            if (dpcDao.getBunruiCount() == 0) {
                val headerNames = (1..4).map { it.toString() }
                val columnTypes: Map<String, ColType> = headerNames.associateWith { ColType.String }
                val inputStream: InputStream = context.resources.openRawResource(R.raw.dpc001348055_2)
                Log.d("tamaiDpc", "bunrui reading")
                // DataFrameを格納する変数を宣言
                val bunruiDf: DataFrame<*>
                inputStream.use { stream ->
                    // CSVの全データをDataFrameとして一括で読み込む
                    bunruiDf = DataFrame.readCSV(
                        stream = stream,
                        header = headerNames,
                        charset = Charset.forName("Shift-JIS"),
                        colTypes = columnTypes, // Specify that all columns should be read as String
                        parserOptions = ParserOptions() // Keep default or adjust as needed
                    )
                }
                val bunruiList = bunruiDf.rows().map { row ->
                    BunruiEntity(
                        // BunruiEntityの定義に合わせて列を指定
                        mdcCode = row[0]?.toString(),
                        bunruiCode = row[1]?.toString(),
                        bunruiName = row[2]?.toString()
                    )
                }
                dpcDao.insertAllBunrui(bunruiList)
                Log.d("tamaiDpc", "bunrui read")
            }

            if (dpcDao.getMdcCount() == 0) {
                val headerNames = (1..4).map { it.toString() }
                val columnTypes: Map<String, ColType> = headerNames.associateWith { ColType.String }
                val inputStream: InputStream = context.resources.openRawResource(R.raw.dpc001348055_1)
                Log.d("tamaiDpc", "mdc reading")
                // DataFrameを格納する変数を宣言
                val mdcDf: DataFrame<*>
                inputStream.use { stream ->
                    // CSVの全データをDataFrameとして一括で読み込む
                    mdcDf = DataFrame.readCSV(
                        stream = stream,
                        header = headerNames,
                        charset = Charset.forName("Shift-JIS"),
                        colTypes = columnTypes, // Specify that all columns should be read as String
                        parserOptions = ParserOptions() // Keep default or adjust as needed
                    )
                }
                val mdcList = mdcDf.rows().map { row ->
                    MdcEntity(
                        // MdcEntityの定義に合わせて列を指定
                        mdcCode = row[0]?.toString(),
                        mdcName = row[1]?.toString()
                    )
                }
                dpcDao.insertAllMdc(mdcList)
            }
            } catch (e: Exception) {
                Log.e("DpcRepository", "Excelからのデータベース構築に失敗しました。", e)
            }
            // TODO: 他のテーブルについても同様のチェックとデータ投入処理を追加
        }
    }
    /**
     * 分類マスターテーブルを検索する。
     * DAOのメソッドがFlowを返すので、そのままViewModelに渡す。
     * @param query 検索文字列
     * @return 検索結果のFlow
     */
    fun searchBunrui(query: String) = dpcDao.searchBunrui("%$query%")



}
