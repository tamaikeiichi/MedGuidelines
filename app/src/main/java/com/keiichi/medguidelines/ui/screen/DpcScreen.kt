package com.keiichi.medguidelines.ui.screen

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.keiichi.medguidelines.R
import com.keiichi.medguidelines.ui.component.MedGuidelinesScaffold
import com.keiichi.medguidelines.ui.component.TextAndUrl
import com.keiichi.medguidelines.ui.component.TitleTopAppBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.io.NameRepairStrategy
import org.jetbrains.kotlinx.dataframe.io.readExcel

// 読み込んだDataFrameを保持するためのデータクラス
data class DpcDataSheets(
    var mdc: DataFrame<*>? = null,
    var bunrui: DataFrame<*>? = null,
    var byotai: DataFrame<*>? = null,
    var icd: DataFrame<*>? = null,
    var nenrei: DataFrame<*>? = null,
    var shujutu: DataFrame<*>? = null,
    var shochi1: DataFrame<*>? = null,
    var shochi2: DataFrame<*>? = null,
    var fukubyomei: DataFrame<*>? = null,
    var jushodoJcs: DataFrame<*>? = null,
    var jushodoShujutu: DataFrame<*>? = null,
    var jushodoJushou: DataFrame<*>? = null,
    var jushodoRankin: DataFrame<*>? = null
)

/**
 * データ読み込みとUIの接続を行う "スマート" なコンポーザブル
 */
@Composable
fun DpcScreen(navController: NavHostController) {
    val context = LocalContext.current

    // UIの状態を管理するためのState変数
    var df by remember { mutableStateOf(DpcDataSheets()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var loadingMessage by remember { mutableStateOf("Starting to load data...") }

    // Composableが最初に表示されたときに一度だけ実行される副作用
    LaunchedEffect(Unit) {
        isLoading = true
        try {
            val loadedMdc =
                withContext(Dispatchers.IO) {
                    loadDpcData(context, "１）ＭＤＣ名称")
                }
            val loadedBunrui =
                withContext(Dispatchers.IO) {
                    loadDpcData(context, "２）分類名称")
                }
            val loadedByotai =
                withContext(Dispatchers.IO) {
                    loadDpcData(context, "３）病態等分類")
                }
            val loadedIcd =
                withContext(Dispatchers.IO){
                    loadDpcData(context, "４）ＩＣＤ")
                }
            val loadedNenrei =
                withContext(Dispatchers.IO){
                    loadDpcData(context, "５）年齢、出生時体重等")
                }
            val loadedShujutu = withContext(Dispatchers.IO) {
                loadDpcData(context, "６）手術 ")
            }
            val loadedShochi1 = withContext(Dispatchers.IO){
                loadDpcData(context, "７）手術・処置等１")
            }
            val loadedShochi2 = withContext(Dispatchers.IO){
                loadDpcData(context, "８）手術・処置等２")
            }
            val loadedFukubyomei = withContext(Dispatchers.IO){
                loadDpcData(context, "９）定義副傷病名")
            }
            val loadedJushodoJcs = withContext(Dispatchers.IO){
                loadDpcData(context, "10－1）重症度等（ＪＣＳ等）")
            }
            val loadedJushodoShujutu = withContext(Dispatchers.IO){
                loadDpcData(context, "10－2）重症度等（手術等）")
            }
            val loadedJushodoJushou = withContext(Dispatchers.IO){
                loadDpcData(context, "10－3）重症度等（重症・軽症）")
            }
            val loadedJushodoRankin = withContext(Dispatchers.IO){
                loadDpcData(context, "10－4）重症度等（発症前Rankin Scale等）")
            }

            loadingMessage = "Waiting for all sheets to finish loading..."

            loadingMessage = "Updating UI..."

            // すべてのデータが揃ったら、Stateを一度だけ更新してUIの再描画をトリガーする
            withContext(Dispatchers.Main) {
                df = df.copy(
                    mdc = loadedMdc,
                    bunrui = loadedBunrui,
                    byotai = loadedByotai,
                    icd = loadedIcd,
                    nenrei = loadedNenrei,
//                    shujutu = loadedShujutu,
//                    shochi1 = loadedShochi1,
//                    shochi2 = loadedShochi2,
//                    fukubyomei = loadedFukubyomei,
//                    jushodoJcs = loadedJushodoJcs,
//                    jushodoShujutu = loadedJushodoShujutu,
//                    jushodoJushou = loadedJushodoJushou,
//                    jushodoRankin = loadedJushodoRankin
                )
            }
        } catch (t: Throwable) {
            errorMessage = t.message ?: "An unknown error occurred"
            t.printStackTrace()
        } finally {
            isLoading = false
        }
    }

    MedGuidelinesScaffold(
        topBar = {
            TitleTopAppBar(
                title = R.string.dpcTitle,
                navController = navController,
                references = listOf(
                    TextAndUrl(R.string.space, R.string.space)
                )
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            when {
                isLoading -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Text(text = loadingMessage, modifier = Modifier.padding(top = 8.dp))
                    }
                }

                errorMessage != null -> {
                    Text(text = "Error: $errorMessage")
                }

                else -> {
                    Column {
                        Text("All sheets loaded successfully!")
                        Text("MDC rows: ${df.mdc?.rowsCount() ?: "Failed"}")
                        Text("Bunrui rows: ${df.bunrui?.rowsCount() ?: "Failed"}")
                        Text("Byotai rows: ${df.byotai?.rowsCount() ?: "Failed"}")
                        Text("ICD rows: ${df.icd?.rowsCount() ?: "Failed"}")
                        Text("Nenrei rows: ${df.nenrei?.rowsCount() ?: "Failed"}")
                        Text("Shujutu rows: ${df.shujutu?.rowsCount() ?: "Failed"}")
                        Text("Shochi1 rows: ${df.shochi1?.rowsCount() ?: "Failed"}")
                        Text("Shochi2 rows: ${df.shochi2?.rowsCount() ?: "Failed"}")
                        Text("Fukubyomei rows: ${df.fukubyomei?.rowsCount() ?: "Failed"}")
                        Text("JCS rows: ${df.jushodoJcs?.rowsCount() ?: "Failed"}")
                        Text("Jushodo Shujutu rows: ${df.jushodoShujutu?.rowsCount() ?: "Failed"}")
                        Text("Jushodo Jushou rows: ${df.jushodoJushou?.rowsCount() ?: "Failed"}")
                        Text("Jushodo Rankin rows: ${df.jushodoRankin?.rowsCount() ?: "Failed"}")
                    }
                }
            }
        }
        // UIの表示はDpcScreenContentに委任する
//    DpcScreenContent(
//        navController = navController,
//        df = df,
//        isLoading = isLoading,
//        errorMessage = errorMessage,
//        loadingMessage = loadingMessage
//    )
    }
}

/**
 * UIの表示のみを担当する "ダム" (Stateless) なコンポーザブル
 */
//@Composable
//private fun DpcScreenContent(
//    navController: NavHostController,
//    df: DpcDataSheets,
//    isLoading: Boolean,
//    errorMessage: String?,
//    loadingMessage: String
//) {
//    MedGuidelinesScaffold(
//        topBar = {
//            TitleTopAppBar(
//                title = R.string.dpcTitle,
//                navController = navController,
//                references = listOf(TextAndUrl(R.string.space, R.string.space))
//            )
//        },
//    ) { innerPadding ->
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(innerPadding),
//            contentAlignment = Alignment.Center
//        ) {
//            // 状態に応じてUIを切り替える
//            when {
//                isLoading -> {
//                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                        CircularProgressIndicator()
//                        Text(text = loadingMessage, modifier = Modifier.padding(top = 8.dp))
//                    }
//                }
//                errorMessage != null -> {
//                    Text(text = "Error: $errorMessage")
//                }
//                else -> {
//                    Column {
//                        Text("All sheets loaded successfully!")
//                        Text("MDC rows: ${df.mdc?.rowsCount() ?: "Failed"}")
//                        Text("Bunrui rows: ${df.bunrui?.rowsCount() ?: "Failed"}")
//                        Text("Byotai rows: ${df.byotai?.rowsCount() ?: "Failed"}")
//                        Text("ICD rows: ${df.icd?.rowsCount() ?: "Failed"}")
//                        Text("Nenrei rows: ${df.nenrei?.rowsCount() ?: "Failed"}")
//                        Text("Shujutu rows: ${df.shujutu?.rowsCount() ?: "Failed"}")
//                        Text("Shochi1 rows: ${df.shochi1?.rowsCount() ?: "Failed"}")
//                        Text("Shochi2 rows: ${df.shochi2?.rowsCount() ?: "Failed"}")
//                        Text("Fukubyomei rows: ${df.fukubyomei?.rowsCount() ?: "Failed"}")
//                        Text("JCS rows: ${df.jushodoJcs?.rowsCount() ?: "Failed"}")
//                        Text("Jushodo Shujutu rows: ${df.jushodoShujutu?.rowsCount() ?: "Failed"}")
//                        Text("Jushodo Jushou rows: ${df.jushodoJushou?.rowsCount() ?: "Failed"}")
//                        Text("Jushodo Rankin rows: ${df.jushodoRankin?.rowsCount() ?: "Failed"}")
//                    }
//                }
//            }
//        }
//    }
//}


/**
 * 指定されたシート名のExcelシートを読み込み、DataFrameとして返す。
 */
private fun loadDpcData(context: Context, sheetName: String): DataFrame<*> {
    context.resources.openRawResource(R.raw.dpc001348055).use { inputStream ->
        return DataFrame.readExcel(
            inputStream = inputStream,
            sheetName = sheetName,
            skipRows = 0,
            nameRepairStrategy = NameRepairStrategy.MAKE_UNIQUE,
            firstRowIsHeader = false
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DpcScreenPreview() {
    DpcScreen(navController = NavHostController(LocalContext.current))
}

