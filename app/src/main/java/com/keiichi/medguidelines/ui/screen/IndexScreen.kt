package com.keiichi.medguidelines.ui.screen

import android.util.Log
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
import com.keiichi.medguidelines.data.IndexScreenActions
import com.keiichi.medguidelines.data.ListItemData
import com.keiichi.medguidelines.data.executeNavigation
import com.keiichi.medguidelines.data.itemsList
import com.keiichi.medguidelines.data.loadListItemData
import com.keiichi.medguidelines.data.saveListItemData
import com.keiichi.medguidelines.ui.component.IndexScreenItemCard
import com.keiichi.medguidelines.ui.component.MyCustomSearchBar
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.collections.toMutableList
import kotlin.text.contains
import java.util.Locale // For lowercase locale handling
import com.keiichi.medguidelines.ui.component.getStringOfSpecificLocale
import com.keiichi.medguidelines.ui.component.normalizeTextForSearch
import kotlinx.coroutines.delay

@Composable
fun IndexScreen(
    actions: IndexScreenActions
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var searchQuery by remember { mutableStateOf("") }

    var animateFirstItem by remember { mutableStateOf(false) }
    var hasBeenVisited by rememberSaveable { mutableStateOf(false) }
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val lazyListState = remember(calculation = { LazyListState() })
    val alpha: Float by animateFloatAsState(
        targetValue = if (animateFirstItem) 0.5f else 1f,
        animationSpec = tween(durationMillis = 200), label = ""
    )
    var itemLoadingForNavigation by remember { mutableStateOf<Int?>(null) }

    val originalItems = rememberSaveable(
        saver = listSaver(
            save = { it.map { item -> Json.encodeToString(item) } },
            restore = { restored ->
                restored.map { item -> Json.decodeFromString<ListItemData>(item) }
                    .toMutableStateList()
            }
        )
    ) {
        mutableStateListOf<ListItemData>()
    }

    var clickedItemForNavigation by remember { mutableStateOf<ListItemData?>(null) } // Renamed for clarity
    //var itemLoadingForNavigation by remember { mutableStateOf<Int?>(null) } // <--- New state for loading indicator

    fun updateAndSaveItems(updatedList: List<ListItemData>) {
        originalItems.clear()
        originalItems.addAll(updatedList)
        scope.launch {
            saveListItemData(context, originalItems)
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    clickedItemForNavigation?.let { item ->
                        val currentList = originalItems.toMutableList()
                        if (currentList.remove(item)) {
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
                    itemLoadingForNavigation = null
                }
                Lifecycle.Event.ON_PAUSE -> { // <--- Clear loader when screen is paused
                    itemLoadingForNavigation = null
                }
                else -> {
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            itemLoadingForNavigation = null
        }
    }

    val expectedItemCount = itemsList.size

    val displayedItems = remember(searchQuery, originalItems, context) {
        if (searchQuery.isBlank()) {
            originalItems
        } else {
            val normalizedSearchQuery = normalizeTextForSearch(searchQuery)

            if (normalizedSearchQuery.isBlank()) {
                originalItems
            } else {
                val englishLocale = Locale.ENGLISH
                val japaneseLocale = Locale.JAPANESE

                val nameMatchingItems = originalItems.filter { itemData ->
                    val itemNameEn =
                        getStringOfSpecificLocale(context, itemData.nameResId, englishLocale)
                    val itemNameJa =
                        getStringOfSpecificLocale(context, itemData.nameResId, japaneseLocale)

                    val normalizedItemNameEn = normalizeTextForSearch(itemNameEn)
                    val normalizedItemNameJa = normalizeTextForSearch(itemNameJa)

                    (normalizedItemNameEn.isNotBlank() && normalizedItemNameEn.contains(
                        normalizedSearchQuery
                    )) ||
                            (normalizedItemNameJa.isNotBlank() && normalizedItemNameJa.contains(
                                normalizedSearchQuery
                            ))
                }

                val nameMatchIds = nameMatchingItems.map { it.nameResId }.toSet()

                val keywordOnlyMatchingItems = originalItems.filter { itemData ->
                    if (itemData.nameResId in nameMatchIds) {
                        false // Already included as a name match
                    } else {
                        itemData.keywords.any { keywordResId ->
                            val keywordEn =
                                getStringOfSpecificLocale(context, keywordResId, englishLocale)
                            val keywordJa =
                                getStringOfSpecificLocale(context, keywordResId, japaneseLocale)

                            val normalizedKeywordEn = normalizeTextForSearch(keywordEn)
                            val normalizedKeywordJa = normalizeTextForSearch(keywordJa)

                            (normalizedKeywordEn.isNotBlank() && normalizedKeywordEn.contains(
                                normalizedSearchQuery
                            )) ||
                                    (normalizedKeywordJa.isNotBlank() && normalizedKeywordJa.contains(
                                        normalizedSearchQuery
                                    ))
                        }
                    }
                }
                // Combine and ensure no duplicates if an item could match by name in one lang and keyword in another
                // (though your current logic for keywordOnlyMatchingItems handles this part)
                nameMatchingItems + keywordOnlyMatchingItems
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
        MyCustomSearchBar(
            searchQuery = searchQuery,
            onSearchQueryChange = { newQuery ->
                searchQuery = newQuery
                // You might also trigger search filtering here as the user types
                // performSearchFilter(newQuery)
            },
            onSearch = { query ->
                // This is called when the user submits the search (e.g., presses Enter)
                // performActualSearch(query)
                println("Search submitted: $query")
            },
//            modifier = Modifier
//                .safeDrawingPadding()

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
                val isFirstNonFavoriteVisible =
                    lazyListState.firstVisibleItemIndex == displayedItems.indexOf(itemData) && !itemData.isFavorite
                val currentAlpha = if (isFirstNonFavoriteVisible && animateFirstItem) alpha else 1f
                //itemLoadingForNavigation == itemData.nameResId
                var isLoadingForCard: Boolean = false
                // Determine if this specific item is the one loading
                // isLoadingForThisCard is true if itemLoadingForNavigation matches the current item's ID
                val isLoadingForThisCard= remember{mutableStateOf(false)}

                LaunchedEffect(itemLoadingForNavigation) {
                    isLoadingForThisCard.value = itemLoadingForNavigation == itemData.nameResId
                }

//                var clickedItem by remember { mutableStateOf<ListItemData?>(null) }
//
//                LaunchedEffect(clickedItem) {
//                    clickedItem?.let { item -> // 'let' executes only if clickedItem is not null
//                        actions.executeNavigation(item.actionType)
//                    }
//                }
                //var isLoading by remember { mutableStateOf(false) }//: Boolean = false// = itemLoadingForNavigation == itemData.nameResId
                IndexScreenItemCard(
                    currentAlpha = currentAlpha,
                    name = itemData.nameResId,
                    isFavorite = itemData.isFavorite,
                    isLoading = isLoadingForThisCard,
                    onItemClick = {
                        // Prevent multiple items from trying to load simultaneously or double clicks on the same item
                        if (itemLoadingForNavigation != null && itemLoadingForNavigation != itemData.nameResId) {
                            Log.d("IndexScreen", "Another item is already loading. Click ignored.")
                            return@IndexScreenItemCard
                        }
                        if (itemLoadingForNavigation == itemData.nameResId) {
                            Log.d("IndexScreen", "This item is already loading (double click?). Click ignored.")
                            return@IndexScreenItemCard
                        }

                        // Set the loading state for the specific item
//                        if (itemData.nameResId == R.string.ikashiRinryokuMasterKensaku) {
//                            itemLoadingForNavigation = itemData.nameResId // <--- CORRECT STATE CHANGE
//                            //itemLoadingForNavigation = true
//                            //isLoadingForThisCard.value = true
//                            Log.d("IndexScreen", "Set itemLoadingForNavigation for ${itemData.nameResId}")
//                        }

                        clickedItemForNavigation = itemData
                        //clickedItem = itemData

                        // Perform list updates (these are synchronous on the main thread)
                        // Consider if these need to be done before navigation or can be in the scope.launch
                        var updatedItems = originalItems.toMutableList()
                        val originalIndex = updatedItems.indexOf(itemData)
                        if (originalIndex != -1) {
                            updatedItems.removeAt(originalIndex)
                        }
                        updatedItems.add(0, itemData) // Add to top temporarily
                        // Re-sort ensuring favorites are first, then the clicked item, then others
                        updatedItems = updatedItems.sortedWith(
                            compareByDescending<ListItemData> { it.isFavorite }
                                .thenByDescending { it.nameResId == itemData.nameResId } // Prioritize clicked item after favorites
                        ).toMutableList()

                        // Update and save items (this part is launched in a coroutine)
                        updateAndSaveItems(updatedItems) // Replaces direct scope.launch for saveListItemData

                        // Execute navigation
//                        if (itemData.nameResId != R.string.ikashiRinryokuMasterKensaku) {
//                            actions.executeNavigation(itemData.actionType)
//                        }
                        actions.executeNavigation(itemData.actionType)
                    },
                    onFavoriteClick = {
                        if (itemLoadingForNavigation != null) return@IndexScreenItemCard // Prevent interaction while an item loads

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
        actions = IndexScreenActions(
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
            navigateToLilleModel = {},
            navigateToEcog = {},
            navigateToInfusionCalculator = {},
            navigateToIkaShinryokoiMaster = {},
            navigateToIcd10 = {},
        )
    )
}
