package com.keiichi.medguidelines.ui.screen

import android.content.Context
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
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
import kotlin.collections.toMutableList

fun getStringFromResourceId(context: Context, resourceId: Int): String {
    return context.getString(resourceId)
}

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
    ListItemData(R.string.homairhomabetaTitle, ActionType.NAVIGATE_TO_HOMAIR),
    ListItemData(R.string.lungTNMTitle, ActionType.NAVIGATE_TO_LUNG_TNM),
    ListItemData(R.string.hccTNMTitle, ActionType.NAVIGATE_TO_HCC_TNM),
    ListItemData(R.string.intrahepaticCholangiocarcinomaTNMTitle, ActionType.NAVIGATE_TO_INTRAHEPATICCHOLANGIOCARCINOMA_TNM),
    ListItemData(R.string.chads2AndHelte2s2Title, ActionType.NAVIGATE_TO_CHADS2),
    ListItemData(R.string.glasgowComaScaleTitle, ActionType.NAVIGATE_TO_GLASGOW_COMA_SCALE),
    ListItemData(R.string.sodiumDifferentialDiagnosisTitle, ActionType.NAVIGATE_TO_SODIUM_DIFFERENTIAL_DIAGNOSIS),

)

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
    navigateToIntrahepaticCholangiocarcinomaTNM: () -> Unit,
    navigateToCHADS2: () -> Unit,
    navigateToGlasgowComaScale: () -> Unit,
    navigateToSodiumDifferentialDiagnosis: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var searchQuery by remember { mutableStateOf("") }

    var animateFirstItem by remember { mutableStateOf(false) }
    var animationCount by remember { mutableStateOf(0) }
    var hasBeenVisited by rememberSaveable { mutableStateOf(false) }
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    //val lazyListState = LazyListState()//rememberLazyListState()
    val lazyListState = remember(calculation = { LazyListState() })
    val alpha: Float by animateFloatAsState(
        targetValue = if (animateFirstItem) 0.5f else 1f,
        animationSpec = tween(durationMillis = 200), label = ""
    )

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

    // Track the item that was clicked for later processing
    //var clickedItem by remember { mutableStateOf<ListItemData?>(null) }
    var clickedItemForNavigation by remember { mutableStateOf<ListItemData?>(null) } // Renamed for clarity

    // Function to reorder items and save
    fun updateAndSaveItems(updatedList: List<ListItemData>) {
        originalItems.clear()
        originalItems.addAll(updatedList)
        scope.launch {
            saveListItemData(context, originalItems)//.toList())// as MutableList<ListItemData>)
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    clickedItemForNavigation?.let { item ->
                        val currentList = originalItems.toMutableList()
                        if (currentList.remove(item)) {
                            // Only move to top if not a favorite, favorites have their own ordering
                            if (!item.isFavorite) {
                                val firstFavoriteIndex = currentList.indexOfFirst { it.isFavorite }
                                if (firstFavoriteIndex != -1) {
                                    currentList.add(firstFavoriteIndex, item) // Add below favorites
                                } else {
                                    currentList.add(0, item) // Add to top if no favorites
                                }
                            } else {
                                val favorites = currentList.filter { it.isFavorite }.toMutableList()
                                val nonFavorites = currentList.filterNot { it.isFavorite }
                                if (favorites.remove(item)) { // Should always be true if it was clicked
                                    favorites.add(0, item)
                                }
                                currentList.clear()
                                currentList.addAll(favorites)
                                currentList.addAll(nonFavorites)
                            }
                            updateAndSaveItems(currentList)
                        }
                        clickedItemForNavigation = null
                    }

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
//    LaunchedEffect(animateFirstItem) {
//        if (animateFirstItem) {
//            while (animationCount < 3) {
//                kotlinx.coroutines.delay(200)
//                animationCount++
//            }
//            animateFirstItem = false
//            animationCount = 0
//        }
//    }

    val expectedItemCount = itemsList.size

    val displayedItems = remember(searchQuery, originalItems) {
        val filtered = if (searchQuery.isBlank()) {
            originalItems
        } else {
            originalItems.toList().filter { itemData ->
                val name = context.getString(itemData.nameResId)
                name.contains(searchQuery, ignoreCase = true)
            }
        }
        filtered
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
            state = lazyListState,
            modifier = Modifier
                .padding(2.dp)
                .fillMaxWidth(),
            contentPadding = PaddingValues(10.dp),
        ) {
            items(
                items = displayedItems, // Use the sorted and filtered list
                key = { item -> item.nameResId } // Provide a stable key
            ) { itemData ->
                val isFirstNonFavoriteVisible = lazyListState.firstVisibleItemIndex == displayedItems.indexOf(itemData) && !itemData.isFavorite
                val currentAlpha = if (isFirstNonFavoriteVisible && animateFirstItem) alpha else 1f
                IndexScreenItemCard(
                    currentAlpha = currentAlpha,
                    name = itemData.nameResId,
                    isFavorite = itemData.isFavorite,
                    onItemClick = {
                        clickedItemForNavigation = itemData // Set item for ON_RESUME handling
                                                var updatedItems = originalItems.toMutableList()//items.toMutableList()
                        updatedItems.remove(itemData)
                        updatedItems.add(0, itemData)
                        updatedItems = updatedItems.sortedWith(compareByDescending<ListItemData> { it.isFavorite }).toMutableList()
                        scope.launch {
                            saveListItemData(context, updatedItems)
                        }
                        // Navigation logic (remains the same)
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
                            ActionType.NAVIGATE_TO_CHADS2 -> navigateToCHADS2()
                            ActionType.NAVIGATE_TO_GLASGOW_COMA_SCALE -> navigateToGlasgowComaScale()
                            ActionType.NAVIGATE_TO_SODIUM_DIFFERENTIAL_DIAGNOSIS -> navigateToSodiumDifferentialDiagnosis()
                        }
                    },
                    onFavoriteClick = {
                        val currentList = originalItems.toMutableList()
                        val itemIndex =
                            currentList.indexOfFirst { it.nameResId == itemData.nameResId }
                        if (itemIndex != -1) {
                            val clickedItem = currentList[itemIndex]
                            val oldFavoriteState = clickedItem.isFavorite // Store old state

                            clickedItem.isFavorite = !clickedItem.isFavorite // Toggle favorite

                            currentList.removeAt(itemIndex) // Remove from current position

                            if (clickedItem.isFavorite) {
                                // Add to the top of the list (or top of favorites section)
                                currentList.add(0, clickedItem)
                            } else {
                                // Add back after all favorites, or maintain original relative order (simpler for now: add after favorites)
                                val firstNonFavoriteIndex =
                                    currentList.indexOfFirst { !it.isFavorite }
                                if (firstNonFavoriteIndex != -1) {
                                    currentList.add(firstNonFavoriteIndex, clickedItem)
                                } else {
                                    currentList.add(clickedItem) // Add to end if all were favorites
                                }
                            }
                            // Re-sort to ensure all favorites are grouped at the top
                            val sortedList = currentList.sortedWith(
                                compareByDescending<ListItemData> { it.isFavorite }
                                // You might want a secondary sort criteria here if needed
                            )
                            updateAndSaveItems(sortedList)
                            // Scroll to top IF an item was newly favorited and thus moved up
                            if (clickedItem.isFavorite && !oldFavoriteState) { // Check if it became a favorite
                                scope.launch {
                                    lazyListState.animateScrollToItem(index = 0) // Animate scroll to the top
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
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
        navigateToIntrahepaticCholangiocarcinomaTNM = {},
        navigateToCHADS2 = {},
        navigateToGlasgowComaScale = {},
        navigateToSodiumDifferentialDiagnosis = {},
    )
}
