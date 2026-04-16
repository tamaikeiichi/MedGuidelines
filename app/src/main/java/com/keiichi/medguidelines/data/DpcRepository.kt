// C:/Users/tamaikeiichi/StudioProjects/MedGuidelines/app/src/main/java/com/keiichi/medguidelines/data/DpcRepository.kt

package com.keiichi.medguidelines.data

import android.content.Context
import android.util.Log
import android.util.Log.e
import com.keiichi.medguidelines.R
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

/**
 * DPCマスターのリソースIDを一括管理するオブジェクト
 * ファイル名（プレフィックス）が変わった場合はここを修正する
 */
object DpcResources {
    val mdc = R.raw.dpc001691187_1
    val bunrui = R.raw.dpc001691187_2
    val byotai = R.raw.dpc001691187_3
    val icd = R.raw.dpc001691187_4
    val nenrei = R.raw.dpc001691187_5
    val shujutsu = R.raw.dpc001691187_6
    val shochi1 = R.raw.dpc001691187_7
    val shochi2 = R.raw.dpc001691187_8
    val fukushobyo = R.raw.dpc001691187_9
    val jushodoJcs = R.raw.dpc001691187_10_1
    val jushodoStroke = R.raw.dpc001691187_10_4
    val tensuhyo = R.raw.dpc001691187_11
}

class DpcRepository(private val dpcDao: DpcDao) {

    fun searchIcd(query: String) = dpcDao.searchIcd("%$query%")

    suspend fun searchIcdByMcdAndBunrui(mdcCode: String?, bunruiCode: String?) =
        dpcDao.searchIcdByMcdAndBunrui(mdcCode, bunruiCode)

    fun searchIcdMulti(word1: String, word2: String, word3: String, word4: String) =
        dpcDao.searchIcdMulti(word1, word2, word3, word4)

    suspend fun checkMdcAndBunruiExist(mdcCode: String, bunruiCode: String): Boolean {
        return withContext(Dispatchers.IO) {
            val mdcExists = dpcDao.getUniqueMdc(mdcCode).isNotEmpty()
            val bunruiExists = dpcDao.getUniqueBunrui(bunruiCode).isNotEmpty()
            mdcExists && bunruiExists
        }
    }

    suspend fun checkMdcAndBunruiExistsInNenrei(mdcCode: String, bunruiCode: String): Boolean {
        return dpcDao.existsMdcAndBunruiInNenreiMaster(mdcCode, bunruiCode)
    }

    suspend fun getByotaiNames(mdcCode: String, bunruiCode: String): List<ByotaiOptionEntity> {
        return withContext(Dispatchers.IO) {
            dpcDao.getByotaiNames(mdcCode, bunruiCode)
        }
    }

    suspend fun getBunruiName(mdcCode: String?, bunruiCode: String?): String? {
        return withContext(Dispatchers.IO) {
            dpcDao.getBunruiNames(mdcCode, bunruiCode)
        }
    }

    suspend fun getNenreiJoken1Ijo(mdcCode: String, bunruiCode: String): String = withContext(Dispatchers.IO) { dpcDao.getNenreiJoken1Ijo(mdcCode, bunruiCode) }
    suspend fun getNenreiJoken1Miman(mdcCode: String, bunruiCode: String): String = withContext(Dispatchers.IO) { dpcDao.getNenreiJoken1Miman(mdcCode, bunruiCode) }
    suspend fun getNenreiJoken1Value(mdcCode: String, bunruiCode: String): String = withContext(Dispatchers.IO) { dpcDao.getNenreiJoken1Value(mdcCode, bunruiCode) }
    suspend fun getNenreiJoken2Ijo(mdcCode: String, bunruiCode: String): String = withContext(Dispatchers.IO) { dpcDao.getNenreiJoken2Ijo(mdcCode, bunruiCode) }
    suspend fun getNenreiJoken2Miman(mdcCode: String, bunruiCode: String): String = withContext(Dispatchers.IO) { dpcDao.getNenreiJoken2Miman(mdcCode, bunruiCode) }
    suspend fun getNenreiJoken2Value(mdcCode: String, bunruiCode: String): String = withContext(Dispatchers.IO) { dpcDao.getNenreiJoken2Value(mdcCode, bunruiCode) }
    suspend fun getNenreiJoken3Ijo(mdcCode: String, bunruiCode: String): String = withContext(Dispatchers.IO) { dpcDao.getNenreiJoken3Ijo(mdcCode, bunruiCode) }
    suspend fun getNenreiJoken3Miman(mdcCode: String, bunruiCode: String): String = withContext(Dispatchers.IO) { dpcDao.getNenreiJoken3Miman(mdcCode, bunruiCode) }
    suspend fun getNenreiJoken3Value(mdcCode: String, bunruiCode: String): String = withContext(Dispatchers.IO) { dpcDao.getNenreiJoken3Value(mdcCode, bunruiCode) }
    suspend fun getNenreiJoken4Ijo(mdcCode: String, bunruiCode: String): String = withContext(Dispatchers.IO) { dpcDao.getNenreiJoken4Ijo(mdcCode, bunruiCode) }
    suspend fun getNenreiJoken4Miman(mdcCode: String, bunruiCode: String): String = withContext(Dispatchers.IO) { dpcDao.getNenreiJoken4Miman(mdcCode, bunruiCode) }
    suspend fun getNenreiJoken4Value(mdcCode: String, bunruiCode: String): String = withContext(Dispatchers.IO) { dpcDao.getNenreiJoken4Value(mdcCode, bunruiCode) }
    suspend fun getNenreiJoken5Ijo(mdcCode: String, bunruiCode: String): String = withContext(Dispatchers.IO) { dpcDao.getNenreiJoken5Ijo(mdcCode, bunruiCode) }
    suspend fun getNenreiJoken5Miman(mdcCode: String, bunruiCode: String): String = withContext(Dispatchers.IO) { dpcDao.getNenreiJoken5Miman(mdcCode, bunruiCode) }
    suspend fun getNenreiJoken5Value(mdcCode: String, bunruiCode: String): String = withContext(Dispatchers.IO) { dpcDao.getNenreiJoken5Value(mdcCode, bunruiCode) }

    suspend fun getByotaiCodeByName(byotaiName: String): String? {
        return withContext(Dispatchers.IO) {
            dpcDao.getByotaiCodeByName(byotaiName)
        }
    }

    suspend fun populateDatabaseFromCsvIfEmpty(context: Context) {
        withContext(Dispatchers.IO) {
            try {
                // ICD
                if (dpcDao.getIcdCount() == 0) {
                    Log.d("tamaiDpc", "icd reading")
                    val icdDf = readDpcCsv(context, DpcResources.icd)
                    val icdList = icdDf.rows().map { row ->
                        IcdEntity(
                            mdcCode = row[0]?.toString() ?: "",
                            bunruiCode = row[1]?.toString() ?: "",
                            icdName = row[2]?.toString() ?: "",
                            icdCode = row[3]?.toString() ?: "",
                            normalizedIcdName = normalizeTextForSearch(row[2]?.toString() ?: "")
                        )
                    }
                    dpcDao.insertAllIcd(icdList)
                }

                // Byotai
                if (dpcDao.getByotaiCount() == 0) {
                    Log.d("tamaiDpc", "byotai reading")
                    val byotaiDf = readDpcCsv(context, DpcResources.byotai)
                    val byotaiList = byotaiDf.rows().map { row ->
                        ByotaiEntity(
                            mdcCode = row[0]?.toString() ?: "",
                            bunruiCode = row[1]?.toString() ?: "",
                            byotaiCode = row[2]?.toString() ?: "",
                            nenreiIjo = row[4]?.toString() ?: "",
                            nenreiMiman = row[5]?.toString() ?: "",
                            byotaiKubunMeisho = row[7]?.toString() ?: ""
                        )
                    }
                    dpcDao.insertAllByotai(byotaiList)
                }

                // Bunrui
                if (dpcDao.getBunruiCount() == 0) {
                    Log.d("tamaiDpc", "bunrui reading")
                    val bunruiDf = readDpcCsv(context, DpcResources.bunrui)
                    val bunruiList = bunruiDf.rows().map { row ->
                        BunruiEntity(
                            mdcCode = row[0]?.toString() ?: "",
                            bunruiCode = row[1]?.toString() ?: "",
                            bunruiName = row[2]?.toString() ?: "",
                            normalizedBunruiName = normalizeTextForSearch(row[2]?.toString() ?: "")
                        )
                    }
                    dpcDao.insertAllBunrui(bunruiList)
                }

                // Mdc
                if (dpcDao.getMdcCount() == 0) {
                    Log.d("tamaiDpc", "mdc reading")
                    val mdcDf = readDpcCsv(context, DpcResources.mdc)
                    val mdcList = mdcDf.rows().map { row ->
                        MdcEntity(
                            mdcCode = row[0]?.toString() ?: "",
                            mdcName = row[1]?.toString() ?: ""
                        )
                    }
                    dpcDao.insertAllMdc(mdcList)
                }

                // Nenrei
                if (dpcDao.getNenreiCount() == 0) {
                    Log.d("tamaiDpc", "nenrei reading")
                    val nenreiDf = readDpcCsv(context, DpcResources.nenrei)
                    val nenreiList = nenreiDf.rows().map { row ->
                        NenreiEntity(
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
                Log.e("DpcRepository", "CSVからのデータベース構築に失敗しました。", e)
            }
        }
    }

    fun searchBunrui(query: String) = dpcDao.searchBunrui("%$query%")
}

class BunruiRepository(private val bunruiDao: BunruiDao) {
    suspend fun populateDatabaseFromCsvIfEmpty(context: Context) {
        withContext(Dispatchers.IO) {
            try {
                if (bunruiDao.getCount() == 0) {
                    val df = readDpcCsv(context, DpcResources.bunrui)
                    val list = df.rows().map { row ->
                        val rawMdcCode = row[0]?.toString() ?: ""
                        BunruiEntity(
                            mdcCode = if (rawMdcCode.length == 1) "0$rawMdcCode" else rawMdcCode,
                            bunruiCode = row[1]?.toString() ?: "",
                            bunruiName = row[2]?.toString() ?: "",
                            normalizedBunruiName = normalizeTextForSearch(row[2]?.toString() ?: "")
                        )
                    }
                    bunruiDao.insertAll(list)
                }
            } catch (e: Exception) {
                e("DpcRepository", "CSVからのデータベース構築に失敗しました。", e)
            }
        }
    }
    fun searchBunruiMulti(word1: String, word2: String, word3: String, word4: String) =
        bunruiDao.searchBunruiMulti(word1, word2, word3, word4)
}

class ShujutsuRepository(private val shujutsuDao: ShujutsuDao) {
    suspend fun getShujutsuNames(mdcCode: String, bunruiCode: String): List<String> {
        return withContext(Dispatchers.IO) { shujutsuDao.getNames(mdcCode, bunruiCode) }
    }

    suspend fun populateDatabaseFromCsvIfEmpty(context: Context) {
        withContext(Dispatchers.IO) {
            try {
                if (shujutsuDao.getShujutsuCount() == 0) {
                    val df = readDpcCsv(context, DpcResources.shujutsu)
                    val list = df.rows().map { row ->
                        val rawTaiouCode = row[5]?.toString() ?: ""
                        ShujutsuEntity(
                            mdcCode = row[0]?.toString() ?: "",
                            bunruiCode = row[1]?.toString() ?: "",
                            taiouCode = if (rawTaiouCode.length == 1) "0$rawTaiouCode" else rawTaiouCode,
                            shujutsu1Name = row[6]?.toString() ?: "",
                            shujutsu2Name = row[7]?.toString() ?: "",
                        )
                    }
                    shujutsuDao.insertAllShujutsu(list)
                }
            } catch (e: Exception) {
                e("DpcRepository", "CSVからのデータベース構築に失敗しました。", e)
            }
        }
    }

    suspend fun checkMdcAndBunruiExistsInShujutsu(mdcCode: String, bunruiCode: String): Boolean {
        return shujutsuDao.existsMdcAndBunruiInShujutsuMaster(mdcCode, bunruiCode)
    }

    suspend fun getShujutsu1CodeByName(shujutsu1Name: String, mdcCode: String?, bunruiCode: String?): String? {
        return withContext(Dispatchers.IO) {
            shujutsuDao.getShujutsu1CodeByName(shujutsu1Name, mdcCode = mdcCode, bunruiCode = bunruiCode)
        }
    }
}

class Shochi1Repository(private val shochi1Dao: Shochi1Dao) {
    suspend fun populateDatabaseFromCsvIfEmpty(context: Context) {
        withContext(Dispatchers.IO) {
            try {
                if (shochi1Dao.getCount() == 0) {
                    val df = readDpcCsv(context, DpcResources.shochi1)
                    val list = df.rows().map { row ->
                        Shochi1Entity(
                            mdcCode = row[0]?.toString() ?: "",
                            bunruiCode = row[1]?.toString() ?: "",
                            code = row[2]?.toString() ?: "",
                            shochi1Name = row[5]?.toString() ?: "",
                            shochi1Code = row[6]?.toString() ?: "",
                            shochi2Name = row[7]?.toString() ?: "",
                            shochi2Code = row[8]?.toString() ?: "",
                        )
                    }
                    shochi1Dao.insertAlldata(list)
                }
            } catch (e: Exception) {
                e("DpcRepository", "CSVからのデータベース構築に失敗しました。", e)
            }
        }
    }
    suspend fun getCodeByName(name: String): String? = withContext(Dispatchers.IO) { shochi1Dao.getCodeByName(name) }
    suspend fun checkMdcAndBunruiExistsInShochi1(mdcCode: String, bunruiCode: String): Boolean = shochi1Dao.existsMdcAndBunruiInMaster(mdcCode, bunruiCode)
    suspend fun getNames(mdcCode: String, bunruiCode: String): List<Shochi1Joken> = withContext(Dispatchers.IO) {
        listOf(Shochi1Joken(code = "0", shochi1Name = "なし")) + shochi1Dao.getNames(mdcCode, bunruiCode)
    }
}

class Shochi2Repository(private val shochi2Dao: Shochi2Dao){
    suspend fun populateDatabaseFromCsvIfEmpty(context: Context) {
        withContext(Dispatchers.IO) {
            try {
                if (shochi2Dao.getCount() == 0) {
                    val df = readDpcCsv(context, DpcResources.shochi2)
                    val list = df.rows().map { row ->
                        Shochi2Entity(
                            mdcCode = row[0]?.toString() ?: "",
                            bunruiCode = row[1]?.toString() ?: "",
                            code = row[2]?.toString() ?: "",
                            shochi1Name = row[4]?.toString() ?: "",
                            shochi1Code = row[5]?.toString() ?: "",
                            shochi2Name = row[6]?.toString() ?: "",
                            shochi2Code = row[7]?.toString() ?: "",
                        )
                    }
                    shochi2Dao.insertAlldata(list)
                }
            } catch (e: Exception) {
                e("DpcRepository", "CSVからのデータベース構築に失敗しました。", e)
            }
        }
    }
    suspend fun getCodeByName(name: String): String? = withContext(Dispatchers.IO) { shochi2Dao.getCodeByName(name) }
    suspend fun checkMdcAndBunruiExistsInMaster(mdcCode: String, bunruiCode: String): Boolean = shochi2Dao.existsBunruiInMaster(mdcCode, bunruiCode)
    suspend fun getNames(mdcCode: String, bunruiCode: String): List<Shochi2Joken> = withContext(Dispatchers.IO) {
        listOf(Shochi2Joken(code = "0", shochi1Name = "なし")) + shochi2Dao.getNames(mdcCode, bunruiCode)
    }
}

class FukushobyoRepository(private val fukushobyoDao: FukushobyoDao){
    suspend fun populateDatabaseFromExcelIfEmpty(context: Context) {
        withContext(Dispatchers.IO) {
            try {
                if (fukushobyoDao.getCount() == 0) {
                    val df = readDpcCsv(context, DpcResources.fukushobyo)
                    val list = df.rows().map { row ->
                        FukushobyoEntity(
                            mdcCode = row[0]?.toString() ?: "",
                            bunruiCode = row[1]?.toString() ?: "",
                            code = row[2]?.toString() ?: "",
                            name = row[4]?.toString() ?: "",
                            IcdCode = row[5]?.toString() ?: "",
                        )
                    }
                    fukushobyoDao.insertAlldata(list)
                }
            } catch (e: Exception) {
                e("DpcRepository", "CSVからのデータベース構築に失敗しました。", e)
            }
        }
    }
    suspend fun getCodeByName(name: String): String? = withContext(Dispatchers.IO) { fukushobyoDao.getCodeByName(name) }
    suspend fun checkMdcAndBunruiExistsInMaster(mdcCode: String, bunruiCode: String): Boolean = fukushobyoDao.existsMdcAndBunruiInMaster(mdcCode, bunruiCode)
    suspend fun getNames(mdcCode: String, bunruiCode: String): List<FukushobyoJoken> = withContext(Dispatchers.IO) {
        listOf(FukushobyoJoken(code = "0", name = "なし")) + fukushobyoDao.getNames(mdcCode, bunruiCode)
    }
}

class JushodoJcsRepository(private val jushodoJcsDao: JushodoJcsDao){
    suspend fun populateDatabaseFromExcelIfEmpty(context: Context) {
        withContext(Dispatchers.IO) {
            try {
                if (jushodoJcsDao.getCount() == 0) {
                    val df = readDpcCsv(context, DpcResources.jushodoJcs)
                    val list = df.rows().map { row ->
                        JushodoJcsEntity(
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
                e("DpcRepository", "CSVからのデータベース構築に失敗しました。", e)
            }
        }
    }
    suspend fun getJushodoJoken(mdcCode: String, bunruiCode: String): List<JushodoJcsJoken> = withContext(Dispatchers.IO) { jushodoJcsDao.getJushodoJoken(mdcCode, bunruiCode) }
    suspend fun checkMdcAndBunruiExistsInMaster(mdcCode: String, bunruiCode: String): Boolean = jushodoJcsDao.existsMdcAndBunruiInMaster(mdcCode, bunruiCode)
    suspend fun getNames(mdcCode: String, bunruiCode: String): List<String> = withContext(Dispatchers.IO) { jushodoJcsDao.getNames(mdcCode, bunruiCode) }
}

class NenreiRepository(private val nenreiDao: NenreiDao) {
    suspend fun getNenreiJoken(mdcCode: String, bunruiCode: String): NenreiJoken? = withContext(Dispatchers.IO) { nenreiDao.getNenreiJoken(mdcCode, bunruiCode) }
}

class JushodoShujutsuRepository(private val jushodoShujutsuDao: JushodoShujutsuDao) {
    suspend fun populateDatabaseFromExcelIfEmpty(context: Context) {
        withContext(Dispatchers.IO) {
            try {
                if (jushodoShujutsuDao.getCount() == 0) {
                    val df = readDpcCsv(context, DpcResources.jushodoJcs) // 同じファイルを使用
                    val list = df.rows().map { row ->
                        JushodoShujutsuEntity(
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
                e("DpcRepository", "CSVからのデータベース構築に失敗しました。", e)
            }
        }
    }
    suspend fun getJushodoJoken(mdcCode: String, bunruiCode: String): List<JushodoShujutsuJoken> = withContext(Dispatchers.IO) { jushodoShujutsuDao.getJushodoJoken(mdcCode, bunruiCode) }
    suspend fun checkBunruiExistsInMaster(mdcCode: String?, bunruiCode: String): Boolean = jushodoShujutsuDao.existsBunruiInMaster(mdcCode, bunruiCode)
    suspend fun getNames(mdcCode: String, bunruiCode: String): List<String> = withContext(Dispatchers.IO) { jushodoShujutsuDao.getNames(mdcCode, bunruiCode) }
}

class JushodoStrokeRepository(private val jushodoStrokeDao: JushodoStrokeDao) {
    suspend fun populateDatabaseFromExcelIfEmpty(context: Context) {
        withContext(Dispatchers.IO) {
            try {
                if (jushodoStrokeDao.getCount() == 0) {
                    val df = readDpcCsv(context, DpcResources.jushodoStroke)
                    val list = df.rows().map { row ->
                        JushodoStrokeEntity(
                            mdcCode = row[0]?.toString() ?: "",
                            bunruiCode = row[1]?.toString() ?: "",
                            code = row[2]?.toString() ?: "",
                            label = row[5]?.toString() ?:"",
                            kubun = row[6]?.toString() ?:"",
                            joken1Name = row[7]?.toString() ?: "",
                        )
                    }
                    jushodoStrokeDao.insertAlldata(list)
                }
            } catch (e: Exception) {
                e("DpcRepository", "CSVからのデータベース構築に失敗しました。", e)
            }
        }
    }
    suspend fun getJushodoJoken(mdcCode: String, bunruiCode: String): List<JushodoStrokeJoken> = withContext(Dispatchers.IO) { jushodoStrokeDao.getJushodoJoken(mdcCode, bunruiCode) }
    suspend fun checkMdcAndBunruiExistInMaster(mdcCode: String?, bunruiCode: String): Boolean = jushodoStrokeDao.existsBunruiInMaster(mdcCode, bunruiCode)
    suspend fun getNames(mdcCode: String, bunruiCode: String): List<String> = withContext(Dispatchers.IO) { jushodoStrokeDao.getNames(mdcCode, bunruiCode) }
    suspend fun getCodeByName(name: String): String? = withContext(Dispatchers.IO) { jushodoStrokeDao.getCodeByName(name) }
}

class ShindangunBunruiTensuhyoRepository(private val shindangunBunruiTensuhyoDao: ShindangunBunruiTensuhyoDao){
    suspend fun populateDatabaseFromCsvIfEmpty(context: Context) {
        withContext(Dispatchers.IO) {
            try {
                if (shindangunBunruiTensuhyoDao.getCount() == 0) {
                    val df = readDpcCsv(context, DpcResources.tensuhyo, skipLines = 4)
                    val list = df.rows().map { row ->
                        ShindangunBunruiTensuhyoEntity(
                            code = row[1]?.toString() ?: "",
                            name = row[2]?.toString() ?: "",
                            nyuinBiI = row[8]?.toString() ?: "",
                            nyuinBiII = row[9]?.toString() ?: "",
                            nyuinBiIII = row[10]?.toString() ?: "",
                            nyuinKikanI = row[11]?.toString() ?: "",
                            nyuinKikanII = row[12]?.toString() ?: "",
                            nyuinKikanIII = row[13]?.toString() ?: "",
                        )
                    }
                    shindangunBunruiTensuhyoDao.insertAlldata(list)
                }
            } catch (e: Exception) {
                e("DpcRepository", "CSVからのデータベース構築に失敗しました。", e)
            }
        }
    }
    suspend fun getCodeByName(name: String): String? = withContext(Dispatchers.IO) { shindangunBunruiTensuhyoDao.getCodeByName(name) }
    suspend fun checkCodeExistsInMaster(code: String): Boolean = shindangunBunruiTensuhyoDao.existsCodeInMaster(code)
    suspend fun getNames(code: String): List<ShindangunBunruiTensuhyoJoken> = withContext(Dispatchers.IO) { shindangunBunruiTensuhyoDao.getNames(code) }
    suspend fun getFirstThreeRows(): List<String> = shindangunBunruiTensuhyoDao.getFirstThreeRows()
}

private fun readDpcCsv(
    context: Context,
    resourceId: Int,
    skipLines: Int = 2,
): DataFrame<*> {
    val headerNames = (1..21).map { it.toString() }
    val columnTypes: Map<String, ColType> = headerNames.associateWith { ColType.String }
    val inputStream: InputStream = context.resources.openRawResource(resourceId)

    inputStream.use { stream ->
        return DataFrame.readCSV(
            stream = stream,
            header = headerNames,
            charset = Charset.forName("Shift-JIS"),
            colTypes = columnTypes,
            skipLines = skipLines,
            parserOptions = ParserOptions()
        )
    }
}
