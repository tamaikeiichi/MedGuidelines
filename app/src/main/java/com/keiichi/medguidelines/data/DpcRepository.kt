// C:/Users/tamaikeiichi/StudioProjects/MedGuidelines/app/src/main/java/com/keiichi/medguidelines/data/DpcRepository.kt

package com.keiichi.medguidelines.data

import android.content.Context
import android.util.Log
import android.util.Log.e
import com.keiichi.medguidelines.R
import com.keiichi.medguidelines.ui.component.normalizeTextForSearch
import com.keiichi.medguidelines.ui.screen.LabelStringAndScore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.ParserOptions
import org.jetbrains.kotlinx.dataframe.api.rows
import org.jetbrains.kotlinx.dataframe.io.ColType
import org.jetbrains.kotlinx.dataframe.io.readCSV
import java.io.InputStream
import java.nio.charset.Charset

class DpcRepository(private val dpcDao: DpcDao) {

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

    suspend fun checkBunruiExistsInNenrei(bunruiCode: String): Boolean {
        return dpcDao.existsBunruiInNenreiMaster(bunruiCode)
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
    suspend fun getNenreiJoken1Ijo(mdcCode: String, bunruiCode: String): String {
        return withContext(Dispatchers.IO) {
            dpcDao.getNenreiJoken1Ijo(mdcCode, bunruiCode)
        }
    }
    suspend fun getNenreiJoken1Miman(mdcCode: String, bunruiCode: String): String {
        return withContext(Dispatchers.IO) {
            dpcDao.getNenreiJoken1Miman(mdcCode, bunruiCode)
        }
    }
    suspend fun getNenreiJoken1Value(mdcCode: String, bunruiCode: String): String {
        return withContext(Dispatchers.IO) {
            dpcDao.getNenreiJoken1Value(mdcCode, bunruiCode)
        }
    }
    suspend fun getNenreiJoken2Ijo(mdcCode: String, bunruiCode: String): String {
        return withContext(Dispatchers.IO) {
            dpcDao.getNenreiJoken2Ijo(mdcCode, bunruiCode)
        }
    }
    suspend fun getNenreiJoken2Miman(mdcCode: String, bunruiCode: String): String {
        return withContext(Dispatchers.IO) {
            dpcDao.getNenreiJoken2Miman(mdcCode, bunruiCode)
        }
        }
    suspend fun getNenreiJoken2Value(mdcCode: String, bunruiCode: String): String {
        return withContext(Dispatchers.IO) {
            dpcDao.getNenreiJoken2Value(mdcCode, bunruiCode)
        }
    }
    suspend fun getNenreiJoken3Ijo(mdcCode: String, bunruiCode: String): String {
        return withContext(Dispatchers.IO) {
            dpcDao.getNenreiJoken3Ijo(mdcCode, bunruiCode)
        }
    }
    suspend fun getNenreiJoken3Miman(mdcCode: String, bunruiCode: String): String {
        return withContext(Dispatchers.IO) {
            dpcDao.getNenreiJoken3Miman(mdcCode, bunruiCode)
        }
    }
    suspend fun getNenreiJoken3Value(mdcCode: String, bunruiCode: String): String {
        return withContext(Dispatchers.IO) {
            dpcDao.getNenreiJoken3Value(mdcCode, bunruiCode)
        }
    }
    suspend fun getNenreiJoken4Ijo(mdcCode: String, bunruiCode: String): String {
        return withContext(Dispatchers.IO) {
            dpcDao.getNenreiJoken4Ijo(mdcCode, bunruiCode)
        }
    }
    suspend fun getNenreiJoken4Miman(mdcCode: String, bunruiCode: String): String {
        return withContext(Dispatchers.IO) {
            dpcDao.getNenreiJoken4Miman(mdcCode, bunruiCode)
        }
        }
    suspend fun getNenreiJoken4Value(mdcCode: String, bunruiCode: String): String {
        return withContext(Dispatchers.IO) {
            dpcDao.getNenreiJoken4Value(mdcCode, bunruiCode)
        }
    }
    suspend fun getNenreiJoken5Ijo(mdcCode: String, bunruiCode: String): String {
        return withContext(Dispatchers.IO) {
            dpcDao.getNenreiJoken5Ijo(mdcCode, bunruiCode)
        }
    }
    suspend fun getNenreiJoken5Miman(mdcCode: String, bunruiCode: String): String {
        return withContext(Dispatchers.IO) {
            dpcDao.getNenreiJoken5Miman(mdcCode, bunruiCode)

        }
    }
    suspend fun getNenreiJoken5Value(mdcCode: String, bunruiCode: String): String {
        return withContext(Dispatchers.IO) {
            dpcDao.getNenreiJoken5Value(mdcCode, bunruiCode)
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
                    val columnTypes: Map<String, ColType> =
                        headerNames.associateWith { ColType.String }
                    val inputStream: InputStream =
                        context.resources.openRawResource(R.raw.dpc001593946_4)
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
                            skipLines = 2,
                            parserOptions = ParserOptions() // Keep default or adjust as needed
                        )
                    }
                    // 読み込んだDataFrameをIcdEntityのリストに変換する
                    val icdList = icdDf.rows().map { row ->
                        IcdEntity(
                            mdcCode = row[0]?.toString() ?: "",
                            bunruiCode = row[1]?.toString() ?: "",
                            icdName = row[2]?.toString() ?: "",
                            icdCode = row[3]?.toString() ?: "",
                            normalizedIcdName = normalizeTextForSearch(row[2]?.toString() ?: "")
                        )
                    }
                    // 変換したリストをデータベースに挿入する
                    dpcDao.insertAllIcd(icdList)
                    Log.d("tamaiDpc", "icd inserted from CSV.")
                }

                // Byotaiテーブルのチェックと投入
                if (dpcDao.getByotaiCount() == 0) {
                    val headerNames = (1..8).map { it.toString() }
                    val columnTypes: Map<String, ColType> =
                        headerNames.associateWith { ColType.String }
                    val inputStream: InputStream =
                        context.resources.openRawResource(R.raw.dpc001593946_3)
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
                            skipLines = 2,
                            parserOptions = ParserOptions() // Keep default or adjust as needed
                        )
                    }

                    val byotaiList = byotaiDf.rows().map { row ->
                        ByotaiEntity(
                            mdcCode = row[0]?.toString() ?: "",
                            bunruiCode = row[1]?.toString() ?: "",
                            byotaiCode = row[2]?.toString() ?: "",
                            byotaiKubunMeisho = row[7]?.toString() ?: ""
                        )
                    }
                    dpcDao.insertAllByotai(byotaiList)
                }
                // --- ここからBunruiテーブルのチェックと投入処理を追加 ---
                if (dpcDao.getBunruiCount() == 0) {
                    val headerNames = (1..4).map { it.toString() }
                    val columnTypes: Map<String, ColType> =
                        headerNames.associateWith { ColType.String }
                    val inputStream: InputStream =
                        context.resources.openRawResource(R.raw.dpc001593946_2)
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
                            skipLines = 2,
                            parserOptions = ParserOptions() // Keep default or adjust as needed
                        )
                    }
                    val bunruiList = bunruiDf.rows().map { row ->
                        BunruiEntity(
                            // BunruiEntityの定義に合わせて列を指定
                            mdcCode = row[0]?.toString() ?: "",
                            bunruiCode = row[1]?.toString() ?: "",
                            bunruiName = row[2]?.toString() ?: ""
                        )
                    }
                    dpcDao.insertAllBunrui(bunruiList)
                    Log.d("tamaiDpc", "bunrui read")
                }

                if (dpcDao.getMdcCount() == 0) {
                    val headerNames = (1..4).map { it.toString() }
                    val columnTypes: Map<String, ColType> =
                        headerNames.associateWith { ColType.String }
                    val inputStream: InputStream =
                        context.resources.openRawResource(R.raw.dpc001593946_1)
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
                            skipLines = 2,
                            parserOptions = ParserOptions() // Keep default or adjust as needed
                        )
                    }
                    val mdcList = mdcDf.rows().map { row ->
                        MdcEntity(
                            // MdcEntityの定義に合わせて列を指定
                            mdcCode = row[0]?.toString() ?: "",
                            mdcName = row[1]?.toString() ?: ""
                        )
                    }
                    dpcDao.insertAllMdc(mdcList)
                }

                if (dpcDao.getNenreiCount() == 0) {
                    val headerNames = (1..21).map { it.toString() }
                    val columnTypes: Map<String, ColType> =
                        headerNames.associateWith { ColType.String }
                    val inputStream: InputStream =
                        context.resources.openRawResource(R.raw.dpc001593946_5)
                    Log.d("tamaiDpc", "nenrei reading")
                    // DataFrameを格納する変数を宣言
                    val nenreiDf: DataFrame<*>
                    inputStream.use { stream ->
                        // CSVの全データをDataFrameとして一括で読み込む
                        nenreiDf = DataFrame.readCSV(
                            stream = stream,
                            header = headerNames,
                            charset = Charset.forName("Shift-JIS"),
                            colTypes = columnTypes, // Specify that all columns should be read as String
                            skipLines = 2,
                            parserOptions = ParserOptions() // Keep default or adjust as needed
                        )
                    }
                    val nenreiList = nenreiDf.rows().map { row ->
                        NenreiEntity(
                            // nenreiEntityの定義に合わせて列を指定
                            mdcCode = row[0]?.toString() ?: "",
                            bunruiCode = row[1]?.toString() ?: "",
                            jokenKubun = row[2]?.toString() ?: "",
                            jokenName = row[3]?.toString() ?: "",
                            joken1Ijo = row[4]?.toString() ?: "",
                            joken1Miman = row[5]?.toString() ?: "",
                            joken1Value = row[6]?.toString() ?: "",
                            joken2Ijo = row[7]?.toString() ?: "",
                            joken2Miman = row[8]?.toString() ?: "",
                            joken2Value = row[9]?.toString() ?: "",
                            joken3Ijo = row[10]?.toString() ?: "",
                            joken3Miman = row[11]?.toString() ?: "",
                            joken3Value = row[12]?.toString() ?: "",
                            joken4Ijo = row[13]?.toString() ?: "",
                            joken4Miman = row[14]?.toString() ?: "",
                            joken4Value = row[15]?.toString() ?: "",
                            joken5Ijo = row[16]?.toString() ?: "",
                            joken5Miman = row[17]?.toString() ?: "",
                            joken5Value = row[18]?.toString() ?: "",
                            strokeKubun = row[19]?.toString() ?: "",
                            strokeName = row[20]?.toString() ?: "",
                        )
                    }
                    dpcDao.insertAllNenrei(nenreiList)
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

class ShujutsuRepository(private val shujutsuDao: ShujutsuDao) {
    suspend fun getShujutsuNames(mdcCode: String, bunruiCode: String): List<String> {
        return withContext(Dispatchers.IO) {
            shujutsuDao.getShujutsuNames(mdcCode, bunruiCode)
        }
    }

    suspend fun populateDatabaseFromExcelIfEmpty(context: Context) {
        withContext(Dispatchers.IO) {
            try {
                Log.d("tamaiDpc", "shujutsu check ${shujutsuDao.getShujutsuCount()}")
                if (shujutsuDao.getShujutsuCount() == 0) {
                    val df = readDpcCsv(context, R.raw.dpc001593946_6)
                    Log.d("tamaiDpc", "shujutsu reading")
                    // DataFrameを格納する変数を宣言
                    val list = df.rows().map { row ->
                        ShujutsuEntity(
                            // nenreiEntityの定義に合わせて列を指定
                            mdcCode = row[0]?.toString() ?: "",
                            bunruiCode = row[1]?.toString() ?: "",
                            taiouCode = row[5]?.toString() ?: "",
                            shujutsu1Name = row[6]?.toString() ?: "",
                            shujutsu2Name = row[7]?.toString() ?: "",
                        )
                    }
                    shujutsuDao.insertAllShujutsu(list)
                }

            } catch (e: Exception) {
                e("DpcRepository", "Excelからのデータベース構築に失敗しました。", e)
            }

        }
    }

    suspend fun checkBunruiExistsInShujutsu(bunruiCode: String): Boolean {
        return shujutsuDao.existsBunruiInShujutsuMaster(bunruiCode)
    }

    suspend fun getShujutsu1CodeByName(shujutsu1Name: String): String? {
        return withContext(Dispatchers.IO) {
            shujutsuDao.getShujutsu1CodeByName(shujutsu1Name)
        }
    }

}

class Shochi1Repository(private val shochi1Dao: Shochi1Dao) {
    suspend fun populateDatabaseFromExcelIfEmpty(context: Context) {
        withContext(Dispatchers.IO) {
            try {
                Log.d("tamaiDpc", "shuochi1 check ${shochi1Dao.getCount()}")
                if (shochi1Dao.getCount() == 0) {
                    val df = readDpcCsv(context, R.raw.dpc001593946_7)
                    Log.d("tamaiDpc", "shochi1 reading")
                    // DataFrameを格納する変数を宣言
                    val list = df.rows().map { row ->
                        Shochi1Entity(
                            // nenreiEntityの定義に合わせて列を指定
                            mdcCode = row[0]?.toString() ?: "",
                            bunruiCode = row[1]?.toString() ?: "",
                            taiouCode = row[2]?.toString() ?: "",
                            shochi1Name = row[5]?.toString() ?: "",
                            shochi1Code = row[6]?.toString() ?: "",
                            shochi2Name = row[7]?.toString() ?: "",
                            shochi2Code = row[8]?.toString() ?: "",
                        )
                    }
                    shochi1Dao.insertAlldata(list)
                }
            } catch (e: Exception) {
                e("DpcRepository", "Excelからのデータベース構築に失敗しました。", e)
            }

        }
    }
    suspend fun getCodeByName(name: String): String? {
        return withContext(Dispatchers.IO) {
            shochi1Dao.getCodeByName(name)
        }
    }
    suspend fun checkBunruiExistsInShochi1(bunruiCode: String): Boolean {
        return shochi1Dao.existsBunruiInMaster(bunruiCode)
    }
    suspend fun getNames(mdcCode: String, bunruiCode: String): List<String> {
        return withContext(Dispatchers.IO) {
            shochi1Dao.getNames(mdcCode, bunruiCode)
        }
    }

}

class Shochi2Repository(private val shochi2Dao: Shochi2Dao){
    suspend fun populateDatabaseFromExcelIfEmpty(context: Context) {
        withContext(Dispatchers.IO) {
            try {
                Log.d("tamaiDpc", "shuochi1 check ${shochi2Dao.getCount()}")
                if (shochi2Dao.getCount() == 0) {
                    val df = readDpcCsv(context, R.raw.dpc001593946_8)
                    Log.d("tamaiDpc", "shochi2 reading")
                    // DataFrameを格納する変数を宣言
                    val list = df.rows().map { row ->
                        Shochi2Entity(
                            // nenreiEntityの定義に合わせて列を指定
                            mdcCode = row[0]?.toString() ?: "",
                            bunruiCode = row[1]?.toString() ?: "",
                            taiouCode = row[2]?.toString() ?: "",
                            shochi1Name = row[4]?.toString() ?: "",
                            shochi1Code = row[5]?.toString() ?: "",
                            shochi2Name = row[6]?.toString() ?: "",
                            shochi2Code = row[7]?.toString() ?: "",
                        )
                    }
                    shochi2Dao.insertAlldata(list)
                }
            } catch (e: Exception) {
                e("DpcRepository", "Excelからのデータベース構築に失敗しました。", e)
            }

        }
    }
    suspend fun getCodeByName(name: String): String? {
        return withContext(Dispatchers.IO) {
            shochi2Dao.getCodeByName(name)
        }
    }
    suspend fun checkBunruiExistsInMaster(bunruiCode: String): Boolean {
        return shochi2Dao.existsBunruiInMaster(bunruiCode)
    }
    suspend fun getNames(mdcCode: String, bunruiCode: String): List<String> {
        return withContext(Dispatchers.IO) {
            shochi2Dao.getNames(mdcCode, bunruiCode)
        }
    }
}

class FukushobyoRepository(private val fukushobyoDao: FukushobyoDao){
    suspend fun populateDatabaseFromExcelIfEmpty(context: Context) {
        withContext(Dispatchers.IO) {
            try {
                Log.d("tamaiDpc", "fukushobyo check ${fukushobyoDao.getCount()}")
                if (fukushobyoDao.getCount() == 0) {
                    val df = readDpcCsv(context, R.raw.dpc001593946_9)
                    Log.d("tamaiDpc", "fukushobyo reading")
                    // DataFrameを格納する変数を宣言
                    val list = df.rows().map { row ->
                        FukushobyoEntity(
                            // nenreiEntityの定義に合わせて列を指定
                            mdcCode = row[0]?.toString() ?: "",
                            bunruiCode = row[1]?.toString() ?: "",
                            taiouCode = row[2]?.toString() ?: "",
                            name = row[4]?.toString() ?: "",
                            code = row[5]?.toString() ?: "",
                        )
                    }
                    fukushobyoDao.insertAlldata(list)
                }
            } catch (e: Exception) {
                e("DpcRepository", "Excelからのデータベース構築に失敗しました。", e)
            }

        }
    }
    suspend fun getCodeByName(name: String): String? {
        return withContext(Dispatchers.IO) {
            fukushobyoDao.getCodeByName(name)
        }
    }
    suspend fun checkBunruiExistsInMaster(bunruiCode: String): Boolean {
        return fukushobyoDao.existsBunruiInMaster(bunruiCode)
    }
    suspend fun getNames(mdcCode: String, bunruiCode: String): List<String> {
        return withContext(Dispatchers.IO) {
            fukushobyoDao.getNames(mdcCode, bunruiCode)
        }
    }
}

class JushodoJcsRepository(private val jushodoJcsDao: JushodoJcsDao){
    suspend fun populateDatabaseFromExcelIfEmpty(context: Context) {
        withContext(Dispatchers.IO) {
            try {
                Log.d("tamaiDpc", "jushodo jcs check ${jushodoJcsDao.getCount()}")
                if (jushodoJcsDao.getCount() == 0) {
                    val df = readDpcCsv(context, R.raw.dpc001593946_10_1)
                    Log.d("tamaiDpc", "jushodo jcs reading")
                    val list = df.rows().map { row ->
                        JushodoJcsEntity(
                            // nenreiEntityの定義に合わせて列を指定
                            mdcCode = row[0]?.toString() ?: "",
                            bunruiCode = row[1]?.toString() ?: "",
                            jokenName = row[3]?.toString() ?:"",
                            joken1Ijo = row[4]?.toString() ?: "",
                            joken1Miman = row[5]?.toString() ?: "",
                            joken1Value = row[6]?.toString() ?: "",
                            joken2Ijo = row[7]?.toString() ?: "",
                            joken2Miman = row[8]?.toString() ?: "",
                            joken2Value = row[9]?.toString() ?: "",

                        )
                    }
                    jushodoJcsDao.insertAlldata(list)
                }
            } catch (e: Exception) {
                e("DpcRepository", "Excelからのデータベース構築に失敗しました。", e)
            }

        }
    }
    suspend fun getJushodoJoken(mdcCode: String, bunruiCode: String): List<JushodoJcsJoken> {
        return withContext(Dispatchers.IO) {
            jushodoJcsDao.getJushodoJoken(mdcCode = mdcCode, bunruiCode = bunruiCode)
        }
    }
    suspend fun checkBunruiExistsInMaster(bunruiCode: String): Boolean {
        return jushodoJcsDao.existsBunruiInMaster(bunruiCode)
    }
    suspend fun getNames(mdcCode: String, bunruiCode: String): List<String> {
        return withContext(Dispatchers.IO) {
            jushodoJcsDao.getNames(mdcCode, bunruiCode)
        }
    }
}


class NenreiRepository(private val nenreiDao: NenreiDao) {
    suspend fun getNenreiJoken(mdcCode: String, bunruiCode: String): NenreiJoken? {
        return withContext(Dispatchers.IO) {
            nenreiDao.getNenreiJoken(mdcCode, bunruiCode)
        }
    }

}

class JushodoShujutsuRepository(private val jushodoShujutsuDao: JushodoShujutsuDao) {
    suspend fun populateDatabaseFromExcelIfEmpty(context: Context) {
        withContext(Dispatchers.IO) {
            try {
                Log.d("tamaiDpc", "jushodo jcs check ${jushodoShujutsuDao.getCount()}")
                if (jushodoShujutsuDao.getCount() == 0) {
                    val df = readDpcCsv(context, R.raw.dpc001593946_10_1)
                    Log.d("tamaiDpc", "jushodo jcs reading")
                    val list = df.rows().map { row ->
                        JushodoShujutsuEntity(
                            // nenreiEntityの定義に合わせて列を指定
                            mdcCode = row[0]?.toString() ?: "",
                            bunruiCode = row[1]?.toString() ?: "",
                            jokenName = row[3]?.toString() ?:"",
                            joken1Name = row[4]?.toString() ?: "",
                            joken1Code = row[5]?.toString() ?: "",
                            joken2Name = row[6]?.toString() ?: "",
                            joken2Code = row[7]?.toString() ?: "",
                            )
                    }
                    jushodoShujutsuDao.insertAlldata(list)
                }
            } catch (e: Exception) {
                e("DpcRepository", "Excelからのデータベース構築に失敗しました。", e)
            }

        }
    }
    suspend fun getJushodoJoken(mdcCode: String, bunruiCode: String): List<JushodoShujutsuJoken> {
        return withContext(Dispatchers.IO) {
            jushodoShujutsuDao.getJushodoJoken(mdcCode = mdcCode, bunruiCode = bunruiCode)
        }
    }
    suspend fun checkBunruiExistsInMaster(mdcCode: String?, bunruiCode: String): Boolean {
        Log.d("dpcJushodoShujutsu", "jushodoShujutsuDataExists $mdcCode $bunruiCode")
        return jushodoShujutsuDao.existsBunruiInMaster(mdcCode, bunruiCode)
    }
    suspend fun getNames(mdcCode: String, bunruiCode: String): List<String> {
        return withContext(Dispatchers.IO) {
            jushodoShujutsuDao.getNames(mdcCode, bunruiCode)
        }
    }
    }

class JushodoStrokeRepository(private val jushodoStrokeDao: JushodoStrokeDao) {
    suspend fun populateDatabaseFromExcelIfEmpty(context: Context) {
        withContext(Dispatchers.IO) {
            try {
                Log.d("tamaiDpc", "jushodo Stroke check ${jushodoStrokeDao.getCount()}")
                if (jushodoStrokeDao.getCount() == 0) {
                    val df = readDpcCsv(context, R.raw.dpc001593946_10_1)
                    Log.d("tamaiDpc", "jushodo jcs reading")
                    val list = df.rows().map { row ->
                        JushodoStrokeEntity(
                            // nenreiEntityの定義に合わせて列を指定
                            mdcCode = row[0]?.toString() ?: "",
                            bunruiCode = row[1]?.toString() ?: "",
                            code = row[2]?.toString() ?: "",
                            label = row[5]?.toString() ?:"",
                            joken1Name = row[7]?.toString() ?: "",

                        )
                    }
                    jushodoStrokeDao.insertAlldata(list)
                }
            } catch (e: Exception) {
                e("DpcRepository", "Excelからのデータベース構築に失敗しました。", e)
            }

        }
    }
    suspend fun getJushodoJoken(mdcCode: String, bunruiCode: String): List<JushodoStrokeJoken> {
        return withContext(Dispatchers.IO) {
            jushodoStrokeDao.getJushodoJoken(mdcCode = mdcCode, bunruiCode = bunruiCode)
        }
    }
    suspend fun checkBunruiExistsInMaster(mdcCode: String?, bunruiCode: String): Boolean {
        Log.d("dpcJushodoShujutsu", "jushodoShujutsuDataExists $mdcCode $bunruiCode")
        return jushodoStrokeDao.existsBunruiInMaster(mdcCode, bunruiCode)
    }
    suspend fun getNames(mdcCode: String, bunruiCode: String): List<String> {
        return withContext(Dispatchers.IO) {
            jushodoStrokeDao.getNames(mdcCode, bunruiCode)
        }
    }
    suspend fun getCodeByName(name: String): String? {
        return withContext(Dispatchers.IO) {
            jushodoStrokeDao.getCodeByName(name)
        }
    }
}

/**
 * 指定されたリソースIDのCSVファイルを読み込み、DataFrameとして返す共通関数
 * @param context
コンテキスト
 * @param resourceId 読み込むCSVファイルのリソースID (例: R.raw.dpc001593946_6)
 * @return 読み込まれたDataFrame
 */
private fun readDpcCsv(context: Context, resourceId: Int): DataFrame<*> {
    val headerNames = (1..21).map { it.toString() }
    val columnTypes: Map<String, ColType> = headerNames.associateWith { ColType.String }
    val inputStream: InputStream = context.resources.openRawResource(resourceId)

    inputStream.use { stream ->
        return DataFrame.readCSV(
            stream = stream,
            header = headerNames,            charset = Charset.forName("Shift-JIS"),
            colTypes = columnTypes,
            skipLines = 2,
            parserOptions = ParserOptions()
        )
    }
}


