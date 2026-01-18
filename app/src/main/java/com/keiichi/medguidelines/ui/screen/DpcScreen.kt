package com.keiichi.medguidelines.ui.screen

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.keiichi.medguidelines.R
import com.keiichi.medguidelines.ui.component.Dimensions
import com.keiichi.medguidelines.ui.component.InputValue
import com.keiichi.medguidelines.ui.component.MedGuidelinesCard
import com.keiichi.medguidelines.ui.component.MedGuidelinesScaffold
import com.keiichi.medguidelines.ui.component.MyCustomSearchBar
import com.keiichi.medguidelines.ui.component.ScoreBottomAppBarVariable
import com.keiichi.medguidelines.ui.component.TextAndUrl
import com.keiichi.medguidelines.ui.component.TitleTopAppBar
import com.keiichi.medguidelines.ui.component.buttonAndScoreWithScoreDisplayedSelectableLabelString
import com.keiichi.medguidelines.ui.viewModel.DpcScreenViewModel
import kotlinx.coroutines.launch
import org.jetbrains.kotlinx.dataframe.DataFrame
import kotlin.text.toDoubleOrNull

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

    var displayedItemsBunrui by remember { mutableStateOf<DataFrame<*>?>(null) }

    val shindangunBunruiTensuhyo by dpcScreenViewModel.shindangunBunruiTensuhyoOptions.collectAsState()
    Log.d("tamaiDpcUI", "Current List Size in UI: ${shindangunBunruiTensuhyo.size}")

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
    MedGuidelinesScaffold(
        topBar = {
            TitleTopAppBar(
                title = R.string.dpcTitle,
                navController = navController,
                references = listOf(TextAndUrl(R.string.space, R.string.space))
            )
        },
        bottomBar = {
            ScoreBottomAppBarVariable(
                displayText = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontSize = 16.sp)) {
                        append("DPCコード: ")
                        append(dpcCodeFirstJoined)
                    }
                    if (shindangunBunruiTensuhyo.isNotEmpty()) {
                        val data = shindangunBunruiTensuhyo.first()
                        cost.intValue = calculateNyuinCost(
                            days = days.doubleValue.toInt(),
                            nyuinbiI = data.nyuinbiI.toInt(),
                            nyuinbiII = data.nyuinbiII.toInt(),
                            nyuinbiIII = data.nyuinbiIII.toInt(),
                            nyuinKikanI = data.nyuinKikanI.toInt(),
                            nyuinKikanII = data.nyuinKikanII.toInt(),
                            nyuinKikanIII = data.nyuinKikanIII.toInt()
                        )
                        append("\n包括金額合計:${(cost.intValue * 10 * coeff.doubleValue).toInt()}円")
                    } else {
                        if (dpcCodeFirst == DpcCode())
                            append("\n(病名を選択してください)")
                        else {
                            append("\n出来高算定")
                        }
                    }
                }
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
                        MyCustomSearchBar(
                            searchQuery = query,
                            onSearchQueryChange = {
                                query = it
                                dpcScreenViewModel.onQueryChanged(it)

                                // クエリが空（クリア）になった場合
                                if (it.isBlank()) {
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
                            isLoading = false,
                            placeholderText = R.string.searchIcd
                        )
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            item {
                                if (bunruiIcdSelectedIcdItem != null) {
                                    MedGuidelinesCard() {
                                        Column() {
                                            Text(
                                                text = "$bunruiIcdSelectedIcdItem",
                                                modifier = Modifier.padding(Dimensions.textPadding)
                                            )
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
                                    }
                                }
                            }
                            if (searchResultsVisible) {
                                val icdItemsList = displayedItemsIcd.toList()
                                // --- ICDコードの結果 ---
                                if (icdItemsList.isNotEmpty()) {
                                    items(
                                        items = icdItemsList,
                                        key = {
                                            it.icdCode ?: it.hashCode().toString()
                                        }) { icdItem ->
                                        MedGuidelinesCard {
                                            Text(
                                                text = icdItem.icdName
                                                    ?: "No Name", // itemTextの代わりに直接参照
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable {
                                                        // 1. UIの状態を更新
                                                        bunruiIcdSelectedIcdItem = icdItem.icdName
                                                        // 2. Stateの更新は .copy() の結果を再代入する
                                                        dpcCodeFirst = dpcCodeFirst.copy(
                                                            icd = icdItem.icdCode,
                                                            mdc = icdItem.mdcCode,
                                                            bunrui = icdItem.bunruiCode
                                                        )

                                                        // 3. ViewModelのメソッドを呼び出してイベントを通知する
                                                        dpcScreenViewModel.onIcdItemSelected(icdItem)

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

                            // --- 病態選択UI ---
                            // ViewModelのshowByotaiSelectionの値に基づいて、UIの表示を切り替える
                            item {
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
                                                                            dpcCodeFirst.copy(byotai = finalByotaiCode)
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
                            }
                            // --- 年齢選択UI ---
                            item {
                                if (showNenreiSelection) {
                                    Log.d("tamaiDpc", "after if (showNenreiSelection)")

                                    // 1. ViewModelから年齢条件のリストを購読するval nenreiOptions by dpcScreenViewModel.nenreiOptions.collectAsState()
                                    val nenreiOptions by dpcScreenViewModel.nenreiOptions.collectAsState()

                                    // 2. 有効な選択肢が1つ以上ある場合のみUIを表示する
                                    if (nenreiOptions.isNotEmpty()) {

                                        var selectedlabelResId: String? by remember(nenreiOptions) {
                                            mutableStateOf(nenreiOptions.first().labelResId.toString())
                                        }

                                        MedGuidelinesCard(modifier = Modifier.padding(vertical = 8.dp)) {
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
                            }
                            //手術
                            item {
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
                                                        code.toDoubleOrNull()?.toInt()?.toString()
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
                                                    dpcScreenViewModel.getShujutsu1Code(selectedName)
                                                if (code != null) {
                                                    val finalCode =
                                                        code.toDoubleOrNull()?.toInt()?.toString()
                                                            ?: code
                                                    // ★★★ dpcCodeFirstの更新先が shochi1 になっているか確認 ★★★
                                                    dpcCodeFirst =
                                                        dpcCodeFirst.copy(shujutu = finalCode) // shujutu -> shochi1 に
                                                }
                                            }
                                        }
                                    )
                                }
                            }
                            //処置１
                            item {
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
                                                        code.toDoubleOrNull()?.toInt()?.toString()
                                                            ?: code
                                                    dpcCodeFirst =
                                                        dpcCodeFirst.copy(shochi1 = finalCode) // shujutu -> shochi1 に
                                                }
                                            }
                                        }
                                    )
                                }
                            }
                            item {
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
                                                        code.toDoubleOrNull()?.toInt()?.toString()
                                                            ?: code
                                                    // ★★★ dpcCodeFirstの更新先が shochi1 になっているか確認 ★★★
                                                    dpcCodeFirst =
                                                        dpcCodeFirst.copy(shochi2 = finalCode) // shujutu -> shochi1 に
                                                }
                                            }
                                        }
                                    )
                                }
                            }
                            //副傷病名
                            item {
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
                                                        code.toDoubleOrNull()?.toInt()?.toString()
                                                            ?: code
                                                    // ★★★ dpcCodeFirstの更新先が shochi1 になっているか確認 ★★★
                                                    dpcCodeFirst =
                                                        dpcCodeFirst.copy(fukushobyo = finalCode) // shujutu -> shochi1 に
                                                }
                                            }
                                        }
                                    )
                                }
                            }
                            //重症度JCS
                            item {
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
                            }
                            //重症度手術
                            item {
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
                                            Log.d("tamaiDpc", "title: $options.first().labelResId")
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
                            }
                            //重症度脳卒中
                            item {
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
                            }
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
