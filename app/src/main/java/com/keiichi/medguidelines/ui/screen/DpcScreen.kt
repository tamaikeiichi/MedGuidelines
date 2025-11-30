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
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
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
        isLoading = false
        try {
            // UIスレッドをブロックしないように、処理全体をバックグラウンドスレッドで実行
//            withContext(Dispatchers.IO) {
//                loadingMessage = "Loading sheets in parallel..."

                // asyncを使って、すべてのシート読み込み処理を並行して開始する
                val deferredMdc = //async
                      loadDpcData(context, "１）ＭＤＣ名称")
//                val deferredBunrui = async { loadDpcData(context, "２）分類名称") }
//                val deferredByotai = async { loadDpcData(context, "３）病態等分類") }
//                val deferredIcd = async { loadDpcData(context, "４）ＩＣＤ") }
//                val deferredNenrei = async { loadDpcData(context, "５）年齢、出生時体重等") }
//                val deferredShujutu = async { loadDpcData(context, "６）手術 ") }
//                val deferredShochi1 = async { loadDpcData(context, "７）手術・処置等１") }
//                val deferredShochi2 = async { loadDpcData(context, "８）手術・処置等２") }
//                val deferredFukubyomei = async { loadDpcData(context, "９）定義副傷病名") }
//                val deferredJushodoJcs = async { loadDpcData(context, "10－1）重症度等（ＪＣＳ等）") }
//                val deferredJushodoShujutu = async { loadDpcData(context, "10－2）重症度等（手術等）") }
//                val deferredJushodoJushou = async { loadDpcData(context, "10－3）重症度等（重症・軽症）") }
//                val deferredJushodoRankin = async { loadDpcData(context, "10－4）重症度等（発症前Rankin Scale等）") }

                loadingMessage = "Waiting for all sheets to finish loading..."

                // awaitAllですべての非同期処理が完了するのを待つ
//                val allData = awaitAll(
//                    deferredMdc,
//                    deferredBunrui, deferredByotai, deferredIcd, deferredNenrei,
//                    deferredShujutu, deferredShochi1, deferredShochi2, deferredFukubyomei,
//                    deferredJushodoJcs, deferredJushodoShujutu, deferredJushodoJushou, deferredJushodoRankin
                //)

                loadingMessage = "Updating UI..."

                // すべてのデータが揃ったら、Stateを一度だけ更新してUIの再描画をトリガーする
                df = df.copy(
                    mdc = deferredMdc as DataFrame<*>?
                    //mdc = allData[0],
//                    bunrui = allData[1],
//                    byotai = allData[2],
//                    icd = allData[3],
//                    nenrei = allData[4],
//                    shujutu = allData[5],
//                    shochi1 = allData[6],
//                    shochi2 = allData[7],
//                    fukubyomei = allData[8],
//                    jushodoJcs = allData[9],
//                    jushodoShujutu = allData[10],
//                    jushodoJushou = allData[11],
//                    jushodoRankin = allData[12]
                )
            //}
        } catch (t: Throwable) {
            errorMessage = t.message ?: "An unknown error occurred"
            t.printStackTrace()
        } finally {
            isLoading = false
        }
    }

    // UIの表示はDpcScreenContentに委任する
    DpcScreenContent(
        navController = navController,
        df = df,
        isLoading = isLoading,
        errorMessage = errorMessage,
        loadingMessage = loadingMessage
    )
}

/**
 * UIの表示のみを担当する "ダム" (Stateless) なコンポーザブル
 */
@Composable
private fun DpcScreenContent(
    navController: NavHostController,
    df: DpcDataSheets,
    isLoading: Boolean,
    errorMessage: String?,
    loadingMessage: String
) {
    MedGuidelinesScaffold(
        topBar = {
            TitleTopAppBar(
                title = R.string.dpcTitle,
                navController = navController,
                references = listOf(TextAndUrl(R.string.space, R.string.space))
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            // 状態に応じてUIを切り替える
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
    }
}


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

// --- START: プレビューの修正 ---
@Preview(showBackground = true)
@Composable
fun DpcScreenPreview() {
    // プレビューでは、UI表示用のDpcScreenContentを直接呼び出す
    // これにより、実際のデータ読み込みを回避し、クラッシュしなくなる
    DpcScreenContent(
        navController = NavHostController(LocalContext.current),
        df = DpcDataSheets(), // 空のデータで初期化
        isLoading = true,     // ローディング状態をプレビュー
        errorMessage = null,
        loadingMessage = "Loading sheets in parallel..."
    )
}
// --- END: プレビューの修正 ---

