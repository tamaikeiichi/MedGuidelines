package com.keiichi.medguidelines.ui.screen

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.keiichi.medguidelines.R
import com.keiichi.medguidelines.data.ShindangunBunruiTensuhyoJoken
import com.keiichi.medguidelines.ui.component.Dimensions
import com.keiichi.medguidelines.ui.component.InputValue
import com.keiichi.medguidelines.ui.component.MedGuidelinesCard
import com.keiichi.medguidelines.ui.component.MedGuidelinesScaffold
import com.keiichi.medguidelines.ui.component.MyCustomSearchBar
import com.keiichi.medguidelines.ui.component.ScoreBottomAppBarVariable
import com.keiichi.medguidelines.ui.component.TextAndUrl
import com.keiichi.medguidelines.ui.component.TitleTopAppBar
import com.keiichi.medguidelines.ui.component.VerticalLazyScrollbar
import com.keiichi.medguidelines.ui.component.VerticalScrollbar
import com.keiichi.medguidelines.ui.component.buttonAndScoreWithScoreDisplayedSelectableLabelString
import com.keiichi.medguidelines.ui.viewModel.DpcScreenViewModel
import kotlinx.coroutines.launch

data class DpcCode(
    var mdc: String? = "xx",
    var bunrui: String? = "xxxx",
    var byotai: String? = "x",
    var nenrei: String? = "x",
    var shujutu: String? = "xx",
    var shochi1: String? = "x",
    var shochi2: String? = "x",
    var fukushobyo: String? = "x",
    var jushodo: String? = "x",
    var icd: String? = "x"
)

data class LabelStringAndScore(
    val labelResId: String?,
    val code: Int,
    val label: String = ""
)

// DpcCodeクラスの近くなどに配置
data class SavedDpcItem(
    val dpcCode: String,
    val tensuData: ShindangunBunruiTensuhyoJoken, // 再計算のためにデータを保持
    val label: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DpcScreen(
    navController: NavHostController,
    dpcScreenViewModel: DpcScreenViewModel = viewModel(factory = DpcScreenViewModel.Factory)
) {
    Log.d("tamai", "before launched effect")
    var dpcCodeFirst by remember { mutableStateOf(DpcCode()) }
    val dpcCodeFirstJoined by remember {
        derivedStateOf {
            listOfNotNull(
                dpcCodeFirst.mdc,
                dpcCodeFirst.bunrui,
                dpcCodeFirst.byotai,
                dpcCodeFirst.nenrei,
                dpcCodeFirst.shujutu,
                dpcCodeFirst.shochi1,
                dpcCodeFirst.shochi2,
                dpcCodeFirst.fukushobyo,
                dpcCodeFirst.jushodo
            ).joinToString("")
        }
    }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var loadingMessage by remember { mutableStateOf("Starting to load data...") }
    val showByotaiSelection by dpcScreenViewModel.showByotaiSelection.collectAsState()
    val showNenreiSelection by dpcScreenViewModel.showNenreiSelection.collectAsState()
    val showShujutsuSelection by dpcScreenViewModel.showShujutsuSelection.collectAsState()
    val showShochi1Selection by dpcScreenViewModel.showShochi1Selection.collectAsState()
    val showShochi2Selection by dpcScreenViewModel.showShochi2Selection.collectAsState()
    val showFukushobyoSelection by dpcScreenViewModel.showFukushobyoSelection.collectAsState()
    val showJushodoJcsSelection by dpcScreenViewModel.showJushodoJcsSelection.collectAsState()
    val showJushodoShujutsuSelection by dpcScreenViewModel.showJushodoShujutsuSelection.collectAsState()
    val showJushodoStrokeSelection by dpcScreenViewModel.showJushodoStrokeSelection.collectAsState()
    val byotaiOptions by dpcScreenViewModel.byotaiOptions.collectAsState()
    var searchResultsVisible by remember { mutableStateOf(true) }
    val days = remember { mutableDoubleStateOf(1.0) }
    var cost = remember { mutableIntStateOf(0) }
    var coeff = remember { mutableDoubleStateOf(1.0) } //JRは1.3071
    var bunruiIcdSelectedIcdItem by androidx.compose.runtime.remember {
        mutableStateOf<String?>(
            null
        )
    }
    val coroutineScope = androidx.compose.runtime.rememberCoroutineScope()

    // データベースからの検索結果を購読
    val displayedItemsIcd by dpcScreenViewModel.displayedItemsIcd.collectAsState()

    var query by remember { mutableStateOf("060") }

    val shindangunBunruiTensuhyo by dpcScreenViewModel.shindangunBunruiTensuhyoOptions.collectAsState()
    Log.d("tamaiDpcUI", "Current List Size in UI: ${shindangunBunruiTensuhyo.size}")

    val clipboard = LocalClipboardManager.current
    val context = LocalContext.current

    val COPY_ICON_ID = "copy_icon"
    val SAVE_ICON_ID = "save_icon"

    val lazyListState = rememberLazyListState()

    // DpcScreen内のrememberセクションに追加
    val savedItems = remember { mutableStateListOf<SavedDpcItem>() }
    val savedTotalAmount by remember(savedItems) {
        derivedStateOf {
            savedItems.sumOf { item ->
                val itemCost = calculateNyuinCost(
                    days = days.doubleValue.toInt(),
                    nyuinbiI = item.tensuData.nyuinbiI.toIntOrNull() ?: 0,
                    nyuinbiII = item.tensuData.nyuinbiII.toIntOrNull() ?: 0,
                    nyuinbiIII = item.tensuData.nyuinbiIII.toIntOrNull() ?: 0,
                    nyuinKikanI = item.tensuData.nyuinKikanI.toIntOrNull() ?: 0,
                    nyuinKikanII = item.tensuData.nyuinKikanII.toIntOrNull() ?: 0,
                    nyuinKikanIII = item.tensuData.nyuinKikanIII.toIntOrNull() ?: 0
                )
                (itemCost * 10 * coeff.doubleValue).toInt()
            }
        }
    }

    // 2. コードが確定したタイミング（例：ICD選択時や、特定のロジック後）でイベントを投げる
    LaunchedEffect(dpcCodeFirstJoined) {
        if (dpcCodeFirstJoined.isNotEmpty()) {
            Log.d("tamaiDpc", "dpcCodeFirst: $dpcCodeFirstJoined")

            // 引数 code を使わず、DBの最初の3行を取得する
            val debugResults = dpcScreenViewModel.getDebugRows()
            debugResults.forEach {
                Log.d("tamaiDpc", "DB stored pattern: $it, ")
            }

            dpcScreenViewModel.onShindangunCodeChanged(dpcCodeFirstJoined)
            Log.d("tamaiDpc", "shindangunBunruiTensuhyo: $shindangunBunruiTensuhyo")
        }
    }

    LaunchedEffect(Unit) {
        // ViewModelの検索メソッドを初期クエリで呼び出す
        dpcScreenViewModel.onQueryChanged(query)
    }
// --- LazyColumnの外（Scaffoldの中など）で計算 ---
    val currentShindangun = shindangunBunruiTensuhyo.firstOrNull()
    val isDataValid = remember(currentShindangun) {
        currentShindangun != null && (
                currentShindangun.nyuinbiI.isNotBlank() ||
                        currentShindangun.nyuinbiII.isNotBlank() ||
                        currentShindangun.nyuinbiIII.isNotBlank() ||
                        currentShindangun.nyuinKikanI.isNotBlank() ||
                        currentShindangun.nyuinKikanII.isNotBlank() ||
                        currentShindangun.nyuinKikanIII.isNotBlank()
                )
    }

    MedGuidelinesScaffold(
        topBar = {
            TitleTopAppBar(
                title = R.string.dpcTitle,
                navController = navController,
                references = listOf(TextAndUrl(R.string.mhlwHp, R.string.dpcUrl)),
                helpMessageResId = R.string.aboutDpc,
            )
        },
        bottomBar = {
            ScoreBottomAppBarVariable(
                modifier = Modifier
//                    .pointerInput(Unit) {
//                    detectTapGestures(
//                        onLongPress = {
//                            if (dpcCodeFirstJoined.isNotEmpty()) {
//                                //coroutineScope.launch {
//                                // 3. クリップボードにコピー
//                                clipboard.setText(AnnotatedString(dpcCodeFirstJoined))
//                                // コピーしたことをユーザーに知らせる
//                                Toast.makeText(
//                                    context,
//                                    "コピー!: $dpcCodeFirstJoined",
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                                //}
//                            }
//                        }
//                    )
                //               },
                ,
                displayText = buildAnnotatedString {
                    withStyle(
                        style = ParagraphStyle(
                            lineHeight = 1.1.em
                        )
                    ) {
                        withStyle(
                            style = SpanStyle(
                                fontSize = 16.sp,
                            )
                        ) {
                            if (savedTotalAmount != 0) {
                                "${
                                    savedItems.lastOrNull()?.let { lastItem ->
                                        append("病名: ")
                                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                            append(lastItem.tensuData.name)
                                        }
                                        append(" DPCコード: ${lastItem.dpcCode} ")
                                        appendInlineContent(COPY_ICON_ID, "[copy]")
                                        append("\n")
                                    }
                                }"
                                append(" 包括金額合計: ${"%,d".format(savedTotalAmount)}円\n")
                            }
                            if (shindangunBunruiTensuhyo.isNotEmpty()) {
                                val data = shindangunBunruiTensuhyo.first()
                                append("病名: ")
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                    append(data.name)
                                }
                            }
                            append(" DPCコード: ")
                            append(dpcCodeFirstJoined)
                            append(" ") // 少し隙間をあける
                            appendInlineContent(COPY_ICON_ID, "[copy]")
                            if (shindangunBunruiTensuhyo.isNotEmpty()) {
                                val data = shindangunBunruiTensuhyo.first()
                                append("\n")
                                if (data.nyuinbiI != "") {
                                    append("入院期間I: ${data.nyuinbiI} ")
                                }
                                if (data.nyuinbiII != "") {
                                    append("入院期間II: ${data.nyuinbiII} ")
                                }
                                if (data.nyuinbiIII != "") {
                                    append("入院期間III: ${data.nyuinbiIII}\n")
                                }
                            }
                        }
                        if (isDataValid && shindangunBunruiTensuhyo.isNotEmpty()) {
                            Log.d("tamaiDpc", "shindangunBunruiTensuhyo: $shindangunBunruiTensuhyo")
//                        savedTotalAmount = savedItems.sumOf { item ->
//                            val itemCost = calculateNyuinCost(
//                                days = days.doubleValue.toInt(),nyuinbiI = item.tensuData.nyuinbiI.toIntOrNull() ?: 0,
//                                nyuinbiII = item.tensuData.nyuinbiII.toIntOrNull() ?: 0,
//                                nyuinbiIII = item.tensuData.nyuinbiIII.toIntOrNull() ?: 0,
//                                nyuinKikanI = item.tensuData.nyuinKikanI.toIntOrNull() ?: 0,
//                                nyuinKikanII = item.tensuData.nyuinKikanII.toIntOrNull() ?: 0,
//                                nyuinKikanIII = item.tensuData.nyuinKikanIII.toIntOrNull() ?: 0
//                            )
//                            (itemCost * 10 * coeff.doubleValue).toInt()
//                        }

                            val data = shindangunBunruiTensuhyo.first()
                            cost.intValue = calculateNyuinCost(
                                days = days.doubleValue.toInt(),
                                nyuinbiI = data.nyuinbiI.toIntOrNull() ?: 0,
                                nyuinbiII = data.nyuinbiII.toIntOrNull() ?: 0,
                                nyuinbiIII = data.nyuinbiIII.toIntOrNull() ?: 0,
                                nyuinKikanI = data.nyuinKikanI.toIntOrNull() ?: 0,
                                nyuinKikanII = data.nyuinKikanII.toIntOrNull() ?: 0,
                                nyuinKikanIII = data.nyuinKikanIII.toIntOrNull() ?: 0,
                            )
                            val currentTotalAmount =
                                (cost.intValue * 10 * coeff.doubleValue).toInt()
                            append("包括金額合計: ${"%,d".format(currentTotalAmount)}円")
                            append(" ")
                            appendInlineContent(SAVE_ICON_ID, "[save]")
                            Log.d("tamaiDpc", "savedTotalAmount: $savedTotalAmount")
//                        if (savedTotalAmount != 0) {
//                            append("包括金額合計: ${"%,d".format(savedTotalAmount)}円")
//                        }
                            if (days.doubleValue.toInt() > data.nyuinbiIII.toInt()) {
                                withStyle(
                                    style = SpanStyle(
                                        fontSize = 16.sp,
                                    )
                                ) {
                                    append("（${data.nyuinbiIII.toInt()}日まで）")
                                }
                            }
                        } else {
                            if (dpcCodeFirst == DpcCode())
                                append("\n(病名を選択してください)")
                            else {
                                append("\n出来高算定")
                            }
                        }
                    }
                },
                inlineContent = mapOf(
                    COPY_ICON_ID to InlineTextContent(
                        Placeholder(
                            width = 18.sp,
                            height = 18.sp,
                            placeholderVerticalAlign = PlaceholderVerticalAlign.Center
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy Code",
                            modifier = Modifier.clickable {
                                if (dpcCodeFirstJoined.isNotEmpty()) {
                                    clipboard.setText(AnnotatedString(dpcCodeFirstJoined))
                                    Toast.makeText(
                                        context,
                                        "DPCコードをコピーしました",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    SAVE_ICON_ID to androidx.compose.foundation.text.InlineTextContent(
                        Placeholder(18.sp, 18.sp, PlaceholderVerticalAlign.Center)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddCircle, // ＋アイコンなど
                            contentDescription = "Save Item",
                            modifier = Modifier.clickable {
                                val currentData = shindangunBunruiTensuhyo.firstOrNull()
                                if (currentData != null) {
                                    savedItems.add(
                                        SavedDpcItem(
                                            dpcCode = dpcCodeFirstJoined,
                                            tensuData = currentData,
                                            label = "DPC: $dpcCodeFirstJoined"
                                        )
                                    )
                                    // 次の病名を選べるようにリセット
                                    dpcCodeFirst = DpcCode()
                                    dpcScreenViewModel.resetAllSelections()
                                    bunruiIcdSelectedIcdItem = null
                                    // 3. 【重要】検索結果とクエリをクリアする
                                    query = "" // 検索窓の文字を消す
                                    searchResultsVisible = false // 検索結果リストを閉じる
                                    //searchResultsVisible = false
                                    //Toast.makeText(context, "リストに追加しました", Toast.LENGTH_SHORT).show()
                                }
                            },
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                )
            )
        }
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
                    Column() {
                        val isLoading by dpcScreenViewModel.isLoading.collectAsState()
                        MyCustomSearchBar(
                            searchQuery = query,
                            onSearchQueryChange = {
 input ->
                        query = input
                        if (input.length == 14) {
                            // 14桁入力されたらDPC検索を実行
                            dpcScreenViewModel.onDpcCodeInput(input)

                            // UI側の dpcCodeFirst も分解して更新する
                            dpcCodeFirst = DpcCode(
                                 mdc = input.substring(0, 2),
                             bunrui = input.substring(2, 6),
                             byotai = input.substring(6, 7),
                             nenrei = input.substring(7, 8),
                             shujutu = input.substring(8, 10),
                             shochi1 = input.substring(10, 11),
                             shochi2 = input.substring(11, 12),
                             fukushobyo = input.substring(12, 13),
                             jushodo = input.substring(13, 14),
                            )
                            // 選択肢を表示させるためのフラグをセット
                            bunruiIcdSelectedIcdItem = "DPC検索結果"
                            searchResultsVisible = false
                        } else {
                            // query = it
                            dpcScreenViewModel.onQueryChanged(input)
                        }

                                // クエリが空（クリア）になった場合
                                if (input.isBlank()) {
                                    // ★ すべての選択状態（フラグ）をViewModel側でリセット
                                    dpcScreenViewModel.resetAllSelections()

                                    dpcCodeFirst = DpcCode()
                                    bunruiIcdSelectedIcdItem = null
                                    searchResultsVisible = false
                                } else {
                                    searchResultsVisible = true
                                }
                            },
                            onSearch = { query = it },
                            isLoading = isLoading,
                            placeholderText = R.string.searchIcd
                        )
                        if (searchResultsVisible) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                            ) {
                                LazyColumn(
                                    state = lazyListState,
                                    modifier = Modifier
                                        .fillMaxSize()
                                    //.verticalScroll(rememberScrollState())
                                ) {
                                    // innerPadding -> の後の LazyColumn 内などで
                                    items(savedItems) { item ->
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                "保持中: ${item.dpcCode}",
                                                modifier = Modifier.weight(1f)
                                            )
                                            IconButton(onClick = { savedItems.remove(item) }) {
                                                Icon(
                                                    Icons.Default.Delete,
                                                    contentDescription = "削除"
                                                )
                                            }
                                        }
                                    }
                                    val icdItemsList = displayedItemsIcd.toList()
                                    // --- ICDコードの結果 ---
                                    if (icdItemsList.isNotEmpty()) {
                                        //icdItemsList.forEach { icdItem ->
                                        items(
                                            items = icdItemsList,
                                            key = {
                                                it.icdCode ?: it.hashCode().toString()
                                            }
                                        ) { icdItem ->
                                            MedGuidelinesCard(
                                                modifier = Modifier.padding(Dimensions.cardPadding)
                                            ) {
                                                Text(
                                                    text = icdItem.icdName
                                                        ?: "No Name", // itemTextの代わりに直接参照
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clickable {
                                                            // 1. UIの状態を更新
                                                            bunruiIcdSelectedIcdItem =
                                                                icdItem.icdName
                                                            // 2. Stateの更新は .copy() の結果を再代入する
                                                            dpcCodeFirst = dpcCodeFirst.copy(
                                                                icd = icdItem.icdCode,
                                                                mdc = icdItem.mdcCode,
                                                                bunrui = icdItem.bunruiCode
                                                            )

                                                            // 3. ViewModelのメソッドを呼び出してイベントを通知する
                                                            dpcScreenViewModel.onIcdItemSelected(
                                                                icdItem
                                                            )

                                                            Log.d(
                                                                "tamaiDpc",
                                                                " dpcScreenViewModel.onIcdItemSelected(icdItem) ran"
                                                            )
                                                            searchResultsVisible = false
                                                        }
                                                        .padding(16.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                                VerticalLazyScrollbar(
                                    listState = lazyListState,
                                    modifier = Modifier
                                        .align(Alignment.CenterEnd)
                                        .fillMaxHeight()
                                )
                            }
                        }
                        if (bunruiIcdSelectedIcdItem != null) {
                            Column(
                                modifier = Modifier
                                //.fillMaxSize()
                                //.verticalScroll(rememberScrollState())
                            ) {
                                MedGuidelinesCard(
                                    modifier = Modifier.padding(Dimensions.cardPadding)
                                ) {
                                    FlowRow(
                                        modifier = Modifier
                                            .padding(4.dp)
                                            .wrapContentHeight(
                                                align = Alignment.Bottom
                                            ),
                                        itemVerticalAlignment = Alignment.Bottom
                                    ) {
                                        InputValue(
                                            label = R.string.daysOfInHospital,
                                            value = days,
                                            japaneseUnit = R.string.days,
                                        )
                                        InputValue(
                                            label = R.string.coeffOfHospital,
                                            value = coeff,
                                            japaneseUnit = R.string.space,
                                        )
                                    }
                                }
                                MedGuidelinesCard(
                                    modifier = Modifier.padding(Dimensions.cardPadding)
                                ) {
                                    FlowRow(
                                        modifier = Modifier
                                            .padding(4.dp)
                                            .wrapContentHeight(
                                                align = Alignment.Bottom
                                            ),
                                        itemVerticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "ICD名称",
                                            fontSize = 16.sp,
                                            modifier = Modifier
                                                .padding(Dimensions.textPadding)

                                        )
                                        Text(
                                            text = "$bunruiIcdSelectedIcdItem",
                                            fontSize = 22.sp,
                                            modifier = Modifier
                                                .padding(Dimensions.textPadding)

                                        )
                                    }
                                }

                            }
                        }
                    }
                    if (!searchResultsVisible) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        ) {
                            val scrollState = rememberScrollState()
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(scrollState)
                            ) {
                                //item {
                                //入院日数と医療係数
                                //}
                                // --- 病態選択UI ---
                                // ViewModelのshowByotaiSelectionの値に基づいて、UIの表示を切り替える
                                //item {
                                if (showByotaiSelection) {
                                    Log.d("tamaiDpc", "after if (showByotaiSelection)")

                                    var expanded by remember { mutableStateOf(false) }
                                    // byotaiOptionsが更新されたらselectedOptionもリセットする
                                    var selectedOption by remember(byotaiOptions) {
                                        mutableStateOf(
                                            byotaiOptions.firstOrNull() ?: "選択してください"
                                        )
                                    }

                                    MedGuidelinesCard(modifier = Modifier.padding(vertical = 8.dp)) {
                                        ExposedDropdownMenuBox(
                                            expanded = expanded,
                                            onExpandedChange = { expanded = !expanded }
                                        ) {
                                            TextField(
                                                value = selectedOption,
                                                onValueChange = {},
                                                readOnly = true,
                                                trailingIcon = {
                                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                                        expanded = expanded
                                                    )
                                                },
                                                colors = ExposedDropdownMenuDefaults.textFieldColors(
                                                    unfocusedContainerColor = Color.Transparent,
                                                    focusedContainerColor = Color.Transparent
                                                ),
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .menuAnchor()
                                            )
                                            ExposedDropdownMenu(
                                                expanded = expanded,
                                                onDismissRequest = { expanded = false },
                                            ) {
                                                byotaiOptions.forEach { option ->
                                                    if (option.isNotBlank()) {
                                                        DropdownMenuItem(
                                                            text = { Text(option) },
                                                            onClick = {
                                                                selectedOption = option
                                                                expanded = false
                                                                // --- イベント: 病態名が選択された ---
                                                                coroutineScope.launch {
                                                                    val byotaiCode =
                                                                        dpcScreenViewModel.getByotaiCode(
                                                                            option
                                                                        )
                                                                    if (byotaiCode != null) {
                                                                        // 取得した病態コードでUIの状態を更新
                                                                        val finalByotaiCode =
                                                                            byotaiCode.toDoubleOrNull()
                                                                                ?.toInt()
                                                                                ?.toString()
                                                                                ?: byotaiCode
                                                                        dpcCodeFirst =
                                                                            dpcCodeFirst.copy(
                                                                                byotai = finalByotaiCode
                                                                            )
                                                                    }
                                                                }
                                                            }
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                //}
                                // --- 年齢選択UI ---
                                //item {
                                if (showNenreiSelection) {
                                    Log.d("tamaiDpc", "after if (showNenreiSelection)")

                                    // 1. ViewModelから年齢条件のリストを購読するval nenreiOptions by dpcScreenViewModel.nenreiOptions.collectAsState()
                                    val nenreiOptions by dpcScreenViewModel.nenreiOptions.collectAsState()

                                    // 2. 有効な選択肢が1つ以上ある場合のみUIを表示する
                                    if (nenreiOptions.isNotEmpty()) {

                                        var selectedlabelResId: String? by remember(
                                            nenreiOptions
                                        ) {
                                            mutableStateOf(nenreiOptions.first().labelResId.toString())
                                        }

                                        MedGuidelinesCard(
                                            modifier = Modifier
                                                .padding(Dimensions.cardPadding)
                                        ) {
                                            val selectedValue =
                                                buttonAndScoreWithScoreDisplayedSelectableLabelString(
                                                    optionsWithScores = nenreiOptions,
                                                    title = R.string.age,
                                                    // ★ defaultSelectedOptionは安全にリストの最初の要素を指定
                                                    defaultSelectedOption = selectedlabelResId,
                                                    onOptionSelected = { onSelected ->
                                                        selectedlabelResId = onSelected
                                                    },
                                                    isNumberDisplayed = false
                                                )
                                            LaunchedEffect(selectedlabelResId) {
                                                dpcCodeFirst =
                                                    dpcCodeFirst.copy(nenrei = selectedValue.toString())
                                            }
                                        }
                                    }
                                }
                                //}
                                //手術
                                //item {
                                if (showShujutsuSelection) {
                                    Log.d("tamaiDpc", "after if (showShujutsuSelection)")

                                    var expanded by remember { mutableStateOf(false) }
                                    // 1. ViewModelから年齢条件のリストを購読するval nenreiOptions by dpcScreenViewModel.nenreiOptions.collectAsState()
                                    val options by dpcScreenViewModel.shujutsuOptions.collectAsState()
                                    // 2. 有効な選択肢が1つ以上ある場合のみUIを表示する
                                    Log.d("tamaiDpc", "here?")

                                    LaunchedEffect(options) {
                                        if (options.isNotEmpty()) {
                                            val firstOptionName = options.first()
                                            if (firstOptionName.isNotBlank()) {
                                                val code = dpcScreenViewModel.getShujutsu1Code(
                                                    firstOptionName
                                                )
                                                if (code != null) {
                                                    val finalCode =
                                                        code.toDoubleOrNull()?.toInt()
                                                            ?.toString()
                                                            ?: code
                                                    dpcCodeFirst =
                                                        dpcCodeFirst.copy(shujutu = finalCode)
                                                    Log.d(
                                                        "tamaiDpc",
                                                        "shujutsu initialized with: $finalCode"
                                                    )
                                                }
                                            }
                                        }
                                    }
                                    DpcDropdownSelection(
                                        title = "手術",
                                        options = options,
                                        onOptionSelected = { selectedName ->
                                            coroutineScope.launch {
                                                val code =
                                                    dpcScreenViewModel.getShujutsu1Code(
                                                        selectedName
                                                    )
                                                if (code != null) {
                                                    val finalCode =
                                                        code.toDoubleOrNull()?.toInt()
                                                            ?.toString()
                                                            ?: code
                                                    // ★★★ dpcCodeFirstの更新先が shochi1 になっているか確認 ★★★
                                                    dpcCodeFirst =
                                                        dpcCodeFirst.copy(shujutu = finalCode) // shujutu -> shochi1 に
                                                }
                                            }
                                        }
                                    )
                                }
                                //}
                                //処置１
                                //item {
                                if (showShochi1Selection) {
                                    Log.d("tamaiDpc", "after if (showShochi1Selection)")
                                    val options by dpcScreenViewModel.shochi1Options.collectAsState()
                                    val labelIdList = options.map { it.shochi1Name }

                                    LaunchedEffect(options) {
                                        if (options.isNotEmpty()) {
                                            val firstItem = options.first()
                                            val initialCode =
                                                firstItem.code?.toDoubleOrNull()?.toInt()
                                                    ?.toString() ?: firstItem.code
                                            if (initialCode != null) {
                                                dpcCodeFirst =
                                                    dpcCodeFirst.copy(shochi1 = initialCode)
                                                Log.d(
                                                    "tamaiDpc",
                                                    "shochi1 initialized with: $initialCode"
                                                )
                                            }
                                        }
                                    }

                                    DpcDropdownSelection(
                                        title = "手術・処置等１",
                                        options = labelIdList,
                                        onOptionSelected = { selectedName ->
                                            coroutineScope.launch {
                                                // optionsリストの中から名称が一致する最初の項目を探し、そのcodeを取得する
                                                val code = options.find {
                                                    it.shochi1Name == selectedName
                                                }?.code
                                                if (code != null) {
                                                    val finalCode =
                                                        code.toDoubleOrNull()?.toInt()
                                                            ?.toString()
                                                            ?: code
                                                    dpcCodeFirst =
                                                        dpcCodeFirst.copy(shochi1 = finalCode) // shujutu -> shochi1 に
                                                }
                                            }
                                        }
                                    )
                                }
                                //}
                                //item {
                                if (showShochi2Selection) {
                                    Log.d("tamaiDpc", "after if (showShochi2Selection)")

                                    val options by dpcScreenViewModel.shochi2Options.collectAsState()
                                    val labelIdList = options.map { it.shochi1Name }

                                    LaunchedEffect(options) {
                                        if (options.isNotEmpty()) {
                                            val firstItem = options.first()
                                            val initialCode =
                                                firstItem.code?.toDoubleOrNull()?.toInt()
                                                    ?.toString() ?: firstItem.code
                                            if (initialCode != null) {
                                                dpcCodeFirst =
                                                    dpcCodeFirst.copy(shochi2 = initialCode)
                                                Log.d(
                                                    "tamaiDpc",
                                                    "shochi1 initialized with: $initialCode"
                                                )
                                            }
                                        }
                                    }
                                    DpcDropdownSelection(
                                        title = "手術・処置等２",
                                        options = labelIdList,
                                        onOptionSelected = { selectedName ->
                                            coroutineScope.launch {
                                                val code = options.find {
                                                    it.shochi1Name == selectedName
                                                }?.code
                                                if (code != null) {
                                                    val finalCode =
                                                        code.toDoubleOrNull()?.toInt()
                                                            ?.toString()
                                                            ?: code
                                                    // ★★★ dpcCodeFirstの更新先が shochi1 になっているか確認 ★★★
                                                    dpcCodeFirst =
                                                        dpcCodeFirst.copy(shochi2 = finalCode) // shujutu -> shochi1 に
                                                }
                                            }
                                        }
                                    )
                                }
                                //}
                                //副傷病名
                                //item {
                                if (showFukushobyoSelection) {
                                    Log.d("tamaiDpc", "after if (showFukushobyoSelection)")
                                    val options by dpcScreenViewModel.fukushobyoOptions.collectAsState()
                                    val labelIdList = options.map { it.name }

                                    LaunchedEffect(options) {
                                        if (options.isNotEmpty()) {
                                            val firstItem = options.first()
                                            val initialCode =
                                                firstItem.code?.toDoubleOrNull()?.toInt()
                                                    ?.toString() ?: firstItem.code
                                            if (initialCode != null) {
                                                dpcCodeFirst =
                                                    dpcCodeFirst.copy(fukushobyo = initialCode)
                                                Log.d(
                                                    "tamaiDpc",
                                                    "shochi1 initialized with: $initialCode"
                                                )
                                            }
                                        }
                                    }

                                    DpcDropdownSelection(
                                        title = "定義副傷病名",
                                        options = labelIdList,
                                        onOptionSelected = { selectedOption ->
                                            coroutineScope.launch {
                                                val code = options.find {
                                                    it.name == selectedOption
                                                }?.code
                                                if (code != null) {
                                                    val finalCode =
                                                        code.toDoubleOrNull()?.toInt()
                                                            ?.toString()
                                                            ?: code
                                                    // ★★★ dpcCodeFirstの更新先が shochi1 になっているか確認 ★★★
                                                    dpcCodeFirst =
                                                        dpcCodeFirst.copy(fukushobyo = finalCode) // shujutu -> shochi1 に
                                                }
                                            }
                                        }
                                    )
                                }
                                //}
                                //重症度JCS
                                //item {
                                if (showJushodoJcsSelection) {
                                    Log.d("tamaiDpc", "after if (showJushodoJcsSelection)")

                                    // 1. ViewModelから年齢条件のリストを購読するval nenreiOptions by dpcScreenViewModel.nenreiOptions.collectAsState()
                                    val options by dpcScreenViewModel.jushodoJcsOptions.collectAsState()

                                    // 2. 有効な選択肢が1つ以上ある場合のみUIを表示する
                                    if (options.isNotEmpty()) {
                                        var selectedOption: String? by remember(options) {
                                            mutableStateOf(options.first().labelResId)
                                        }
                                        MedGuidelinesCard(modifier = Modifier.padding(vertical = 8.dp)) {
                                            val title = if (options.first().label == "年齢") {
                                                R.string.age
                                            } else {
                                                R.string.jcs
                                            }
                                            Log.d("tamaiDpc", "title: $title")
                                            val value =
                                                buttonAndScoreWithScoreDisplayedSelectableLabelString(
                                                    optionsWithScores = options,
                                                    title = title,
                                                    //R.string.jushodoJcs,
                                                    // ★ defaultSelectedOptionは安全にリストの最初の要素を指定
                                                    defaultSelectedOption = selectedOption,
                                                    onOptionSelected = { newSelection ->
                                                        selectedOption = newSelection
                                                    },
                                                    isNumberDisplayed = false
                                                )
                                            LaunchedEffect(selectedOption) {
                                                dpcCodeFirst =
                                                    dpcCodeFirst.copy(jushodo = value.toString())
                                            }
                                        }
                                    }
                                }
                                //}
                                //重症度手術
                                //item {
                                Log.d(
                                    "tamaiDpc",
                                    "after if (showJushodoShujutsuSelection)　${showJushodoShujutsuSelection}"
                                )

                                if (showJushodoShujutsuSelection) {
                                    Log.d(
                                        "tamaiDpc",
                                        "after if (showJushodoShujutsuSelection)　${showJushodoShujutsuSelection}"
                                    )

                                    // 1. ViewModelから年齢条件のリストを購読するval nenreiOptions by dpcScreenViewModel.nenreiOptions.collectAsState()
                                    val options by dpcScreenViewModel.jushodoJcsOptions.collectAsState()

                                    // 2. 有効な選択肢が1つ以上ある場合のみUIを表示する
                                    if (options.isNotEmpty()) {
                                        var selectedOption: String? by remember(options) {
                                            mutableStateOf(options.first().labelResId)
                                        }
                                        MedGuidelinesCard(modifier = Modifier.padding(vertical = 8.dp)) {
                                            Log.d(
                                                "tamaiDpc",
                                                "title: $options.first().labelResId"
                                            )
                                            val value =
                                                buttonAndScoreWithScoreDisplayedSelectableLabelString(
                                                    optionsWithScores = options,
                                                    title = options.first().label,
                                                    //R.string.jushodoJcs,
                                                    // ★ defaultSelectedOptionは安全にリストの最初の要素を指定
                                                    defaultSelectedOption = selectedOption,
                                                    onOptionSelected = { newSelection ->
                                                        selectedOption = newSelection
                                                    },
                                                    isNumberDisplayed = false
                                                )
                                            LaunchedEffect(selectedOption) {
                                                dpcCodeFirst =
                                                    dpcCodeFirst.copy(jushodo = value.toString())
                                            }
                                        }
                                    }
                                }
                                //}
                                //重症度脳卒中
                                //item {
                                if (showJushodoStrokeSelection) {
                                    Log.d("tamaiDpc", "after if (showShochi1Selection)")

                                    val options by dpcScreenViewModel.jushodoStrokeOptions.collectAsState()
                                    // options から labelResId だけを抽出したリストを作成
                                    val labelIdList = options.map { it.labelResId }

                                    LaunchedEffect(options) {
                                        if (options.isNotEmpty()) {
                                            val firstItem = options.first()
                                            val initialCode = firstItem.code.toString()
                                                ?: firstItem.code.toString()
                                            if (initialCode != null) {
                                                dpcCodeFirst =
                                                    dpcCodeFirst.copy(jushodo = initialCode)
                                                Log.d(
                                                    "tamaiDpc",
                                                    "shochi1 initialized with: $initialCode"
                                                )
                                            }
                                        }
                                    }
                                    DpcDropdownSelection(
                                        title = options.first().label,
                                        options = labelIdList as List<String>,
                                        onOptionSelected = { selectedName ->
                                            coroutineScope.launch {
                                                val code = options.find {
                                                    it.labelResId == selectedName
                                                }?.code
                                                if (code != null) {
                                                    val finalCode =
                                                        code.toString()
                                                    dpcCodeFirst =
                                                        dpcCodeFirst.copy(jushodo = finalCode) // shujutu -> shochi1 に
                                                }
                                            }
                                        }
                                    )
                                }
                                //}
                            }
                            VerticalScrollbar(
                                scrollState = scrollState,
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .align(Alignment.CenterEnd)
                            ) // 今回追加した方
                        }
                    }

                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DpcDropdownSelection(
    options: List<String>,
    onOptionSelected: (
        selectedOption: String
    ) -> Unit,
    title: String
) {
    var expanded by remember { mutableStateOf(false) }
    // optionsリストが変更されたら、選択中の項目もリセットする
    var selectedOption by remember(options) {
        mutableStateOf(options.firstOrNull() ?: "選択してください")
    }

    if (options.isNotEmpty()) {
        MedGuidelinesCard(
            modifier = Modifier.padding(
                Dimensions.cardPadding
            )
        ) {
            Column() {
                Text(
                    text = title,
                    modifier = Modifier.padding(Dimensions.textPadding)
                )
                ExposedDropdownMenuBox(
                    expanded = expanded, onExpandedChange = { expanded = !expanded }
                ) {
                    TextField(
                        value = selectedOption,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = expanded
                            )
                        },
                        colors = ExposedDropdownMenuDefaults.textFieldColors(
                            unfocusedContainerColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                    ) {
                        options.forEach { option ->
                            if (option.isNotBlank()) {
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        selectedOption = option
                                        expanded = false
                                        //
                                        //選択された項目をコールバックで親に通知
                                        onOptionSelected(option)
                                    }
                                )
                            }
                        }
                    }
                }
            }

        }
    }
}

private fun calculateNyuinCost(
    days: Int,
    nyuinbiI: Int,
    nyuinbiII: Int,
    nyuinbiIII: Int,
    nyuinKikanI: Int,
    nyuinKikanII: Int,
    nyuinKikanIII: Int
): Int {
    val cost: Int
    if (days <= nyuinbiI) {
        cost = days.times(nyuinKikanI)
    } else if (days <= nyuinbiII) {
        cost = nyuinbiI.times(nyuinKikanI) +
                (days - nyuinbiI).times(nyuinKikanII)
    } else if (days <= nyuinbiIII) {
        cost =
            nyuinbiI.times(nyuinKikanI) +
                    (nyuinbiII - nyuinbiI).times(nyuinKikanII) +
                    (days - nyuinbiII).times(nyuinKikanIII)
    } else {
        cost =
            nyuinbiI.times(nyuinKikanI) +
                    (nyuinbiII - nyuinbiI).times(nyuinKikanII) +
                    (nyuinbiIII - nyuinbiII).times(nyuinKikanIII)
    }
    return cost
}

@Preview(showBackground = true) // 背景をtrueにすると見やすいです
@Composable
fun DpcScreenPreview() {

}
