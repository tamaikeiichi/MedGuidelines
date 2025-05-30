package com.keiichi.medguidelines.ui.screen

import android.content.Context
import android.icu.text.Transliterator
import android.os.Build
import androidx.annotation.RequiresApi
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
//import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.keiichi.medguidelines.data.ActionType
import com.keiichi.medguidelines.data.ListItemData
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
import android.content.res.Configuration


@RequiresApi(Build.VERSION_CODES.Q)
fun normalizeTextForSearch(text: String): String {
    if (text.isBlank()) return ""

    // 1. Convert to lowercase (consider locale for specific language rules if needed,
    //    but general lowercase is often fine for search)
    var normalized = text.lowercase(Locale.getDefault())

    // 2. Normalize Japanese characters (Full-width to Half-width, Katakana to Hiragana)
    //    These are common normalizations for Japanese search.
    //    The order can sometimes matter.
    try {
        // Full-width to Half-width (alphanumerics and common symbols)
        val fullToHalfTransliterator = Transliterator.getInstance("Fullwidth-Halfwidth")
        normalized = fullToHalfTransliterator.transliterate(normalized)

        // Katakana to Hiragana (makes matching easier if data/query uses mixed forms)
        val katakanaToHiraganaTransliterator = Transliterator.getInstance("Katakana-Hiragana")
        normalized = katakanaToHiraganaTransliterator.transliterate(normalized)
    } catch (e: Exception) {
        // ICU might not be available on all Android versions/devices
        // or a specific transliterator might be missing.
        // Log the error or handle it gracefully.
        // For simplicity, we'll just print if there's an issue in this example.
        println("ICU Transliterator error: ${e.message}")
        // Fallback: return the lowercased string if ICU fails
    }

    // 4. Optional: Remove whitespace or specific punctuation if needed
    //    normalized = normalized.replace("\\s+".toRegex(), "") // Example: remove all whitespace

    return normalized
}

fun getStringForLocale(context: Context, resId: Int, targetLocale: Locale): String {
    val currentLocale = context.resources.configuration.locale
    if (currentLocale == targetLocale) {
        // Optimization: If target is current, just use normal getString
        try {
            return context.getString(resId)
        } catch (e: Exception) {
            // Handle cases where the string might be missing for the current locale
            // (should ideally fall back to default, but explicit error handling is safer)
            println("Error getting string for current locale $targetLocale: ${e.message}")
            return "" // Or some default
        }
    }
    // Create a new configuration context for the target locale
    val config = Configuration(context.resources.configuration)
    config.setLocale(targetLocale)
    try {
        return context.createConfigurationContext(config).getString(resId)
    } catch (e: Exception) {
        // Handle cases where the string might be missing for the target locale
        println("Error getting string for target locale $targetLocale: ${e.message}")
        return "" // Or some default value if the string is not found
    }
}

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
    var hasBeenVisited by rememberSaveable { mutableStateOf(false) }
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val lazyListState = remember(calculation = { LazyListState() })
    val alpha: Float by animateFloatAsState(
        targetValue = if (animateFirstItem) 0.5f else 1f,
        animationSpec = tween(durationMillis = 200), label = ""
    )

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

    val expectedItemCount = itemsList.size

//    val displayedItems = remember(searchQuery, originalItems) {
//        if (searchQuery.isBlank()) {
//            originalItems
//        } else {
////            val normalizedSearchQuery = normalizeTextForSearch(searchQuery)
////            val lowerCaseSearchQuery = searchQuery.lowercase()
////            if (normalizedSearchQuery.isBlank()) { // Handle if normalization results in blank
////                originalItems
////            } else {
////                val nameMatchingItems = originalItems.filter { itemData ->
////                    context.getString(itemData.nameResId).lowercase().contains(lowerCaseSearchQuery)
////                }
////                // Create a set of nameResIds from nameMatchingItems for efficient lookup
////                val nameMatchIds = nameMatchingItems.map { it.nameResId }.toSet()
////                val keywordOnlyMatchingItems = originalItems.filter { itemData ->
////                    // Check if it's NOT a name match
////                    if (itemData.nameResId in nameMatchIds) {
////                        false // Already included
////                    } else {
////                        // Check if any keyword matches
////                        itemData.keywords.any { keywordResId ->
////                            context.getString(keywordResId).lowercase()
////                                .contains(lowerCaseSearchQuery)
////                        }
////                    }
////                }
//            // Normalize the search query once
//            val normalizedSearchQuery = normalizeTextForSearch(searchQuery)
//
//            if (normalizedSearchQuery.isBlank()) { // Handle if normalization results in blank
//                originalItems
//            } else {
//                val nameMatchingItems = originalItems.filter { itemData ->
//                    val itemName = context.getString(itemData.nameResId)
//                    normalizeTextForSearch(itemName).contains(normalizedSearchQuery)
//                }
//
//                val nameMatchIds = nameMatchingItems.map { it.nameResId }.toSet()
//
//                val keywordOnlyMatchingItems = originalItems.filter { itemData ->
//                    if (itemData.nameResId in nameMatchIds) {
//                        false
//                    } else {
//                        itemData.keywords.any { keywordResId ->
//                            val keyword = context.getString(keywordResId)
//                            normalizeTextForSearch(keyword).contains(normalizedSearchQuery)
//                        }
//                    }
//                }
//                nameMatchingItems + keywordOnlyMatchingItems
//            }
//        }
//    }

    // Make sure you have the normalizeTextForSearch function from the previous answers

    val displayedItems = remember(searchQuery, originalItems, context) {
        if (searchQuery.isBlank()) {
            originalItems
        } else {
            val normalizedSearchQuery = normalizeTextForSearch(searchQuery)

            if (normalizedSearchQuery.isBlank()) {
                originalItems
            } else {
                val englishLocale = Locale.ENGLISH
                // For Japanese, you might use Locale.JAPANESE (general)
                // or new Locale("ja") for more specificity if needed.
                // Locale.JAPAN is for the country, Locale.JAPANESE is for the language.
                val japaneseLocale = Locale.JAPANESE

                val nameMatchingItems = originalItems.filter { itemData ->
                    val itemNameEn = getStringForLocale(context, itemData.nameResId, englishLocale)
                    val itemNameJa = getStringForLocale(context, itemData.nameResId, japaneseLocale)

                    val normalizedItemNameEn = normalizeTextForSearch(itemNameEn)
                    val normalizedItemNameJa = normalizeTextForSearch(itemNameJa)

                    (normalizedItemNameEn.isNotBlank() && normalizedItemNameEn.contains(normalizedSearchQuery)) ||
                            (normalizedItemNameJa.isNotBlank() && normalizedItemNameJa.contains(normalizedSearchQuery))
                }

                val nameMatchIds = nameMatchingItems.map { it.nameResId }.toSet()

                val keywordOnlyMatchingItems = originalItems.filter { itemData ->
                    if (itemData.nameResId in nameMatchIds) {
                        false // Already included as a name match
                    } else {
                        itemData.keywords.any { keywordResId ->
                            val keywordEn = getStringForLocale(context, keywordResId, englishLocale)
                            val keywordJa = getStringForLocale(context, keywordResId, japaneseLocale)

                            val normalizedKeywordEn = normalizeTextForSearch(keywordEn)
                            val normalizedKeywordJa = normalizeTextForSearch(keywordJa)

                            (normalizedKeywordEn.isNotBlank() && normalizedKeywordEn.contains(normalizedSearchQuery)) ||
                                    (normalizedKeywordJa.isNotBlank() && normalizedKeywordJa.contains(normalizedSearchQuery))
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
//        MedGuidelinesSearchBar(
//            searchQuery = searchQuery,
//            onSearchQueryChange = { newQuery ->
//                searchQuery = newQuery
//            }
//        )
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
                            ActionType.NAVIGATE_TO_PANCREASE_TNM -> navigateToPancreaticTNM()
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
