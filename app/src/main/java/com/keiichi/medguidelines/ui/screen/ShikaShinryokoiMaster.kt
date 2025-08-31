package com.keiichi.medguidelines.ui.screen

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.keiichi.medguidelines.ui.component.MedGuidelinesScaffold
import com.keiichi.medguidelines.R
import com.keiichi.medguidelines.ui.component.MyCustomSearchBar
import java.io.InputStream
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.toMutableStateList
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.navigation.NavHostController
import com.keiichi.medguidelines.data.dataStore
import com.keiichi.medguidelines.ui.component.normalizeTextForSearch
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.nio.charset.Charset
import org.jetbrains.kotlinx.dataframe.*
import org.jetbrains.kotlinx.dataframe.api.*
import org.jetbrains.kotlinx.dataframe.io.*
import org.jetbrains.kotlinx.dataframe.io.ColType
import kotlin.collections.map
import kotlin.text.contains
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.keiichi.medguidelines.data.PairedTextItem
import com.keiichi.medguidelines.ui.component.ShinryokoiMasterScreenItemCard
import com.keiichi.medguidelines.ui.component.TextAndUrl
import com.keiichi.medguidelines.ui.component.TitleTopAppBar
import com.keiichi.medguidelines.ui.component.doAnyDecodeStringsMatchAnyPairedItemsStrings
import com.keiichi.medguidelines.ui.component.saveListData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

val LIST_PAIRED_DATA_KEY_SHIKA = stringPreferencesKey("list_paired_data_shika")

@Composable
fun ShikaShinryokoiMasterScreen(navController: NavHostController) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    val lazyListState = remember(calculation = { LazyListState() })
    val numberOfColumns = 50
    val scope = rememberCoroutineScope()
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    var clickedItemForNavigation by remember { mutableStateOf<PairedTextItem?>(null) } // Renamed for clarity
    // State to hold the processed list, initialized to empty or a loading state
    var pairedDataList by remember { mutableStateOf<List<PairedTextItem>>(emptyList()) }
    var isLoadingPairedData by remember { mutableStateOf(true) } // Optional loading state

    val originalItems = rememberSaveable(
        saver = listSaver(
            save = { it.map { item -> Json.encodeToString(item) } },
            restore = { restored ->
                restored.map { item -> Json.decodeFromString<PairedTextItem>(item) }
                    .toMutableStateList()
            }
        )
    ) {
        mutableStateListOf<PairedTextItem>()
    }

    // State for the query that actually triggers filtering
    var effectiveSearchQuery by remember { mutableStateOf("") }
    // State for the items to be displayed by LazyColumn
    // State for loading indicator
    var isFiltering by remember { mutableStateOf(false) }

    // Debounce search query
    LaunchedEffect(searchQuery) {
        delay(350L) // Debounce time
        effectiveSearchQuery = searchQuery
    }

    var displayedItems by remember { mutableStateOf<List<PairedTextItem>>(emptyList()) }

    LaunchedEffect(effectiveSearchQuery, originalItems.toList()) {
        if (effectiveSearchQuery.isBlank()) {
            displayedItems = originalItems.toList()
            isFiltering = false
            return@LaunchedEffect
        }

        isFiltering = true
        val currentRawQuery = effectiveSearchQuery
        val currentList = originalItems.toList()//displayedItems.toList()//originalItems.toList()

        val filtered = withContext(Dispatchers.Default) {
            val spaceRegex = Regex("[ 　]+")
            // Split the raw query into individual terms by space.
            // Filter out empty strings that might result from multiple spaces.
            val searchTerms = currentRawQuery
                .split(spaceRegex) // Split by the regex
                .map { normalizeTextForSearch(it) }
                .filter { it.isNotBlank() }

            if (searchTerms.isEmpty()) {
                // If all terms were blank (e.g., query was just spaces), show all items
                currentList
            } else {
                currentList.filter { itemData ->
                    // Normalize the item's text fields once
                    //val kanjiText = normalizeTextForSearch(itemData.kanjiMeisho.toString())
                    //val kanaText = normalizeTextForSearch(itemData.kanaMeisho.toString())
                    val kanjiText = itemData.kanjiText
                    val kanaText = itemData.kanaText
                    // Check if ALL search terms are found in the item's text fields.
                    searchTerms.all { term -> // <<< Key change: using .all {}
                        // An item matches if the term is in EITHER kanjiText OR kanaText
                        kanjiText.contains(term) || kanaText.contains(term)
                    }
                }
            }
        }
        displayedItems = filtered
        isFiltering = false
    }

    fun updateAndSaveItems(updatedList: List<PairedTextItem>) {
        originalItems.clear()
        originalItems.addAll(updatedList)
        scope.launch {
            saveListData(
                context,
                originalItems,
                LIST_PAIRED_DATA_KEY_SHIKA//.toMutableList()
            )
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

    val master: AnyFrame? = try {
        //isLoadingPairedData = true
        val inputStream: InputStream = context.resources.openRawResource(R.raw.h_20250808)
        val headerNames = (1..numberOfColumns).map { it.toString() }
        val columnTypes: Map<String, ColType> = headerNames.associateWith { ColType.String }

        inputStream.use { stream ->
            DataFrame.readCSV(
                stream = stream,
                header = headerNames,
                charset = Charset.forName("Shift-JIS"),
                colTypes = columnTypes, // Specify that all columns should be read as String
                parserOptions = ParserOptions() // Keep default or adjust as needed
            )
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    } finally {
        isLoadingPairedData = false
    }

    // At the top level of your Composable, where 'master' is available

// Effect to process 'master' when it changes (or on initial composition)
    LaunchedEffect(master) {
        if (master == null) {
            pairedDataList = emptyList()
            isLoadingPairedData = false
            return@LaunchedEffect
        }

        isLoadingPairedData = true
        // Perform heavy processing in a background thread
        val processedList = withContext(Dispatchers.Default) { // Use Dispatchers.Default for CPU-bound work
            // Your existing logic for extracting columns and processing
            val kanjiMeishoIndex = 8
            val tensuShikibetsuIndex = 12
            val tensuIndex = 13
            val kanaMeishoIndex = 9

            if (master.columnsCount() > kanjiMeishoIndex &&
                master.columnsCount() > tensuShikibetsuIndex &&
                master.columnsCount() > tensuIndex &&
                master.columnsCount() > kanaMeishoIndex
            ) {
                try {
                    val kanjiMeishoCol = master.columns()[kanjiMeishoIndex]
                    val tensuShikibetsuCol = master.columns()[tensuShikibetsuIndex]
                    val tensuCol = master.columns()[tensuIndex]
                    val kanaMeishoCol = master.columns()[kanaMeishoIndex]

                    // --- HEAVY PART ---
                    // This will now run on Dispatchers.Default
                    val kanjiTextList: List<String> = kanjiMeishoCol.values().map { item ->
                        normalizeTextForSearch(item?.toString() ?: "")
                    }
                    val kanaTextList: List<String> = kanaMeishoCol.values().map { item ->
                        normalizeTextForSearch(item?.toString() ?: "")
                    }
                    // --- END OF HEAVY PART ---

                    (0 until master.rowsCount()).map { index ->
                        val kanjiMeishoValue = kanjiMeishoCol[index]?.toString()
                        val tensuShikibetsuValue = tensuShikibetsuCol[index]?.toString() ?: ""
                        val tensuValue = tensuCol[index]?.toString() ?: ""
                        val kanaMeishoValue = kanaMeishoCol[index]?.toString()

                        PairedTextItem(
                            kanjiMeisho = kanjiMeishoValue,
                            tensuShikibetsu = tensuShikibetsuValue,
                            tensu = tensuValue,
                            kanaMeisho = kanaMeishoValue,
                            originalIndex = index,
                            kanjiText = kanjiTextList.getOrElse(index) { "" },
                            kanaText = kanaTextList.getOrElse(index) { "" }
                        )
                    }
                } catch (e: IndexOutOfBoundsException) {
                    println("Error accessing columns for paired data: ${e.message}")
                    emptyList<PairedTextItem>() // Ensure type inference
                }
            } else {
                println("Not enough columns in DataFrame for paired data.")
                emptyList<PairedTextItem>() // Ensure type inference
            }
        }
        pairedDataList = processedList
        isLoadingPairedData = false
    }

    val expectedItemCount = pairedDataList.size // This will update when pairedDataList updates

    LaunchedEffect(pairedDataList) { // Key on the processed list
        if (pairedDataList.isNotEmpty()) { // Or some other condition to ensure it's ready
            loadListPairedData(
                context = context,
                expectedItemCount = expectedItemCount, // or directly pairedDataList.size
                pairedDataList = pairedDataList
            ).collect { loadedItems ->
                originalItems.clear()
                originalItems.addAll(loadedItems)
                // displayedItems = originalItems.toList() // Consider if you need to set displayedItems here
            }
        } else if (!isLoadingPairedData && master != null) {
            // Handle case where processing finished but list is empty (e.g., error or no data)
            originalItems.clear()
            // displayedItems = emptyList()
        }
    }

    MedGuidelinesScaffold (
        topBar = {
            TitleTopAppBar(
                title = R.string.ShikaShinryokoiMaster,
                navController = navController,
                references = listOf(
                    TextAndUrl(R.string.ShikaShinryokoiMaster, R.string.ShikaShinryokoiMasterUrl)
                )
            )
        },
    ) {innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth(),

            ) {
            MyCustomSearchBar(
                searchQuery = searchQuery,
                onSearchQueryChange = { newQuery ->
                    searchQuery = newQuery
                },
                onSearch = { query ->
                    println("Search submitted: $query")
                },
                isLoading = isFiltering || isLoadingPairedData
            )
            LazyColumn(
                state = lazyListState,
                modifier = Modifier
                    //.weight(1f)
                    .padding(2.dp)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(2.dp),
            ) {
                items(
                    items = displayedItems,
                    key = { pairedItem ->
                        pairedItem.originalIndex // Use the original row index as a stable key
                    }
                ) { pairedItem ->
                    ShinryokoiMasterScreenItemCard(
                        pairedItem = pairedItem,
                        isFavorite = pairedItem.isFavorite,
                        onFavoriteClick = { clickedPairedItem: PairedTextItem -> // Pass the actual item
                            // Find the item in the *originalItems* list
                            val itemIndexInOriginal =
                                originalItems.indexOfFirst { it.originalIndex == clickedPairedItem.originalIndex }

                            if (itemIndexInOriginal != -1) {
                                val itemToUpdate = originalItems[itemIndexInOriginal]
                                val oldFavoriteState = itemToUpdate.isFavorite

                                itemToUpdate.isFavorite = !itemToUpdate.isFavorite // Toggle favorite

                                originalItems.removeAt(itemIndexInOriginal) // Remove from current position

                                if (itemToUpdate.isFavorite) {
                                    // Add to the top of the list (or top of favorites section)
                                    originalItems.add(0, itemToUpdate)
                                } else {
                                    // Add back after all favorites, or maintain original relative order (simpler for now: add after favorites)
                                    val firstNonFavoriteIndex =
                                        originalItems.indexOfFirst { !it.isFavorite }
                                    if (firstNonFavoriteIndex != -1) {
                                        originalItems.add(firstNonFavoriteIndex, itemToUpdate)
                                    } else {
                                        originalItems.add(itemToUpdate) // Add to end if all were favorites
                                    }
                                }

                                // 3. Sort the new list: favorites first, then by original order or another criteria
                                val sortedList = originalItems.sortedWith(
                                    compareByDescending<PairedTextItem> { it.isFavorite }
                                    // Optional: then by original index to keep non-favorites in their loaded order
                                    //.thenBy { it.originalIndex }
                                )

                                // 4. Update the state and save
                                updateAndSaveItems(sortedList) // This function should update originalItems

                                displayedItems = sortedList
                                // 5. Scroll if it became a favorite
                                if (itemToUpdate.isFavorite && !oldFavoriteState) {
                                    // Find the new index of the item in the *displayedItems* list to scroll to it
                                    // This is more robust if the item is visible after filtering
                                    val displayIndex =
                                        displayedItems.indexOfFirst { it.originalIndex == itemToUpdate.originalIndex }
                                    if (displayIndex != -1) {
                                        scope.launch {
                                            lazyListState.animateScrollToItem(index = displayIndex)
                                        }
                                    } else {
                                        // If it became favorite but isn't in displayedItems (due to search),
                                        // scrolling to top might still be desired.
                                        scope.launch {
                                            lazyListState.animateScrollToItem(index = 0)
                                        }
                                        // }
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}


private fun loadListPairedData(
    context: Context,
    expectedItemCount: Int,
    pairedDataList: List<PairedTextItem>
): Flow<List<PairedTextItem>> {
    return context.dataStore.data.map { preferences ->
        val jsonString = preferences[LIST_PAIRED_DATA_KEY_SHIKA]

        if (jsonString != null) {
            try {
                val decodedList = Json.decodeFromString<List<PairedTextItem>>(jsonString)
                if (
                    decodedList.size == expectedItemCount
                    && doAnyDecodeStringsMatchAnyPairedItemsStrings(
                        context,
                        decodedList,
                        pairedDataList
                    )
                ) {
                    //if (areAnyDecodeNamesInItemsList(decodedList, itemsList)) {
                    decodedList
                    //}
                } else {
                    context.dataStore.edit { mutablePreferences ->
                        mutablePreferences.remove(LIST_PAIRED_DATA_KEY_SHIKA)
                    }
                    pairedDataList
                }
            } catch (e: kotlinx.serialization.SerializationException) {
                context.dataStore.edit { mutablePreferences ->
                    mutablePreferences.remove(LIST_PAIRED_DATA_KEY_SHIKA)
                }
                pairedDataList
            }
        } else {
            pairedDataList
        }
    }
}



@Preview
@Composable
fun ShikaShinryokoiMasterScreenPreview() {
    ShikaShinryokoiMasterScreen(navController = NavHostController(LocalContext.current))
}