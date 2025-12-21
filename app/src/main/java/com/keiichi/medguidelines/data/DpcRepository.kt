// C:/Users/tamaikeiichi/StudioProjects/MedGuidelines/app/src/main/java/com/keiichi/medguidelines/data/DpcRepository.kt

package com.keiichi.medguidelines.data

import android.content.Context
import android.util.Log
import com.keiichi.medguidelines.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.rows
import org.jetbrains.kotlinx.dataframe.io.NameRepairStrategy
import org.jetbrains.kotlinx.dataframe.io.readExcel
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
                if (dpcDao.getIcdCount() == 0) {
                    Log.d("tamaiDpc", "icd reading")
                    context.resources.openRawResource(excelResourceId).use { icdStream ->
                        // Apache POIを直接使用してストリーミング処理
                        val workbook = WorkbookFactory.create(icdStream)
                        val sheet = workbook.getSheet("４）ＩＣＤ")

                        val batchSize = 1  // バッチサイズを調整
                        val batch = mutableListOf<IcdEntity>()
                        Log.d("tamaiDpc", "set val")
                        for (row in sheet) {
                            if (row.rowNum == 0) continue  // ヘッダーをスキップ

                            val entity = IcdEntity(
                                mdcCode = row.getCell(0)?.toString(),
                                bunruiCode = row.getCell(1)?.toString(),
                                icdName = row.getCell(2)?.toString(),
                                icdCode = row.getCell(3)?.toString()
                            )
                            batch.add(entity)
                            Log.d("tamaiDpc", "batch added")
                            if (batch.size >= batchSize) {
                                dpcDao.insertAllIcd(batch)
                                batch.clear()
                                Log.d("tamaiDpc", "Inserted batch")
                            }
                        }

                        // 残りを挿入
                        if (batch.isNotEmpty()) {
                            dpcDao.insertAllIcd(batch)
                        }

                        workbook.close()
                    }
                    Log.d("tamaiDpc", "icd inserted")
                }

            // Byotaiテーブルのチェックと投入
            if (dpcDao.getByotaiCount() == 0) {
                Log.d("tamaiDpc", "byotai reading")
                val byotaiStream = context.resources.openRawResource(excelResourceId)
                val byotaiDf = DataFrame.readExcel(byotaiStream, "３）病態等分類",
                    skipRows = 0,
                    nameRepairStrategy = NameRepairStrategy.MAKE_UNIQUE,
                    firstRowIsHeader = false,)
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
                Log.d("tamaiDpc", "bunrui reading")
                val bunruiStream = context.resources.openRawResource(excelResourceId)
                val bunruiDf = DataFrame.readExcel(bunruiStream, "２）分類名称",
                    skipRows = 0,
                    nameRepairStrategy = NameRepairStrategy.MAKE_UNIQUE,
                    firstRowIsHeader = false,)
                val bunruiList = bunruiDf.rows().map { row ->
                    BunruiEntity(
                        // BunruiEntityの定義に合わせて列を指定
                        mdcCode = row[0]?.toString(),
                        bunruiCode = row[1]?.toString(),
                        bunruiName = row[2]?.toString()
                    )
                }
                dpcDao.insertAllBunrui(bunruiList)
            }
            if (dpcDao.getMdcCount() == 0) {
                val mdcStream = context.resources.openRawResource(excelResourceId)
                val mdcDf = DataFrame.readExcel(
                    mdcStream,
                    "１）ＭＤＣ名称",
                    skipRows = 0,
                    nameRepairStrategy = NameRepairStrategy.MAKE_UNIQUE,
                    firstRowIsHeader = false,
                    )
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
