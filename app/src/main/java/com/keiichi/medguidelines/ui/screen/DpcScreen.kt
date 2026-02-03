package com.keiichi.medguidelines.ui.screen

import android.icu.text.DecimalFormat
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.keiichi.medguidelines.data.ByotaiOptionEntity
import com.keiichi.medguidelines.data.IcdEntity
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
import com.keiichi.medguidelines.ui.viewModel.formatByotaiNenreiLabel
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

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
    var currentDpcCode by remember { mutableStateOf(DpcCode()) }
    val currentDpcCodeJoined by remember {
        derivedStateOf {
            listOfNotNull(
                currentDpcCode.mdc,
                currentDpcCode.bunrui,
                currentDpcCode.byotai,
                currentDpcCode.nenrei,
                currentDpcCode.shujutu,
                currentDpcCode.shochi1,
                currentDpcCode.shochi2,
                currentDpcCode.fukushobyo,
                currentDpcCode.jushodo
            ).joinToString("")
        }
    }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var loadingMessage by remember { mutableStateOf("Starting to load data...") }
    var showBunruiResults by remember { mutableStateOf(true) }
    val showByotaiSelection by dpcScreenViewModel.showByotaiSelection.collectAsState()
    val showNenreiSelection by dpcScreenViewModel.showNenreiSelection.collectAsState()
    val showShujutsuSelection by dpcScreenViewModel.showShujutsuSelection.collectAsState()
    val showShochi1Selection by dpcScreenViewModel.showShochi1Selection.collectAsState()
    val showShochi2Selection by dpcScreenViewModel.showShochi2Selection.collectAsState()
    val showFukushobyoSelection by dpcScreenViewModel.showFukushobyoSelection.collectAsState()
    val showJushodoJcsSelection by dpcScreenViewModel.showJushodoJcsSelection.collectAsState()
    val showJushodoShujutsuSelection by dpcScreenViewModel.showJushodoShujutsuSelection.collectAsState()
    val showJushodoStrokeSelection by dpcScreenViewModel.showJushodoStrokeSelection.collectAsState()
    val showIcdName by dpcScreenViewModel.showIcdName.collectAsState()
    val byotaiOptions by dpcScreenViewModel.byotaiOptions.collectAsState()
    var showSearchResults by remember { mutableStateOf(true) }
    val days = remember { mutableDoubleStateOf(1.0) }
    var cost = remember { mutableIntStateOf(0) }
    // UI表示用の状態変数
    val bunruiName = remember { mutableStateOf<String?>(null) }

    // currentDpcCode の mdc または bunrui が変わるたびに実行される
    LaunchedEffect(currentDpcCode.mdc, currentDpcCode.bunrui) {
        val mdc = currentDpcCode.mdc
        val bunrui = currentDpcCode.bunrui

        if (!mdc.isNullOrBlank() && !bunrui.isNullOrBlank() && mdc != "xx" && bunrui != "xxxx") {
            // ViewModel経由でDBから分類名を取得
            val name = dpcScreenViewModel.getBunruiNames(mdc, bunrui)
            if (name != null) {
                bunruiName.value = name
            }
        } else {
            // コードが初期状態（xxなど）の場合は名前もクリアする
            bunruiName.value = null
        }
    }

    // 1. ViewModelから永続化されている値を購読
    val savedCoeff by dpcScreenViewModel.hospitalCoeffFlow.collectAsState(initial = 1.3071)

// 2. UI表示・計算用のState。初期値に DataStore からの値を設定
    val coeff = remember { mutableDoubleStateOf(savedCoeff) }

// 3. 重要：DataStoreの値がロードされたらStateを更新する
    LaunchedEffect(savedCoeff) {
        if (coeff.doubleValue != savedCoeff) {
            coeff.doubleValue = savedCoeff
        }
    }

// 4. 重要：UI側で値が書き換えられたらDataStoreに保存する
    LaunchedEffect(coeff.doubleValue) {
        dpcScreenViewModel.saveHospitalCoeff(coeff.doubleValue)
    }
    var icdName by androidx.compose.runtime.remember {
        mutableStateOf<String?>(
            null
        )
    }
//    var bunruiName by androidx.compose.runtime.remember {
//        mutableStateOf<String?>(
//            null
//        )
//    }
    val coroutineScope = androidx.compose.runtime.rememberCoroutineScope()

    // データベースからの検索結果を購読
    val displayedItemsIcd by dpcScreenViewModel.displayedItemsIcd.collectAsState()
    val displayedItemsBunrui by dpcScreenViewModel.displayedItemsBunrui.collectAsState()

    var query by remember { mutableStateOf("細菌性肺炎") }

    val shindangunBunruiTensuhyo by dpcScreenViewModel.shindangunBunruiTensuhyoOptions.collectAsState()
    Log.d("tamaiDpcUI", "Current List Size in UI: ${shindangunBunruiTensuhyo.size}")

    val clipboard = LocalClipboardManager.current
    val context = LocalContext.current

    val COPY_ICON_ID = "copy_icon"
    val SAVE_ICON_ID = "save_icon"

    val lazyListState = rememberLazyListState()

    var icdNameList: List<String?> = remember { mutableListOf() }
    val icdItemsList = remember { mutableStateListOf<IcdEntity>() }

    LaunchedEffect(displayedItemsIcd) {
        icdItemsList.clear()
        icdItemsList.addAll(displayedItemsIcd)
    }

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
                (itemCost * 10 * coeff.doubleValue).roundToInt()
            }
        }
    }

    // 2. コードが確定したタイミング（例：ICD選択時や、特定のロジック後）でイベントを投げる
    LaunchedEffect(currentDpcCodeJoined) {
        if (currentDpcCodeJoined.isNotEmpty()) {
            Log.d("tamaiDpc", "dpcCodeFirst: $currentDpcCodeJoined")
            // 引数 code を使わず、DBの最初の3行を取得する
            val debugResults = dpcScreenViewModel.getDebugRows()
            debugResults.forEach {
                Log.d("tamaiDpc", "DB stored pattern: $it, ")
            }
            dpcScreenViewModel.onShindangunCodeChanged(currentDpcCodeJoined)
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
            val secondaryColor = MaterialTheme.colorScheme.tertiaryContainer
            val primaryColor = MaterialTheme.colorScheme.onPrimary

            ScoreBottomAppBarVariable(
                modifier = Modifier,
                displayText = buildAnnotatedString {
                    withStyle(
                        style = ParagraphStyle(
                            lineHeight = 1.2.em
                        )
                    ) {
                        withStyle(
                            style = SpanStyle(
                                fontSize = 16.sp,
                            ),
                        ) {
                            if (savedTotalAmount != 0) {
                                // 履歴（セカンダリカラー適用範囲）
                                withStyle(
                                    style = SpanStyle(
                                        color = secondaryColor
                                    )
                                ) {
                                    savedItems.lastOrNull()?.let { lastItem ->
                                        append("DPC病名: ")
                                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                            append(lastItem.tensuData.name)
                                        }
                                        withStyle(style = SpanStyle(letterSpacing = 16.sp)) {
                                            append(
                                                " "
                                            )
                                        }
                                        append("DPCコード: ${lastItem.dpcCode} ")
                                        appendInlineContent(COPY_ICON_ID, "[copy]")
                                        //append("\n")
                                    }
                                    withStyle(style = SpanStyle(letterSpacing = 16.sp)) { append(" ") }
                                    append(" 包括金額合計: ${"%,d".format(savedTotalAmount)}円")
                                    if (days.doubleValue.toInt() > (savedItems.lastOrNull()?.tensuData?.nyuinbiIII?.toInt()
                                            ?: 0)
                                    ) {
                                        withStyle(
                                            style = SpanStyle(
                                                fontSize = 16.sp,
                                                color = secondaryColor
                                            )
                                        ) {
                                            append("（${savedItems.lastOrNull()?.tensuData?.nyuinbiIII?.toInt()}日まで）")
                                            append("\n")
                                        }
                                    }
                                }
                            }
                            if (bunruiName.value != null) {
                                bunruiName?.let { name ->
                                    withStyle(
                                        style = SpanStyle(
                                            fontWeight = FontWeight.Normal,
                                            color = primaryColor,
                                            fontSize = 16.sp
                                        )
                                    ) {
                                        append("DPC病名: ")
                                    }
                                    withStyle(
                                        style = SpanStyle(
                                            fontWeight = FontWeight.Bold,
                                            color = primaryColor,
                                            fontSize = 16.sp
                                        )
                                    ) {
                                        append("${name.value}\n")
                                    }
                                }
                            }
                            withStyle(
                                style = SpanStyle(
                                    color = primaryColor,
                                    fontSize = 16.sp
                                )
                            ) {
                                append(" DPCコード: ")
                                append(currentDpcCodeJoined)
                                append(" ") // 少し隙間をあける
                            }
                            appendInlineContent(COPY_ICON_ID, "[copy]")
                            append("\n")
                            withStyle(
                                style = SpanStyle(
                                    color = primaryColor,
                                    fontSize = 16.sp
                                )
                            ) {
                                if (shindangunBunruiTensuhyo.isNotEmpty()) {
                                    val data = shindangunBunruiTensuhyo.firstOrNull()
                                    //append("\n")
                                    if (data?.nyuinbiI != "") {
                                        append("入院期間I: ${data?.nyuinbiI} ")
                                    }
                                    if (data?.nyuinbiII != "") {
                                        append("入院期間II: ${data?.nyuinbiII} ")
                                    }
                                    if (data?.nyuinbiIII != "") {
                                        append("入院期間III: ${data?.nyuinbiIII}")
                                    }
                                }
                            }
                        }
                        if (isDataValid && shindangunBunruiTensuhyo.isNotEmpty()) {
                            Log.d("tamaiDpc", "shindangunBunruiTensuhyo: $shindangunBunruiTensuhyo")

                            val data = shindangunBunruiTensuhyo.firstOrNull()
                            cost.intValue = calculateNyuinCost(
                                days = days.doubleValue.toInt(),
                                nyuinbiI = data?.nyuinbiI?.toIntOrNull() ?: 0,
                                nyuinbiII = data?.nyuinbiII?.toIntOrNull() ?: 0,
                                nyuinbiIII = data?.nyuinbiIII?.toIntOrNull() ?: 0,
                                nyuinKikanI = data?.nyuinKikanI?.toIntOrNull() ?: 0,
                                nyuinKikanII = data?.nyuinKikanII?.toIntOrNull() ?: 0,
                                nyuinKikanIII = data?.nyuinKikanIII?.toIntOrNull() ?: 0,
                            )
                            val currentTotalAmount =
                                (cost.intValue * 10 * coeff.doubleValue).roundToInt()
                            withStyle(
                                style = SpanStyle(
                                    color = primaryColor,
                                    fontSize = 26.sp
                                )
                            ) {
                                append("\n包括金額合計: ${"%,d".format(currentTotalAmount)}円")
                                append(" ")
                            }
                            appendInlineContent(SAVE_ICON_ID, "[save]")
                            Log.d("tamaiDpc", "savedTotalAmount: $savedTotalAmount")
//                        if (savedTotalAmount != 0) {
//                            append("包括金額合計: ${"%,d".format(savedTotalAmount)}円")
//                        }
                            if (days.doubleValue.toInt() > (data?.nyuinbiIII?.toInt() ?: 0)) {
                                withStyle(
                                    style = SpanStyle(
                                        fontSize = 16.sp,
                                        color = primaryColor
                                    )
                                ) {
                                    append("（${data?.nyuinbiIII?.toInt()}日まで）")
                                }
                            }
                        } else {
                            withStyle(
                                style = SpanStyle(
                                    color = primaryColor
                                )
                            ) {
                                if (currentDpcCode == DpcCode() || currentDpcCode.icd == "x"
                                )
                                    append("")
                                //append("\n(病名を選択してください)")
                                else {
                                    append("\n出来高算定")
                                    Log.d("tamaiDpc", "dpcCodeFirst: $currentDpcCode")
                                }
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
                                if (currentDpcCodeJoined.isNotEmpty()) {
                                    clipboard.setText(AnnotatedString(currentDpcCodeJoined))
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
                        Placeholder(
                            22.sp, 22.sp,
                            PlaceholderVerticalAlign.Center
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddCircle, // ＋アイコンなど
                            contentDescription = "Save Item",
                            modifier = Modifier.clickable {
                                val currentData = shindangunBunruiTensuhyo.firstOrNull()
                                if (currentData != null) {
                                    savedItems.add(
                                        SavedDpcItem(
                                            dpcCode = currentDpcCodeJoined,
                                            tensuData = currentData,
                                            label = "DPC: $currentDpcCodeJoined"
                                        )
                                    )
                                    // 次の病名を選べるようにリセット
                                    currentDpcCode = DpcCode()
                                    dpcScreenViewModel.resetAllSelections()
                                    bunruiName.value = null
                                    icdName = null
                                    // 3. 【重要】検索結果とクエリをクリアする
                                    query = "" // 検索窓の文字を消す
                                    showSearchResults = false // 検索結果リストを閉じる
                                    //searchResultsVisible = false
                                    Toast.makeText(
                                        context,
                                        "次の病名を選んでください",
                                        Toast.LENGTH_SHORT
                                    ).show()
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
                            onSearchQueryChange = { input ->
                                query = input
                                val parts = input.split(" ")
                                if (parts.size == 2 && parts[0].length == 2 && parts[1].length == 4) {
                                    coroutineScope.launch {
                                        // 分類に対応するICDリストをViewModelから取得して表示
                                        //icdItemsList = dpcScreenViewModel.searchIcdByMcdAndBunrui(parts[0], parts[1])
                                        val names = dpcScreenViewModel.searchIcdByMcdAndBunrui(
                                            parts[0],
                                            parts[1]
                                        )

                                        // 2. 取得した名称を IcdEntity に変換して代入

                                        val newEntities = names.filterNotNull().map { name ->
                                            IcdEntity(
                                                mdcCode = parts[0],
                                                bunruiCode = parts[1],
                                                icdName = name,
                                                icdCode = "",        // 必要に応じて初期値を設定
                                                normalizedIcdName = ""
                                            )
                                        }
                                        icdItemsList.clear()    // 今までの分を消す
                                        icdItemsList.addAll(newEntities) // 新しい分を入れる
                                        showSearchResults = true
                                        showBunruiResults = false
                                    }
                                } else if (input.length == 14 && input.all { it.isDigit() || it == 'x' }) {
                                    // 14桁入力されたらDPC検索を実行
                                    dpcScreenViewModel.onDpcCodeSearched(input)
                                    dpcScreenViewModel.onDpcCodeInput(input)
                                    Log.d(
                                        "tamaiDpc",
                                        "dpcScreenViewModel.onDpcCodeInput(input) ran"
                                    )
                                    // UI側の dpcCodeFirst も分解して更新する
                                    currentDpcCode = DpcCode(
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
                                    coroutineScope.launch {
                                        try {

                                            // 病名（分類名）の取得
                                            val name = dpcScreenViewModel.getBunruiNames(
                                                currentDpcCode.mdc ?: "",
                                                currentDpcCode.bunrui ?: ""
                                            )
                                            Log.d("tamaiDpc", "got Name, bunruiName: $name")
                                            if (name != null) {
                                                icdName = name
                                                // 点数データの検索（bottom barに金額を表示させるため）
                                                dpcScreenViewModel.onDpcCodeInput(input)
                                            } else {
                                                icdName = "該当する病名が見つかりません"
                                                Toast.makeText(
                                                    context,
                                                    "無効なDPCコードです",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                showSearchResults = true // 見つからない場合はリストに戻す
                                            }
                                        } catch (e: Exception) {
                                            Log.e("tamaiDpc", "DPC Search Error", e)
                                        }
                                    }
                                    Log.d("tamaiDpc", "got Name, bunruiName: $icdName")
                                    showSearchResults = false
                                } else {
                                    // query = it
                                    dpcScreenViewModel.onQueryChanged(input)
                                    showSearchResults = true
                                    showBunruiResults = true
                                }

                                // クエリが空（クリア）になった場合
                                if (input.isBlank()) {
                                    // ★ すべての選択状態（フラグ）をViewModel側でリセット
                                    dpcScreenViewModel.resetAllSelections()
                                    bunruiName.value = null
                                    //icdName = null
                                    currentDpcCode = DpcCode()
                                    icdName = null
                                    showSearchResults = false
                                    showBunruiResults = false
                                }
                            },
                            onSearch = { query = it },
                            isLoading = isLoading,
                            placeholderText = R.string.searchIcd
                        )
                        Log.d(
                            "tamaiDpc",
                            "after MyCustomSearchBar, showSearchResults: $showSearchResults"
                        )
                        if (showSearchResults) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                            ) {
                                LazyColumn(
                                    state = lazyListState,
                                    modifier = Modifier
                                        .fillMaxSize()
                                ) {
                                    val bunruiItemsList = displayedItemsBunrui.toList()
                                    // --- ICDコードの結果 ---
                                    if (icdItemsList.isNotEmpty()) {
                                        //icdItemsList.forEach { icdItem ->
                                        items(
                                            items = icdItemsList,
                                            //key = { it.id }
                                        ) { icdItem ->
                                            MedGuidelinesCard(
                                                modifier = Modifier.padding(Dimensions.cardPadding),
                                                containerColor = MaterialTheme.colorScheme.onPrimary,
                                                contentColor = MaterialTheme.colorScheme.primary
                                            ) {
                                                Text(
                                                    text = icdItem.icdName
                                                        ?: "No Name", // itemTextの代わりに直接参照
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clickable {
                                                            // 1. UIの状態を更新
                                                            icdName =
                                                                icdItem.icdName
                                                            // 2. Stateの更新は .copy() の結果を再代入する
                                                            currentDpcCode = currentDpcCode.copy(
                                                                icd = icdItem.icdCode,
                                                                mdc = icdItem.mdcCode,
                                                                bunrui = icdItem.bunruiCode
                                                            )
                                                            dpcScreenViewModel.onIcdItemSelected(
                                                                icdItem
                                                            )
                                                            coroutineScope.launch {
                                                                val name =
                                                                    dpcScreenViewModel.getBunruiNames(
                                                                        icdItem.mdcCode,
                                                                        icdItem.bunruiCode
                                                                    )
                                                                bunruiName.value = name
                                                            }
                                                            Log.d(
                                                                "tamaiDpc",
                                                                " dpcScreenViewModel.onIcdItemSelected(icdItem) ran"
                                                            )
                                                            showSearchResults = false
                                                        }
                                                        .padding(16.dp)
                                                )
                                            }
                                        }
                                    }
                                    if (showBunruiResults) {
                                        if (bunruiItemsList.isNotEmpty()) {
                                            //icdItemsList.forEach { icdItem ->
                                            items(
                                                items = bunruiItemsList,
                                                //key = { it.id,item -> "${item.id}_$it.id" }
                                            ) { item ->
                                                MedGuidelinesCard(
                                                    modifier = Modifier.padding(Dimensions.cardPadding),
                                                    containerColor = MaterialTheme.colorScheme.onTertiary,
                                                    contentColor = MaterialTheme.colorScheme.tertiary
                                                ) {
                                                    Text(
                                                        text = item.bunruiName
                                                            ?: "No Name", // itemTextの代わりに直接参照
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .clickable {
                                                                // 1. UIの状態を更新
                                                                bunruiName.value =
                                                                    item.bunruiName
                                                                // 2. Stateの更新は .copy() の結果を再代入する
                                                                currentDpcCode =
                                                                    currentDpcCode.copy(
                                                                        //icd = item.icdCode,
                                                                        mdc = item.mdcCode,
                                                                        bunrui = item.bunruiCode
                                                                    )
                                                                coroutineScope.launch {
                                                                    // 分類に関連するICDリストをバックグラウンドで取得
                                                                    val names =
                                                                        dpcScreenViewModel.searchIcdByMcdAndBunrui(
                                                                            item.mdcCode ?: "",
                                                                            item.bunruiCode ?: ""
                                                                        )
                                                                }
                                                                coroutineScope.launch {
                                                                    icdNameList =
                                                                        dpcScreenViewModel.searchIcdByMcdAndBunrui(
                                                                            item.mdcCode,
                                                                            item.bunruiCode
                                                                        )

                                                                }
                                                                icdNameList
                                                                    .filterNotNull() // null を除外（必要に応じて）
                                                                    .map { name ->
                                                                        IcdEntity(
                                                                            mdcCode = item.mdcCode,
                                                                            bunruiCode = item.bunruiCode,
                                                                            icdName = name,
                                                                            icdCode = "",
                                                                            normalizedIcdName = ""
                                                                        )
                                                                    }
                                                                val mdc = item.mdcCode ?: ""
                                                                val bunrui = item.bunruiCode ?: ""
                                                                query =
                                                                    "${item.mdcCode} ${item.bunruiCode}"
                                                                coroutineScope.launch {
                                                                    // ICDリストを取得して検索結果として表示
                                                                    val names =
                                                                        dpcScreenViewModel.searchIcdByMcdAndBunrui(
                                                                            mdc,
                                                                            bunrui
                                                                        )
                                                                    val newEntities =
                                                                        names.filterNotNull()
                                                                            .map { name ->
                                                                                IcdEntity(
                                                                                    mdcCode = mdc,
                                                                                    bunruiCode = bunrui,
                                                                                    icdName = name,
                                                                                    icdCode = "",
                                                                                    normalizedIcdName = ""
                                                                                )
                                                                            }
                                                                    icdItemsList.clear()
                                                                    icdItemsList.addAll(newEntities)
                                                                }

                                                                Log.d(
                                                                    "tamaiDpc",
                                                                    " dpcScreenViewModel.onIcdItemSelected(icdItem) ran"
                                                                )
                                                                showBunruiResults = false
                                                                //showSearchResults = false
                                                            }
                                                            .padding(16.dp)

                                                    )
                                                }
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
                        if (showIcdName) {
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
                                            formatter = remember { DecimalFormat("#.####") },
                                        )
                                    }
                                }
                                MedGuidelinesCard(
                                    modifier = Modifier.padding(Dimensions.cardPadding)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .padding(4.dp),
                                    ) {
                                        Text(
                                            text = "ICD名称",
                                            fontSize = 16.sp,
                                            modifier = Modifier
                                                .padding(Dimensions.textPadding)
                                        )
                                        Text(
                                            text = "$icdName",
                                            fontSize = 22.sp,
                                            modifier = Modifier
                                                .padding(Dimensions.textPadding)

                                        )
                                    }
                                }

                            }
                        }
                    }
                    if (!showSearchResults) {
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
                                if (showByotaiSelection) {
                                    Log.d("tamaiDpc", "after if (showByotaiSelection)")

                                    var expanded by remember { mutableStateOf(false) }
                                    var selectedLabel by remember(byotaiOptions) {
                                        mutableStateOf(byotaiOptions.firstOrNull()?.labelResId ?: "")
                                    }
                                    var selectedCode by remember(byotaiOptions) {
                                        mutableIntStateOf(byotaiOptions.firstOrNull()?.code ?: 0)
                                    }
                                    var selectedLabelResId: String? by remember(
                                        byotaiOptions
                                    ) {
                                        mutableStateOf(
                                            if (currentDpcCode.nenrei != "x") {
                                                byotaiOptions.find { it.code.toString() == currentDpcCode.nenrei }?.labelResId?.toString()
                                                    ?: byotaiOptions.firstOrNull()?.labelResId?.toString()
                                            } else {
                                                byotaiOptions.first().labelResId.toString()
                                            }
                                        )
                                    }
//                                    // 最初の要素が自動選択されたことを currentDpcCode にも反映させるための処理
//                                    LaunchedEffect(byotaiOptions) {
//                                        byotaiOptions.firstOrNull()?.let { firstOption ->
//                                            currentDpcCode = currentDpcCode.copy(
//                                                byotai = firstOption.code.toString()
//                                            )
//                                        }
//                                    }

                                    LaunchedEffect(selectedCode) {
                                        currentDpcCode = currentDpcCode.copy(
                                            byotai = selectedCode.toString()
                                        )
                                    }
                                    if (byotaiOptions.any { !it.labelResId.isNullOrBlank() }) {
                                        MedGuidelinesCard(modifier = Modifier.padding(vertical = 8.dp)) {
                                            ExposedDropdownMenuBox(
                                                expanded = expanded,
                                                onExpandedChange = { expanded = !expanded }
                                            ) {
                                                TextField(
                                                    value = selectedLabelResId ?: "", //?: "選択してください",
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
                                                    byotaiOptions.forEach { optionEntity ->
                                                        // 1. 表示用のラベルを作成
                                                        val displayLabel = optionEntity.labelResId
                                                        DropdownMenuItem(
                                                            text = { Text(displayLabel ?: "") },
                                                            onClick = {
                                                                selectedCode = optionEntity.code
                                                                selectedLabel =
                                                                    optionEntity.labelResId ?: "選択してください"
                                                                expanded = false
                                                                val rawCode = optionEntity.code

                                                                // フォーマット（数値変換せず、空文字チェックのみで安全に）
                                                                if (rawCode != null) {
                                                                    currentDpcCode =
                                                                        currentDpcCode.copy(
                                                                            // 先頭の0を維持するため、そのままか、必要ならpadStartを使う
                                                                            byotai = rawCode.toString()
                                                                        )
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
                                    val nenreiOptions by dpcScreenViewModel.nenreiOptions.collectAsState()
                                    if (nenreiOptions.isNotEmpty()) {
                                        var selectedlabelResId: String? by remember(
                                            nenreiOptions
                                        ) {
                                            mutableStateOf(
                                                if (currentDpcCode.nenrei != "x") {
                                                    nenreiOptions.find { it.code.toString() == currentDpcCode.nenrei }?.labelResId?.toString()
                                                        ?: nenreiOptions.firstOrNull()?.labelResId?.toString()
                                                } else {
                                                    nenreiOptions.first().labelResId.toString()
                                                }
                                            )
                                        }
                                        MedGuidelinesCard(
                                            modifier = Modifier
                                                .padding(Dimensions.cardPadding)
                                        ) {
                                            // 1. byotaiの値に応じて表示する選択肢を絞り込む
                                            val filteredNenreiOptions =
                                                remember(currentDpcCode.byotai, nenreiOptions, showNenreiSelection) {
                                                    when (currentDpcCode.byotai) {
                                                        "1" -> {
                                                            // 例：byotaiが1の時は、特定の条件（例：labelResIdが子供向けのものなど）のみ
                                                            // または、最初からn番目までを表示するなど
                                                            nenreiOptions.take(2) // スコアがあるものだけに絞る例
                                                        }

                                                        "0", "2" -> {
                                                            // 例：特定のIDを除外する、あるいは特定の範囲だけに絞る
                                                            nenreiOptions.takeLast(3) // 最初の2つだけに絞る例
                                                        }

                                                        else -> {
                                                            nenreiOptions // それ以外は全件表示
                                                        }
                                                    }
                                                }
                                            LaunchedEffect(filteredNenreiOptions) {
                                                // 絞り込まれたリストに、今の選択肢が含まれていなければ、デフォルトを選び直す
                                                if (filteredNenreiOptions.none { it.labelResId == selectedlabelResId }) {
                                                    selectedlabelResId =
                                                        filteredNenreiOptions.firstOrNull()?.labelResId
                                                }
                                            }
                                            val selectedValue =
                                                buttonAndScoreWithScoreDisplayedSelectableLabelString(
                                                    optionsWithScores = filteredNenreiOptions,
                                                    title = R.string.age,
                                                    // ★ defaultSelectedOptionは安全にリストの最初の要素を指定
                                                    defaultSelectedOption = selectedlabelResId
//                                                        when (currentDpcCode.byotai) {
//                                                            "1"    -> nenreiOptions.first().labelResId
//                                                            "0", "2" -> nenreiOptions.last().labelResId
//                                                            else     -> selectedlabelResId
//                                                        }
                                                    ,
                                                    onOptionSelected = { onSelected ->
                                                        selectedlabelResId = onSelected
                                                    },
                                                    isNumberDisplayed = false
                                                )
                                            LaunchedEffect(selectedlabelResId) {
                                                currentDpcCode =
                                                    currentDpcCode.copy(nenrei = selectedValue.toString())
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

                                    // 1. currentLabel を状態として定義
                                    var currentLabel by remember { mutableStateOf<String?>(null) }

// 2. options または shujutu が変わるたびに、ラベルを検索して更新する
                                    LaunchedEffect(options, currentDpcCode.shujutu) {
                                        Log.d("tamaiDpc", "Updating currentLabel for shujutu: ${currentDpcCode.shujutu}")

                                        // options の中から現在のコードに一致する名前を探す
                                        currentLabel = options.find { optionName ->
                                            val code = dpcScreenViewModel.getShujutsu1Code(
                                                optionName,
                                                mdcCode = currentDpcCode.mdc,
                                                bunruiCode = currentDpcCode.bunrui
                                            )
                                            code == currentDpcCode.shujutu
                                        }

                                        Log.d("tamaiDpc", "Resulting currentLabel: $currentLabel")
                                    }

                                    LaunchedEffect(options) {
                                        if (options.isNotEmpty()) {
                                            // ★ 修正ポイント:
                                            // 現在の shujutu がデフォルト ("xx" または空) の場合のみ、
                                            // リストの最初の項目をセットするようにガードを入れる
                                            if (currentDpcCode.shujutu == "xx" || currentDpcCode.shujutu.isNullOrBlank()) {
                                                val firstOptionName = options.first()
                                                if (firstOptionName.isNotBlank()) {
                                                    val code = dpcScreenViewModel.getShujutsu1Code(
                                                        shujutsu1Name = firstOptionName,
                                                        mdcCode = currentDpcCode.mdc,
                                                        bunruiCode = currentDpcCode.bunrui
                                                    )
                                                    if (code != null) {
                                                        currentDpcCode = currentDpcCode.copy(shujutu = code)
                                                        Log.d(
                                                            "tamaiDpc",
                                                            "shujutsu initialized with: $code"
                                                        )
                                                    }
                                                }
                                            } else {
                                                // DPCコード検索などで既に shujutu に値 ("xx"以外) が入っている場合は、
                                                // そのまま保持し、勝手に初期化しない
                                                Log.d("tamaiDpc", "shujutsu skip initialization. current: ${currentDpcCode.shujutu}")
                                            }
                                        }
                                    }
                                    key(currentLabel) {
                                        DpcDropdownSelection(
                                            title = "手術",
                                            options = options,
                                            initialSelection = currentLabel,
                                            onOptionSelected = { selectedName ->
                                                coroutineScope.launch {
                                                    val code =
                                                        dpcScreenViewModel.getShujutsu1Code(
                                                            selectedName,
                                                            mdcCode = currentDpcCode.mdc,
                                                            bunruiCode = currentDpcCode.bunrui
                                                        )
                                                    if (code != null) {
                                                        // ★★★ dpcCodeFirstの更新先が shochi1 になっているか確認 ★★★
                                                        currentDpcCode =
                                                            currentDpcCode.copy(shujutu = code) // shujutu -> shochi1 に
                                                    }
                                                }
                                            }
                                        )
                                    }
                                }
                                //}
                                //処置１
                                //item {
                                if (showShochi1Selection) {
                                    Log.d("tamaiDpc", "after if (showShochi1Selection)")
                                    val options by dpcScreenViewModel.shochi1Options.collectAsState()
                                    val labelIdList = options.map { it.shochi1Name }
                                    val currentLabel = options.find {
                                        val finalCode = it.code
                                        finalCode == currentDpcCode.shochi1
                                    }?.shochi1Name

                                    LaunchedEffect(options) {
                                        if (options.isNotEmpty()) {
                                            val isCurrentCodeValid = options.any { it.code == currentDpcCode.shochi1 }
                                            if (!isCurrentCodeValid) {
                                                val initialCode = options.first().code
                                                if (initialCode != null) {
                                                    currentDpcCode =
                                                        currentDpcCode.copy(shochi1 = initialCode)
                                                    Log.d(
                                                        "tamaiDpc",
                                                        "shochi1 initialized with: $initialCode"
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    DpcDropdownSelection(
                                        title = "手術・処置等１",
                                        options = labelIdList,
                                        initialSelection = currentLabel,
                                        onOptionSelected = { selectedName ->
                                            coroutineScope.launch {
                                                // optionsリストの中から名称が一致する最初の項目を探し、そのcodeを取得する
                                                val code = options.find {
                                                    it.shochi1Name == selectedName
                                                }?.code
                                                if (code != null) {
                                                    currentDpcCode =
                                                        currentDpcCode.copy(shochi1 = code) // shujutu -> shochi1 に
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

                                    val currentLabel = options.find {
                                        val finalCode = it.code
                                        finalCode == currentDpcCode.shochi2
                                    }?.shochi1Name

                                    LaunchedEffect(options) {
                                        if (options.isNotEmpty()) {
                                            val isCurrentCodeValid = options.any { it.code == currentDpcCode.shochi1 }
                                            if (!isCurrentCodeValid) {
                                                val initialCode = options.first().code
                                                if (initialCode != null) {
                                                    currentDpcCode =
                                                        currentDpcCode.copy(shochi2 = initialCode)
                                                    Log.d(
                                                        "tamaiDpc",
                                                        "shochi1 initialized with: $initialCode"
                                                    )
                                                }
                                            }
                                        }
                                    }
                                    DpcDropdownSelection(
                                        title = "手術・処置等２",
                                        options = labelIdList,
                                        initialSelection = currentLabel,
                                        onOptionSelected = { selectedName ->
                                            coroutineScope.launch {
                                                val code = options.find {
                                                    it.shochi1Name == selectedName
                                                }?.code
                                                if (code != null) {
                                                    // ★★★ dpcCodeFirstの更新先が shochi1 になっているか確認 ★★★
                                                    currentDpcCode =
                                                        currentDpcCode.copy(shochi2 = code) // shujutu -> shochi1 に
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

                                    val currentLabel = options.find {
                                        val finalCode = it.code
                                        finalCode == currentDpcCode.fukushobyo
                                    }?.name

                                    LaunchedEffect(options) {
                                        if (options.isNotEmpty()) {
                                            val isCurrentCodeValid = options.any { it.code == currentDpcCode.shochi1 }
                                            if (!isCurrentCodeValid) {
                                                val initialCode = options.first().code
                                                if (initialCode != null) {
                                                    currentDpcCode =
                                                        currentDpcCode.copy(fukushobyo = initialCode)
                                                    Log.d(
                                                        "tamaiDpc",
                                                        "shochi1 initialized with: $initialCode"
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    DpcDropdownSelection(
                                        title = "定義副傷病名",
                                        options = labelIdList,
                                        initialSelection = currentLabel,
                                        onOptionSelected = { selectedOption ->
                                            coroutineScope.launch {
                                                val code = options.find {
                                                    it.name == selectedOption
                                                }?.code
                                                if (code != null) {
                                                    // ★★★ dpcCodeFirstの更新先が shochi1 になっているか確認 ★★★
                                                    currentDpcCode =
                                                        currentDpcCode.copy(fukushobyo = code) // shujutu -> shochi1 に
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
                                        // ★ 追加: 現在の dpcCodeFirst.jushodo (コード) に一致する labelResId を探す
                                        val currentLabel = options.find {
                                            it.code.toString() == currentDpcCode.jushodo
                                        }?.labelResId

                                        // rememberのキーに options と currentLabel を追加し、データ復元時にUIが更新されるようにする
                                        var selectedOption: String? by remember(
                                            options,
                                            currentLabel
                                        ) {
                                            mutableStateOf(
                                                currentLabel ?: options.first().labelResId
                                            )
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
                                                currentDpcCode =
                                                    currentDpcCode.copy(jushodo = value.toString())
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
                                    val options by dpcScreenViewModel.jushodoShujutsuOptions.collectAsState()

                                    // 2. 有効な選択肢が1つ以上ある場合のみUIを表示する
                                    if (options.isNotEmpty()) {
                                        // ★ 追加: 現在のコードに対応する名称（labelResId）を探す
                                        val currentLabel = options.find {
                                            it.code.toString() == currentDpcCode.jushodo
                                        }?.labelResId

                                        // optionsやcurrentLabelが変わった時に再計算
                                        var selectedOption: String? by remember(
                                            options,
                                            currentLabel
                                        ) {
                                            mutableStateOf(
                                                currentLabel ?: options.first().labelResId
                                            )
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
                                                currentDpcCode =
                                                    currentDpcCode.copy(jushodo = value.toString())
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
                                    val currentLabel = options.find {
                                        it.code.toString() == currentDpcCode.jushodo
                                    }?.labelResId

                                    LaunchedEffect(options) {
                                        if (options.isNotEmpty()) {// ★ 修正ポイント:
                                            // 現在の jushodo がデフォルト ("x" または空) の場合のみ、リストの最初をセットする。
                                            // DPCコード検索などで既に具体的な値 ("x"以外) が入っている場合は上書きしない。
                                            if (currentDpcCode.jushodo == "x" || currentDpcCode.jushodo.isNullOrBlank()) {
                                                val firstItem = options.first()
                                                val initialCode = firstItem.code.toString()

                                                if (initialCode != null) {
                                                    currentDpcCode = currentDpcCode.copy(jushodo = initialCode)
                                                    Log.d(
                                                        "tamaiDpc",
                                                        "jushodo (stroke) initialized with default: $initialCode"
                                                    )
                                                }
                                            } else {
                                                // 既に値がある場合は、マスターリストの中にそのコードが存在するかだけ確認し、
                                                // 存在しない（無効な）場合のみ初期化する、というガードを入れるとより安全です
                                                val isCurrentCodeValid = options.any { it.code.toString() == currentDpcCode.jushodo }
                                                if (!isCurrentCodeValid) {
                                                    val firstItem = options.first()
                                                    currentDpcCode = currentDpcCode.copy(jushodo = firstItem.code.toString())
                                                }
                                                Log.d("tamaiDpc", "jushodo skip initialization. kept: ${currentDpcCode.jushodo}")
                                            }
                                        }
                                    }
                                    DpcDropdownSelection(
                                        title = options.first().label,
                                        options = labelIdList as List<String>,
                                        initialSelection = currentLabel,
                                        onOptionSelected = { selectedName ->
                                            coroutineScope.launch {
                                                val code = options.find {
                                                    it.labelResId == selectedName
                                                }?.code
                                                if (code != null) {
                                                    val finalCode =
                                                        code.toString()
                                                    currentDpcCode =
                                                        currentDpcCode.copy(jushodo = finalCode) // shujutu -> shochi1 に
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
    title: String,
    initialSelection: String? = null
) {
    var expanded by remember { mutableStateOf(false) }
    // optionsリストが変更されたら、選択中の項目もリセットする
    var selectedOption by remember(options) {
        mutableStateOf(
            // 1. 指定された初期値があればそれを使う
            // 2. なければリストの最初を使う
            // 3. リストも空ならデフォルト文字
            initialSelection ?: options.firstOrNull() ?: "選択してください"
        )
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

// 1. 表示用のラベルを生成する関数（Entityを渡すと文字列を返す）
fun getByotaiDisplayLabel(option: ByotaiOptionEntity): String {
    return buildString {
        // 年齢情報があればカッコ書きで追加する
        if (option.nenreiIjo.isNotEmpty() || option.nenreiMiman.isNotEmpty()) {
            if (option.nenreiIjo != "000") {
                if (option.nenreiIjo.isNotEmpty()) append(
                    "${
                        option.nenreiIjo.toInt().toString()
                    }歳以上"
                )
            }
            if (option.nenreiIjo.isNotEmpty() && option.nenreiMiman.isNotEmpty()) append(
                " "
            )
            if (option.nenreiMiman != "999") {
                if (option.nenreiMiman.isNotEmpty()) append(
                    "${option.nenreiMiman.toInt()}歳未満"
                )
            }
        }
        append(option.byotaiKubunMeisho)
    }
}


@Preview(showBackground = true)
@Composable
fun ScoreBottomAppBarPreview() {
    MaterialTheme {
        // プレビュー用のダミーデータ
        val primaryColor = MaterialTheme.colorScheme.primary
        val secondaryColor = MaterialTheme.colorScheme.secondary

        // モックデータ: 本来は remember や State で管理されているもの
        val currentDpcCodeJoined = "010010xxxxxxxx"
        val savedTotalAmount = 45000
        val bunruiNameValue = "肺炎（等）"
        val currentTotalAmount = 52300

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // ここが実際のコンポーネント呼び出しに相当します
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = ParagraphStyle(lineHeight = 1.2.em)) {
                            // 1. 履歴セクション (savedTotalAmount != 0)
                            withStyle(style = SpanStyle(color = secondaryColor, fontSize = 16.sp)) {
                                append("DPC病名: ")
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                    append("前回選択の病名")
                                }
                                append(" DPCコード: 010203xxxx ")
                                appendInlineContent("COPY_ICON_ID", "[copy]")
                                append("\n")
                                append(" 包括金額合計: ${"%,d".format(savedTotalAmount)}円\n")
                            }

                            // 2. 現在のDPC病名
                            withStyle(style = SpanStyle(color = primaryColor, fontSize = 16.sp)) {
                                append("DPC病名: ")
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                    append("$bunruiNameValue\n")
                                }
                            }

                            // 3. DPCコードと入院期間
                            withStyle(style = SpanStyle(color = primaryColor, fontSize = 16.sp)) {
                                append(" DPCコード: $currentDpcCodeJoined ")
                            }
                            appendInlineContent("COPY_ICON_ID", "[copy]")
                            append("\n")

                            withStyle(style = SpanStyle(color = primaryColor, fontSize = 16.sp)) {
                                append("入院期間I: 5 入院期間II: 12 入院期間III: 25")
                            }
                        }
                        withStyle(style = ParagraphStyle(lineHeight = 1.2.em)) {
                            // 4. 合計金額 (大きく表示)
                            withStyle(style = SpanStyle(color = primaryColor, fontSize = 26.sp)) {
                                append("\n包括金額合計: ${"%,d".format(currentTotalAmount)}円 ")
                            }
                            appendInlineContent("SAVE_ICON_ID", "[save]")

                            withStyle(style = SpanStyle(fontSize = 16.sp, color = primaryColor)) {
                                append("（25日まで）")
                            }
                        }
                    },
                    inlineContent = mapOf(
                        "COPY_ICON_ID" to InlineTextContent(
                            Placeholder(18.sp, 18.sp, PlaceholderVerticalAlign.Center)
                        ) {
                            Icon(
                                Icons.Default.ContentCopy,
                                contentDescription = null,
                                tint = primaryColor
                            )
                        },
                        "SAVE_ICON_ID" to InlineTextContent(
                            Placeholder(16.sp, 16.sp, PlaceholderVerticalAlign.Center)
                        ) {
                            Icon(
                                Icons.Default.AddCircle,
                                contentDescription = null,
                                tint = secondaryColor
                            )
                        }
                    )
                )
            }
        }
    }
}
