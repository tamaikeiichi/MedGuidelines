// C:/Users/tamaikeiichi/StudioProjects/MedGuidelines/app/src/main/java/com/keiichi/medguidelines/data/DpcRepository.kt

package com.keiichi.medguidelines.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.rows
import org.jetbrains.kotlinx.dataframe.io.readExcel

class DpcRepository(private val dpcDao: DpcDao) {

    // 検索クエリに基づいてICDマスターを検索する
    // DAOのメソッドがFlowを返すので、そのままViewModelに渡す
    fun searchIcd(query: String) = dpcDao.searchIcd("%$query%")

    /**
     * データベースにデータが存在しない場合、Excelから読み込んで挿入する
     */
    suspend fun populateDatabaseFromExcelIfEmpty(context: Context) {
        // I/Oスレッドで実行
        withContext(Dispatchers.IO) {
            // ICDテーブルが空の場合のみ処理を実行
            if (dpcDao.getIcdCount() == 0) {
                // 1. Excelファイルを読み込む
                val df: DataFrame<*> = DataFrame.readExcel(
                    context.assets.open("dpc/４）ＩＣＤ.xlsx")
                )

                // 2. DataFrameの各行をIcdEntityオブジェクトに変換する
                val icdList = df.rows().map { row ->
                    IcdEntity(
                        mdcCode = row[0]?.toString(),    // 1列目
                        bunruiCode = row[1]?.toString(), // 2列目
                        icdName = row[2]?.toString(),  // 3列目
                        icdCode = row[3]?.toString()     // 4列目
                    )
                }

                // 3. 変換したリストをデータベースに挿入する
                dpcDao.insertAllIcd(icdList)
            }

            // TODO: 他のテーブル(byotaiなど)についても同様のチェックとデータ投入処理を追加
        }
    }
}
