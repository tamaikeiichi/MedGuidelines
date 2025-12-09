package com.keiichi.medguidelines.ui.screen

import android.content.Context
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.core.graphics.values
import androidx.navigation.NavHostController
import com.keiichi.medguidelines.R
import com.keiichi.medguidelines.ui.component.MedGuidelinesCard
import com.keiichi.medguidelines.ui.component.MedGuidelinesScaffold
import com.keiichi.medguidelines.ui.component.MyCustomSearchBar
import com.keiichi.medguidelines.ui.component.TextAndUrl
import com.keiichi.medguidelines.ui.component.TitleTopAppBar
import com.keiichi.medguidelines.ui.component.normalizeTextForSearch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.filter
import org.jetbrains.kotlinx.dataframe.api.rows
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

@Composable
fun DpcScreen(navController: NavHostController) {
    val context = LocalContext.current

    var df by remember { mutableStateOf(DpcDataSheets()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var loadingMessage by remember { mutableStateOf("Starting to load data...") }

    // Composableが最初に表示されたときに一度だけ実行される副作用
    LaunchedEffect(Unit) {
        delay(100)
        isLoading = true
        try {
            val loadedData = withContext(Dispatchers.IO) {
                loadingMessage = "Loading sheets in parallel..."

                val deferredMdc = async { loadDpcData(context, "１）ＭＤＣ名称") }
                val deferredBunrui = async { loadDpcData(context, "２）分類名称") }
                val deferredByotai = async { loadDpcData(context, "３）病態等分類") }
                val deferredIcd = async { loadDpcData(context, "４）ＩＣＤ") }
                val deferredNenrei = async { loadDpcData(context, "５）年齢、出生時体重等") }
                val deferredShujutu = async { loadDpcData(context, "６）手術 ") }
                val deferredShochi1 = async { loadDpcData(context, "７）手術・処置等１") }
                val deferredShochi2 = async { loadDpcData(context, "８）手術・処置等２") }
                val deferredFukubyomei = async { loadDpcData(context, "９）定義副傷病名") }
                val deferredJushodoJcs = async { loadDpcData(context, "10－1）重症度等（ＪＣＳ等）") }
                val deferredJushodoShujutu = async { loadDpcData(context, "10－2）重症度等（手術等）") }
                val deferredJushodoJushou = async { loadDpcData(context, "10－3）重症度等（重症・軽症）") }
                val deferredJushodoRankin = async { loadDpcData(context, "10－4）重症度等（発症前Rankin Scale等）") }

                loadingMessage = "Waiting for all sheets to finish loading..."

                awaitAll(
                    deferredMdc, deferredBunrui, deferredByotai, deferredIcd, deferredNenrei,
                    deferredShujutu, deferredShochi1, deferredShochi2, deferredFukubyomei,
                    deferredJushodoJcs, deferredJushodoShujutu, deferredJushodoJushou, deferredJushodoRankin
                )
            }

            withContext(Dispatchers.Main) {
                loadingMessage = "Updating UI..."
                df = df.copy(
                    mdc = loadedData[0],
                    bunrui = loadedData[1],
                    byotai = loadedData[2],
                    icd = loadedData[3],
                    nenrei = loadedData[4],
                    shujutu = loadedData[5],
                    shochi1 = loadedData[6],
                    shochi2 = loadedData[7],
                    fukubyomei = loadedData[8],
                    jushodoJcs = loadedData[9],
                    jushodoShujutu = loadedData[10],
                    jushodoJushou = loadedData[11],
                    jushodoRankin = loadedData[12]
                )
                loadingMessage = "loaded"
            }

        } catch (t: Throwable) {
            errorMessage = t.message ?: "An unknown error occurred"
            t.printStackTrace()
        } finally {
            // このブロックはtry-catchが完了した後に必ず実行される
            isLoading = false
        }
    }

    DpcScreenContent(
        navController = navController,
        df = df,
        isLoading = isLoading,
        errorMessage = errorMessage,
        loadingMessage = loadingMessage
    )
}

@Composable
private fun DpcScreenContent(
    navController: NavHostController,
    df: DpcDataSheets,
    isLoading: Boolean,
    errorMessage: String?,
    loadingMessage: String
) {
    // --- START: 検索と選択のためのStateを追加 ---
    var bunruiIcdCurrentRawQuery by remember { mutableStateOf("") }
    var bunruiIcdSelectedIcdItem by remember { mutableStateOf<String?>(null) }
    var displayedItemsBunrui by remember { mutableStateOf<DataFrame<*>?>(null) }
    var displayedItemsIcd by remember { mutableStateOf<DataFrame<*>?>(null) }

    var isSearching by remember { mutableStateOf(false) }

    // --- START: 修正・統合された検索ロジック ---
    LaunchedEffect(bunruiIcdCurrentRawQuery) {
        isSearching = true
        withContext(Dispatchers.Default) {
            val spaceRegex = Regex("[ 　]+")
            val searchTerms = bunruiIcdCurrentRawQuery
                .split(spaceRegex)
                .map { normalizeTextForSearch(it) }
                .filter { it.isNotBlank() }

            if (searchTerms.isEmpty()) {
                withContext(Dispatchers.Main) {
                    displayedItemsIcd = DataFrame.empty()
                    displayedItemsBunrui = DataFrame.empty()
                }
            } else {
                // 両方のフィルタリングを並行して実行
                val filteredIcdDeferred = //async {
                    df.icd?.filter { dataRow ->
                        // 3列目(インデックス2)の値を取得してフィルタリング
                        // getOrNullを使って安全に列にアクセス
                        val itemText = dataRow[2]?.toString()
                        if (itemText != null) {
                            val normalizedItemText = normalizeTextForSearch(itemText)
                            searchTerms.all { term -> normalizedItemText.contains(term) }
                        } else {
                            false // 3列目がnullの場合はフィルタリングしない
                        }
                    } ?: DataFrame.empty()
                //}

                val filteredBunruiDeferred = async {
                    df.bunrui?.filter { dataRow ->
                        // 3列目(インデックス2)の値を取得してフィルタリング
                        dataRow[2]?.toString()?.let { itemText ->
                            val normalizedItemText = normalizeTextForSearch(itemText)
                            searchTerms.all { term -> normalizedItemText.contains(term) }
                        } ?: false // 3列目が存在しないかnullの場合はフィルタリングしない
                    } ?: DataFrame.empty()
                }

                // UI更新はメインスレッドで行う
                withContext(Dispatchers.Main) {
                    displayedItemsIcd = filteredIcdDeferred//.await()
                    displayedItemsBunrui = filteredBunruiDeferred.await()
                }
            }
        }
        isSearching = false
    }
    // --- END: 修正・統合された検索ロジック ---

    MedGuidelinesScaffold(
        topBar = {
            TitleTopAppBar(
                title = R.string.dpcTitle,
                navController = navController,
                references = listOf(TextAndUrl(R.string.space, R.string.space))
            )
        },
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) { // Columnを追加してUI要素を縦に並べる
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                            Text(text = loadingMessage, modifier = Modifier.padding(top = 8.dp))
                        }
                    }
                }
                errorMessage != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Error: $errorMessage")
                    }
                }
                else -> {
                    // --- START: 検索UIと結果表示UIを追加 ---
                    // 選択されたアイテムを表示
                    if (bunruiIcdSelectedIcdItem != null) {
                        MedGuidelinesCard() {
                            Text(
                                text = "$bunruiIcdSelectedIcdItem",
                                modifier = Modifier.padding(16.dp)
                            )
                        }

                    }

                    // 検索バー
                    MyCustomSearchBar(
                        searchQuery = bunruiIcdCurrentRawQuery,
                        onSearchQueryChange = { bunruiIcdCurrentRawQuery = it },
                        onSearch = { bunruiIcdCurrentRawQuery = it },
                        isLoading = false,
                        placeholderText = R.string.search
                    )

                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        // --- ICDコードの結果 ---
                        displayedItemsIcd?.let { icdResults ->
                            if (icdResults.rowsCount() > 0) {
                                stickyHeader {
                                    MedGuidelinesCard {
                                        Text(text = "ICDコードから (From ICD Code)", modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp))
                                    }
                                }
                                items(icdResults.rows().toList()) { row ->
                                    val itemText = row[2]?.toString() ?: ""
                                    Text(
                                        text = itemText,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                bunruiIcdSelectedIcdItem = itemText
                                            }
                                            .padding(16.dp)
                                    )
                                }
                            }
                        }

                        // --- 分類名称の結果 ---
                        displayedItemsBunrui?.let { bunruiResults ->
                            if (bunruiResults.rowsCount() > 0) {
                                stickyHeader {
                                    MedGuidelinesCard {
                                        Text(text = "分類名称から (From Classification Name)", modifier = Modifier.fillMaxWidth().padding(16.dp))
                                    }
                                }
                                items(bunruiResults.rows().toList()) { row ->
                                    val itemText = row.values().joinToString(" | ")
                                    Text(
                                        text = itemText,
                                        modifier = Modifier.fillMaxWidth().clickable {
                                            bunruiIcdSelectedIcdItem = itemText
                                        }.padding(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
private fun loadDpcData(context: Context, sheetName: String): DataFrame<*> {
    return try {
        context.resources.openRawResource(R.raw.dpc001348055).use { inputStream ->
            DataFrame.readExcel(
                inputStream = inputStream,
                sheetName = sheetName,
                skipRows = 0,
                nameRepairStrategy = NameRepairStrategy.MAKE_UNIQUE,
                firstRowIsHeader = false
            )
        }
    } catch (e: Exception) {
        Log.e("DPC", "Error loading data for sheet $sheetName: ${e.message}")
        e.printStackTrace()
        DataFrame.empty() // Return an empty DataFrame instead of emptyList()
    }
}

@Preview(showBackground = true)
@Composable
fun DpcScreenPreview() {
    DpcScreen(navController = NavHostController(LocalContext.current))
}

