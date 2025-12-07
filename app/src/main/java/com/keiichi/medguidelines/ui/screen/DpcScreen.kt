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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
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
import org.jetbrains.kotlinx.dataframe.api.schema
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
    var currentRawQuery by remember { mutableStateOf("") }
    var selectedIcdItem by remember { mutableStateOf<String?>(null) }
    var displayedItems by remember { mutableStateOf<List<String>>(emptyList()) }


    // searchQueryが変更されるたびに、検索結果を再計算する
    LaunchedEffect(
        currentRawQuery
    ) {
        val filtered = withContext(Dispatchers.Default) {

                // df.icdがnullまたはsearchQueryが空の場合は空のリストを返す
                if (currentRawQuery.isBlank()) {
                    emptyList<String>()
                } else {
                        val spaceRegex = Regex("[ 　]+")
                        val searchTerms = currentRawQuery
                            .split(spaceRegex) // Split by the regex
                            .map { normalizeTextForSearch(it) }
                            .filter { it.isNotBlank() }
                    // Safely get the data from the third column
                    val thirdColumnData = df.icd?.columns()?.getOrNull(2)?.toList()
                    thirdColumnData?.filter { item ->
                        // Convert the current item to a string and normalize it for searching
                        val normalizedItemText = normalizeTextForSearch(item.toString())
                        // Check if the normalized item text contains ALL of the search terms
                        searchTerms.all { term ->
                            normalizedItemText.contains(term)
                        }
                    } ?: emptyList()

                }
        }
        displayedItems = filtered.map { it.toString() }
    }
    // --- END: 検索と選択のためのStateを追加 ---

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
                    if (selectedIcdItem != null) {
                        Text(
                            text = "Selected: $selectedIcdItem",
                            modifier = Modifier.padding(16.dp)
                        )
                    }

                    // 検索バー
                    MyCustomSearchBar(
                        searchQuery = currentRawQuery,
                        onSearchQueryChange = { currentRawQuery = it },
                        onSearch = { currentRawQuery = it },
                        isLoading = false
                    )
                    // Directly get the string name of the third column
                    // Safely get the third column name only if the dataframe and its columns exist
                    // Safely get the third column by index and convert its values to a list.
                    val thirdColumnData: List<Any?>? = df.icd?.columns()?.getOrNull(2)?.toList()

// You can now use `thirdColumnData` as a list of all row values for that column.
                    if (thirdColumnData != null) {
                        Text("First 5 rows of the third column:")
                        LazyColumn {
                            items(items = thirdColumnData.take(5)) { rowData ->
                                Text(text = rowData.toString())
                            }
                        }
                    }



                    // 検索結果リスト
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(items = displayedItems) { item ->
                            Text(
                                text = item,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        // アイテムをタップしたら、選択状態を更新
                                        selectedIcdItem = item
                                    }
                                    .padding(16.dp)
                            )
                        }
                    }
                    // --- END: 検索UIと結果表示UIを追加 ---
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

