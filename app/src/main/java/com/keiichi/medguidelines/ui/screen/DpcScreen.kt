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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.keiichi.medguidelines.data.dpcByotai
import com.keiichi.medguidelines.ui.component.MedGuidelinesCard
import com.keiichi.medguidelines.ui.component.MedGuidelinesScaffold
import com.keiichi.medguidelines.ui.component.MyCustomSearchBar
import com.keiichi.medguidelines.ui.component.TextAndUrl
import com.keiichi.medguidelines.ui.component.TitleTopAppBar
import com.keiichi.medguidelines.ui.component.buttonAndScoreWithScoreDisplayedSelectable
import com.keiichi.medguidelines.ui.component.normalizeTextForSearch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.api.concat
import org.jetbrains.kotlinx.dataframe.api.distinct
import org.jetbrains.kotlinx.dataframe.api.drop
import org.jetbrains.kotlinx.dataframe.api.filter
import org.jetbrains.kotlinx.dataframe.api.firstOrNull
import org.jetbrains.kotlinx.dataframe.api.forEach
import org.jetbrains.kotlinx.dataframe.api.map
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

data class DpcCode(
    var mdc: String? = "xx",
    var bunrui: String? = "xxxx",
    var byotai: String? = "x",
    var nyuin: String? = "x",
    var nenrei: String? = "x",
    var shujutu: String? = "xx",
    var shochi1: String? = "x",
    var shochi2: String? = "x",
    var fukubyomei: String? = "x",
    var jushodo: String? = "x",
    var icd: String? = "x"
)

@Composable
fun DpcScreen(navController: NavHostController) {
    val context = LocalContext.current

    var dpcMaster by remember { mutableStateOf(DpcDataSheets()) }
    var dpcCodeFirst by remember { mutableStateOf(DpcCode()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var loadingMessage by remember { mutableStateOf("Starting to load data...") }

    LaunchedEffect(Unit) {
        delay(500)
        isLoading = true
        try {
            val loadedData = withContext(Dispatchers.IO) {
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
                val deferredJushodoJushou =
                    async { loadDpcData(context, "10－3）重症度等（重症・軽症）") }
                val deferredJushodoRankin =
                    async { loadDpcData(context, "10－4）重症度等（発症前Rankin Scale等）") }

                loadingMessage = "DPCマスタ読込中..."

                awaitAll(
                    deferredMdc,
                    deferredBunrui,
                    deferredByotai,
                    deferredIcd,
                    deferredNenrei,
                    deferredShujutu,
                    deferredShochi1,
                    deferredShochi2,
                    deferredFukubyomei,
                    deferredJushodoJcs,
                    deferredJushodoShujutu,
                    deferredJushodoJushou,
                    deferredJushodoRankin
                )
            }

            withContext(Dispatchers.Main) {
                dpcMaster = dpcMaster.copy(
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
            isLoading = false
        }
    }

    DpcScreenContent(
        navController = navController,
        dpcMaster = dpcMaster,
        isLoading = isLoading,
        errorMessage = errorMessage,
        loadingMessage = loadingMessage,
        dpcCodeFirst = dpcCodeFirst,
        onDpcCodeChange = { newDpcCode ->
            dpcCodeFirst = newDpcCode
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DpcScreenContent(
    navController: NavHostController,
    dpcMaster: DpcDataSheets,
    isLoading: Boolean,
    errorMessage: String?,
    loadingMessage: String,
    dpcCodeFirst: DpcCode,
    onDpcCodeChange: (DpcCode) -> Unit
) {
    // --- START: 検索と選択のためのStateを追加 ---
    var query by remember { mutableStateOf("J16") }
    var bunruiIcdSelectedIcdItem by remember { mutableStateOf<String?>(null) }
    var icdCode by remember { mutableStateOf<String?>(null) }
    var displayedItemsBunrui by remember { mutableStateOf<DataFrame<*>?>(null) }
    var displayedItemsIcd by remember { mutableStateOf<DataFrame<*>?>(null) }
    var filteredByotai by remember { mutableStateOf<DataFrame<*>?>(null) }

    LaunchedEffect(query, dpcMaster.icd) {
        // クエリが空なら結果をクリアして終了
        if (query.isBlank()) {
            displayedItemsIcd = DataFrame.empty()
            return@LaunchedEffect
        }
        val filteredResult: DataFrame<*> = withContext(Dispatchers.Default) {
            // 1. 3列目(targetRow=2)でフィルタリング
            val filteredByThirdColumn = filterDpcMaster(
                icdDataFrame = dpcMaster.icd,
                query = query,
                targetRow = 2
            )
            val filteredByFourthColumn = filterDpcMaster(
                icdDataFrame = dpcMaster.icd,
                query = query,
                targetRow = 3
            )
            filteredByThirdColumn.concat(filteredByFourthColumn).distinct()
        }
        withContext(Dispatchers.Main) {
            displayedItemsIcd = filteredResult
        }
    }

    LaunchedEffect(dpcCodeFirst.bunrui, dpcCodeFirst.mdc, query) {
        val filteredResult = withContext(Dispatchers.Default) {
            // 1. まずMDCコードでフィルタリング
            val mdcFiltered = filterDpcMaster(
                icdDataFrame = dpcMaster.byotai,
                query = dpcCodeFirst.mdc ?: "",
                targetRow = 0
            )
            // 2. 次に、MDCでフィルタリングされた結果を、さらに分類コードでフィルタリング
            filterDpcMaster(
                icdDataFrame = mdcFiltered,
                query = dpcCodeFirst.bunrui ?: "",
                targetRow = 1
            )
        }
        // 最終的な結果をUIのStateに反映
        filteredByotai = filteredResult
    }

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
                    if (bunruiIcdSelectedIcdItem != null) {
                        MedGuidelinesCard() {
                            Text(
                                text = "$bunruiIcdSelectedIcdItem",
                                modifier = Modifier.padding(16.dp)
                            )
                            Text(
                                text = "ICDコード: $icdCode",
                                modifier = Modifier.padding(16.dp)
                            )

                        }

                    }

                    MyCustomSearchBar(
                        searchQuery = query,
                        onSearchQueryChange = { query = it },
                        onSearch = { query = it },
                        isLoading = false,
                        placeholderText = R.string.searchIcd
                    )

                    if (dpcCodeFirst.mdc == "xx" && dpcCodeFirst.bunrui == "xxxx") {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            // --- ICDコードの結果 ---
                            displayedItemsIcd?.let { icdResults ->
                                if (icdResults.rowsCount() > 0) {
                                    stickyHeader {
                                        MedGuidelinesCard {
                                            Text(
                                                text = "ICDコードから (From ICD Code)",
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(16.dp)
                                            )
                                        }
                                    }
                                    items(icdResults.rows().toList()) { row ->
                                        val itemText = row[2]?.toString() ?: ""
                                        val icdCodeSelected = row[3]?.toString() ?: ""
                                        val mdcSelected = row[0]?.toString() ?: ""
                                        val bunruiSelected = row[1]?.toString() ?: ""
                                        Text(
                                            text = itemText,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    bunruiIcdSelectedIcdItem = itemText
                                                    onDpcCodeChange(
                                                        dpcCodeFirst.copy(
                                                            icd = icdCodeSelected,
                                                            mdc = mdcSelected,
                                                            bunrui = bunruiSelected
                                                        )
                                                    )
                                                }
                                                .padding(16.dp)
                                        )
                                    }
                                }
                            }

                            displayedItemsBunrui?.let { bunruiResults ->
                                if (bunruiResults.rowsCount() > 0) {
                                    stickyHeader {
                                        MedGuidelinesCard {
                                            Text(
                                                text = "分類名称から (From Classification Name)",
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(16.dp)
                                            )
                                        }
                                    }
                                    items(bunruiResults.rows().toList()) { row ->
                                        val itemText = row.values().joinToString(" | ")
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
                        }

                    }
                    if (dpcCodeFirst.mdc != "xx" && dpcCodeFirst.bunrui != "xxxx") {
                        var defaultSelectedOption by remember { mutableIntStateOf(dpcByotai[1].labelResId) }
                        val dpcByotaiAgeScore = buttonAndScoreWithScoreDisplayedSelectable(
                            optionsWithScores = dpcByotai,
                            title = R.string.space,
                            defaultSelectedOption = defaultSelectedOption,
                            isNumberDisplayed = false,
                            onOptionSelected = { newSelection ->
                                defaultSelectedOption = newSelection
                            },
                        )
                        if (dpcByotaiAgeScore == 0) {
                            dpcCodeFirst.byotai == "0"
                        } else {
                            var expanded by remember { mutableStateOf(false) }
                            val options = dpcMaster.byotai?.columns()[7]?.drop(3)?.map {
                                it.toString() }
                            var selectedOption by remember { mutableStateOf("院内肺炎又は市中肺炎") }
                            ExposedDropdownMenuBox(
                                expanded = expanded,
                                onExpandedChange = { expanded = !expanded }
                            ) {
                                TextField(
                                    value = selectedOption,
                                    onValueChange = {},
                                    readOnly = true,
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                                    },
                                    colors = ExposedDropdownMenuDefaults.textFieldColors(),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                )
                                ExposedDropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false }
                                ) {
                                    options?.forEach { option ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    option,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                            },
                                            onClick = {
                                                selectedOption = option
                                                expanded = false
                                                val matchedRow = dpcMaster.byotai?.filter {
                                                    // 8列目(index 7)の値が、選択されたoptionと一致するかチェック
                                                    it[7]?.toString() == option
                                                }

                                                // 3. 一致した行から3列目(index 2)の値を取得し、dpcCodeFirstを更新
                                                // filterの結果から最初の行を取得
                                                val firstMatchedRow = matchedRow?.firstOrNull()
// 取得した行から3列目(index 2)の値を取得
                                                val byotaiCode = firstMatchedRow?.get(2)?.toString()

//                                                if (byotaiCode != null) {
//                                                    onDpcCodeChange(dpcCodeFirst.copy(byotai = byotaiCode))
//                                                }
                                            },
                                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                        )
                                    }
                                }
                            }
                        }
                        Text(
                            text = "dpcCodeFirst.byotai ${dpcCodeFirst.byotai.toString()}",
                            modifier = Modifier.padding(16.dp)
                        )
//                        Text(
//                            text = "filteredByotai ${filteredByotai.toString()}",
//                            modifier = Modifier.padding(16.dp)
//                        )
//                        Text(
//                            text = "dpcCodeFirst.mdc ${dpcCodeFirst.mdc.toString()}",
//                            modifier = Modifier.padding(16.dp)
//                        )
//                        Text(
//                            text = "dpcCodeFirst.bunrui ${dpcCodeFirst.bunrui.toString()}",
//                            modifier = Modifier.padding(16.dp)
//                        )
//                        Text(
//                            text = "dpcMaster.byotai ${dpcMaster.byotai.toString()}",
//                            modifier = Modifier.padding(16.dp)
//                        )
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
                firstRowIsHeader = false,

                //stringColumns = StringColumns
            )//.convert { all() }.to<String>()
        }
    } catch (e: Exception) {
        Log.e("DPC", "Error loading data for sheet $sheetName: ${e.message}")
        e.printStackTrace()
        DataFrame.empty() // Return an empty DataFrame instead of emptyList()
    }
}

/** * ICDのDataFrameを検索クエリでフィルタリングするサスペンド関数
 * @param icdDataFrame フィルタリング対象のDataFrame
 * @param query ユーザーが入力した検索クエリ
 * @return フィルタリングされたDataFrame
 */
private suspend fun filterDpcMaster(
    icdDataFrame: DataFrame<*>?,
    query: String,
    targetRow: Int
): DataFrame<*> {
    // この関数はバックグラウンドスレッドで実行されることを想定
    if (icdDataFrame == null || query.isBlank()) {
        return DataFrame.empty()
    }

    val spaceRegex = Regex("[ 　]+")
    val searchTerms = query
        .split(spaceRegex)
        .map { normalizeTextForSearch(it) }
        .filter { it.isNotBlank() }

    if (searchTerms.isEmpty()) {
        return DataFrame.empty()
    }

    return icdDataFrame.filter { dataRow ->
        // 3列目(インデックス2)の値を安全に取得してフィルタリング
        val itemText = dataRow[targetRow]?.toString()
        if (itemText != null) {
            val normalizedItemText = normalizeTextForSearch(itemText)
            searchTerms.all { term -> normalizedItemText.contains(term) }
        } else {
            false // 3列目がnullの場合は結果に含めない
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DpcScreenPreview() {
    DpcScreen(navController = NavHostController(LocalContext.current))
}

