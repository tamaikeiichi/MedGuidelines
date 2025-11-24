package com.keiichi.medguidelines.ui.screen

import android.content.Context
import androidx.compose.foundation.gestures.forEach
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import com.keiichi.medguidelines.R
import com.keiichi.medguidelines.ui.component.MedGuidelinesScaffold
import com.keiichi.medguidelines.ui.component.TextAndUrl
import com.keiichi.medguidelines.ui.component.TitleTopAppBar
import org.apache.poi.ss.usermodel.DateUtil
import org.dhatim.fastexcel.reader.Cell
import org.dhatim.fastexcel.reader.ReadableWorkbook
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.emptyDataFrame
import org.jetbrains.kotlinx.dataframe.api.schema
import org.jetbrains.kotlinx.dataframe.io.readExcel
import java.io.InputStream
import java.time.format.DateTimeFormatter

data class DpcDataSheets(
    val mdc: DataFrame<*>? = null,
    val bunrui: DataFrame<*>? = null,
    val byotai: DataFrame<*>? = null,
    val icd: DataFrame<*>? = null,
    val nenrei: DataFrame<*>? = null,
    val shujutu: DataFrame<*>? = null,
    val shochi1: DataFrame<*>? = null,
    val shochi2: DataFrame<*>? = null,
    val fukubyomei: DataFrame<*>? = null,
    val jushodoJcs: DataFrame<*>? = null,
    val jushodoShujutu: DataFrame<*>? = null,
    val jushodoJushou: DataFrame<*>? = null,
    val jushodoRankin: DataFrame<*>? = null,
    val shidangunBunruiTensu: DataFrame<*>? = null,
    val henkanTable: DataFrame<*>? = null,
    val dekidaka: DataFrame<*>? = null,
    val ccpm: DataFrame<*>? = null,
    // Add more properties here for other sheets
)
@Composable
fun DpcScreen(navController: NavHostController) {
    val context = LocalContext.current
    val inputStream = context.resources.openRawResource(R.raw.dpc001348055)
    val df = sample(inputStream)
//    val df = remember {
//        loadAllDpcData(context)
//    }
    MedGuidelinesScaffold (
        topBar = {
            TitleTopAppBar(
                title = R.string.dpcTitle,
                navController = navController,
                references = listOf(
                    TextAndUrl(R.string.space, R.string.space)
                )
            )
        },
    ){
        Column() {
            Text(df.toString())
//            Text(df.mdc?.schema().toString())
//            Text(df.bunrui?.schema().toString())
            Text("test")
        }
    }
}

private fun loadAllDpcData(context: Context): DpcDataSheets {
    val mdc = DataFrame.readExcel(context.resources.openRawResource(R.raw.dpc001348055), "１）ＭＤＣ名称")
    val bunrui = DataFrame.readExcel(context.resources.openRawResource(R.raw.dpc001348055), "２）分類名称")
    //val byotai = DataFrame.readExcel(context.resources.openRawResource(R.raw.dpc001348055), "３）病態等分類")
//    val icd = DataFrame.readExcel(context.resources.openRawResource(R.raw.dpc001348055), "４）ＩＣＤ")
//    val nenrei = DataFrame.readExcel(context.resources.openRawResource(R.raw.dpc001348055), "５）年齢、出生時体重等")
//    val shujutu = DataFrame.readExcel(context.resources.openRawResource(R.raw.dpc001348055), "６）手術 ")
//    val shochi1 = DataFrame.readExcel(context.resources.openRawResource(R.raw.dpc001348055), "７）手術・処置等１")
//    val shochi2 = DataFrame.readExcel(context.resources.openRawResource(R.raw.dpc001348055), "８）手術・処置等２")
//    val fukubyomei = DataFrame.readExcel(context.resources.openRawResource(R.raw.dpc001348055), "９）定義副傷病名")
//    val jushodoJcs = DataFrame.readExcel(context.resources.openRawResource(R.raw.dpc001348055), "10－1）重症度等（ＪＣＳ等）")
//    val jushodoShujutu = DataFrame.readExcel(context.resources.openRawResource(R.raw.dpc001348055), "10－2）重症度等（手術等）")
//    val jushodoJushou = DataFrame.readExcel(context.resources.openRawResource(R.raw.dpc001348055), "10－3）重症度等（重症・軽症）")
//    val jushodoRankin = DataFrame.readExcel(context.resources.openRawResource(R.raw.dpc001348055), "10－4）重症度等（発症前Rankin Scale等）")
//    val shidangunBunruiTensu = DataFrame.readExcel(context.resources.openRawResource(R.raw.dpc001348055), "11）診断群分類点数表")
//    val henkanTable = DataFrame.readExcel(context.resources.openRawResource(R.raw.dpc001348055), "12）変換テーブル ")
//    val dekidaka = DataFrame.readExcel(context.resources.openRawResource(R.raw.dpc001348055), "13)出来高算定手術等コード ")
//    val ccpm = DataFrame.readExcel(context.resources.openRawResource(R.raw.dpc001348055), "14）CCPM対応")

    return DpcDataSheets(
        mdc = mdc,
        bunrui = bunrui,
//        byotai = byotai,
//        icd = icd,
//        nenrei = nenrei,
//        shujutu = shujutu,
//        shochi1 = shochi1,
//        shochi2 = shochi2,
//        fukubyomei = fukubyomei,
//        jushodoJcs = jushodoJcs,
//        jushodoShujutu = jushodoShujutu,
//        jushodoJushou = jushodoJushou,
//        jushodoRankin = jushodoRankin,
//        shidangunBunruiTensu = shidangunBunruiTensu,
//        henkanTable = henkanTable,
//        dekidaka = dekidaka,
//        ccpm = ccpm
    )
}

fun sample(inputStream: InputStream) : DataFrame<*> {
    val result = DataFrame<*> // Use a StringBuilder for efficiency

    // The 'use' block can return a value. The value of its last expression is returned.
    ReadableWorkbook(inputStream).use { wb ->
        val sheet = wb.firstSheet ?: return emptyDataFrame()

        sheet.openStream().use { rows ->
            rows.forEach { r ->
                val num = r.getCellAsNumber(0).orElse(null)
                val str = r.getCellAsString(1).orElse(null)
                val date = r.getCellAsDate(2).orElse(null)

                // Append the formatted string to the StringBuilder
                result.appendLine("Row ${r.rowNum}: Number=$num, String='$str', Date=$date")
            }
        }
    }
    return result.toDataFrame() // Return the final built string
}


// 日付判定は自前で作る必要がある
private fun isDateCell(cell: Cell): Boolean {
    val dataFormatId = cell.dataFormatId ?: return false
    // 14..22は日付形式のID
    // 参考: https://www.iso.org/standard/71691.html
    if (dataFormatId in 14..22) {
        return true
    }
    val dataFormatString = cell.dataFormatString ?: return false
    // POIさんの関数を使わせて頂いております
    return DateUtil.isADateFormat(dataFormatId, dataFormatString)
}

@Preview(showBackground = true)
@Composable
fun DpcScreenPreview() {
    DpcScreen(navController = NavHostController(LocalContext.current))
}
