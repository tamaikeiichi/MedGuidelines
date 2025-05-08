package com.keiichi.medguidelines.ui.screen

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.keiichi.medguidelines.R
import com.keiichi.medguidelines.data.ActionType
import com.keiichi.medguidelines.data.ListItemData
import com.keiichi.medguidelines.data.loadListItemData
import com.keiichi.medguidelines.data.saveListItemData
import com.keiichi.medguidelines.ui.component.IndexScreenItemCard
import com.keiichi.medguidelines.ui.component.SearchBar
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

val itemsList = listOf(
    ListItemData(R.string.childPughTitle, ActionType.NAVIGATE_TO_CHILD_PUGH),
    ListItemData(R.string.aDropTitle, ActionType.NAVIGATE_TO_ADROP),
    ListItemData(R.string.colorectalTNMTitle, ActionType.NAVIGATE_TO_COLORECTAL_TNM),
    ListItemData(
        R.string.acuteTonsillitisAlgorithmTitle,
        ActionType.NAVIGATE_TO_ACUTE_TONSILLITIS_ALGORITHM
    ),
    ListItemData(R.string.bloodGasAnalysisTitle, ActionType.NAVIGATE_TO_BLOOD_GAS_ANALYSIS),
    ListItemData(R.string.acutePancreatitisTitle, ActionType.NAVIGATE_TO_ACUTE_PANCREATITIS),
    ListItemData(R.string.netakiridoTitle, ActionType.NAVIGATE_TO_NETAKIRIDO),
    ListItemData(R.string.pancreaticTNMTitle, ActionType.NAVIGATE_TO_PANCREATITIS_TNM),
    ListItemData(R.string.esophagealTNMTitle, ActionType.NAVIGATE_TO_ESOPAGEAL_TNM),
    ListItemData(R.string.mALBITitle, ActionType.NAVIGATE_TO_MALBI),
    ListItemData(
        R.string.liverFibrosisScoreSystemTitle,
        ActionType.NAVIGATE_TO_LIVERFIBROSISSCORESYSTEM
    ),
    ListItemData(R.string.homairTitle, ActionType.NAVIGATE_TO_HOMAIR),
    ListItemData(R.string.lungTNMTitle, ActionType.NAVIGATE_TO_LUNG_TNM),
    ListItemData(R.string.hccTNMTitle, ActionType.NAVIGATE_TO_HCC_TNM),
    ListItemData(R.string.intrahepaticCholangiocarcinomaTNMTitle, ActionType.NAVIGATE_TO_INTRAHEPATICCHOLANGIOCARCINOMA_TNM),
)

//fun setOriginalItems(mutableState: SnapshotStateList<ListItemData>, newList: MutableList<ListItemData>) {
//    mutableState.clear()
//    mutableState.addAll(newList)
//}

@Composable
fun IndexScreen(
    navigateToChildPugh: () -> Unit,
    navigateToAdrop: () -> Unit,
    navigateToColorectalTNM: () -> Unit,
    navigateToAcuteTonsillitisAlgorithm: () -> Unit,
    navigateToBloodGasAnalysis: () -> Unit,
    navigateToAcutePancreatitis: () -> Unit,
    navigateToNetakirido: () -> Unit,
    navigateToPancreaticTNM: () -> Unit,
    navigateToEsophagealTNM: () -> Unit,
    navigateToMALBI: () -> Unit,
    navigateToLiverFibrosisScoreSystem: () -> Unit,
    navigateToHomaIR: () -> Unit,
    navigateToLungTNM: () -> Unit,
    navigateToHccTNM: () -> Unit,
    navigateToIntrahepaticCholangiocarcinomaTNM: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var searchQuery by remember { mutableStateOf("") }

    var animateFirstItem by remember { mutableStateOf(false) }
    var animationCount by remember { mutableStateOf(0) }
    var hasBeenVisited by rememberSaveable { mutableStateOf(false) }
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val lazyListState = rememberLazyListState()
    val alpha: Float by animateFloatAsState(
        targetValue = if (animateFirstItem) 0.5f else 1f,
        animationSpec = tween(durationMillis = 200), label = ""
    )

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    if (hasBeenVisited) {
                        animateFirstItem = true
                    }
                    hasBeenVisited = true
                }
                else -> {
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    LaunchedEffect(animateFirstItem) {
        if (animateFirstItem) {
            while (animationCount < 3) {
                kotlinx.coroutines.delay(200)
                animationCount++
            }
            animateFirstItem = false
            animationCount = 0
        }
    }

    val expectedItem = itemsList
    val expectedItemCount = itemsList.size

    val originalItems = rememberSaveable(
        saver = listSaver(
            save = { it.map { item -> Json.encodeToString(item) } },
            restore = {restored ->
                restored.map { item -> Json.decodeFromString<ListItemData>(item) }
                    .toMutableStateList()
            }
        )
    ) {
        mutableStateListOf<ListItemData>()
    }

    val filteredItems = remember(searchQuery, originalItems) {
        if (searchQuery.isBlank()) {
            originalItems
        } else {
            originalItems.toList().filter { itemData ->
                val name = context.getString(itemData.nameResId)
                name.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    LaunchedEffect(Unit) {
        loadListItemData(context, expectedItemCount).collect { loadedItems ->
                originalItems.clear()
                originalItems.addAll(loadedItems)
        }
    }

    Column {
        SearchBar(
            searchQuery = searchQuery,
            onSearchQueryChange = { newQuery ->
                searchQuery = newQuery
            }
        )
        LazyColumn(
            modifier = Modifier
                .padding(2.dp)
                .fillMaxWidth(),
            contentPadding = PaddingValues(10.dp),
        ) {
            items(
                filteredItems//items
            ) { itemData ->

                val isFirstItem = lazyListState.firstVisibleItemIndex == filteredItems.indexOf(itemData)
                val currentAlpha = if (isFirstItem) alpha else 1f

                IndexScreenItemCard(
                    currentAlpha = currentAlpha,
                    name = itemData.nameResId,
                    onClick = {
                        val updatedItems = originalItems.toMutableList()//items.toMutableList()
                        updatedItems.remove(itemData)
                        updatedItems.add(0, itemData)
                        scope.launch {
                            saveListItemData(context, updatedItems)
                        }
                        when (itemData.actionType) {
                            ActionType.NAVIGATE_TO_CHILD_PUGH -> navigateToChildPugh()
                            ActionType.NAVIGATE_TO_ADROP -> navigateToAdrop()
                            ActionType.NAVIGATE_TO_COLORECTAL_TNM -> navigateToColorectalTNM()
                            ActionType.NAVIGATE_TO_ACUTE_TONSILLITIS_ALGORITHM -> navigateToAcuteTonsillitisAlgorithm()
                            ActionType.NAVIGATE_TO_BLOOD_GAS_ANALYSIS -> navigateToBloodGasAnalysis()
                            ActionType.NAVIGATE_TO_ACUTE_PANCREATITIS -> navigateToAcutePancreatitis()
                            ActionType.NAVIGATE_TO_NETAKIRIDO -> navigateToNetakirido()
                            ActionType.NAVIGATE_TO_PANCREATITIS_TNM -> navigateToPancreaticTNM()
                            ActionType.NAVIGATE_TO_ESOPAGEAL_TNM -> navigateToEsophagealTNM()
                            ActionType.NAVIGATE_TO_MALBI -> navigateToMALBI()
                            ActionType.NAVIGATE_TO_LIVERFIBROSISSCORESYSTEM -> navigateToLiverFibrosisScoreSystem()
                            ActionType.NAVIGATE_TO_HOMAIR -> navigateToHomaIR()
                            ActionType.NAVIGATE_TO_LUNG_TNM -> navigateToLungTNM()
                            ActionType.NAVIGATE_TO_HCC_TNM -> navigateToHccTNM()
                            ActionType.NAVIGATE_TO_INTRAHEPATICCHOLANGIOCARCINOMA_TNM -> navigateToIntrahepaticCholangiocarcinomaTNM()
                        }
                    }
                )
            }
        }
    }
//    Button(
//        onClick = {
//        } as () -> Unit
//    ) {
//        Text("Go to Child Screen")
//    }
}

@Preview
@Composable
fun IndexScreenPreview() {
    IndexScreen(
        navigateToChildPugh = {},
        navigateToAdrop = {},
        navigateToColorectalTNM = {},
        navigateToAcuteTonsillitisAlgorithm = {},
        navigateToBloodGasAnalysis = {},
        navigateToAcutePancreatitis = {},
        navigateToNetakirido = {},
        navigateToPancreaticTNM = {},
        navigateToEsophagealTNM = {},
        navigateToMALBI = {},
        navigateToLiverFibrosisScoreSystem = {},
        navigateToHomaIR = {},
        navigateToLungTNM = {},
        navigateToHccTNM = {},
        navigateToIntrahepaticCholangiocarcinomaTNM = {}
    )
}
