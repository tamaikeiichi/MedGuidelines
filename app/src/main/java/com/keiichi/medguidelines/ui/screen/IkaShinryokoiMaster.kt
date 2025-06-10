package com.keiichi.medguidelines.ui.screen

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.keiichi.medguidelines.ui.component.MedGuidelinesCard
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavHostController
import com.keiichi.medguidelines.data.LIST_ITEM_DATA_KEY
import com.keiichi.medguidelines.data.ListItemData
import com.keiichi.medguidelines.data.dataStore
import com.keiichi.medguidelines.data.doAnyDecodeStringsMatchAnyItemsStrings
import com.keiichi.medguidelines.data.itemsList
import com.keiichi.medguidelines.data.loadListItemData
import com.keiichi.medguidelines.data.saveListItemData
import com.keiichi.medguidelines.ui.component.Dimensions
import com.keiichi.medguidelines.ui.component.getStringOfSpecificLocale
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
import java.util.Locale
import kotlin.collections.any
import kotlin.collections.map
import kotlin.text.contains

// Define a simple wrapper class
data class IndexedItem<T>(val index: Int, val data: T)

// Define a data class to hold the paired data for clarity (optional but recommended)
data class PairedTextItem(
    val shinryoKoiKanjiMeisho: Any?,
    val tensuShikibetsu: String,
    val tensu: String, val originalIndex: Int
)

var pairedDataList: List<PairedTextItem> = listOf()

@Composable
fun IkaShinryokoiMasterScreen(navController: NavHostController) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    val lazyListState = remember(calculation = { LazyListState() })
    val numberOfColumns = 50
    val scope = rememberCoroutineScope()
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    var hasBeenVisited by rememberSaveable { mutableStateOf(false) }

    val master: AnyFrame? = try {
        val inputStream: InputStream = context.resources.openRawResource(R.raw.s_20250602)
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
    }finally {
    }

    MedGuidelinesScaffold {
        Column {
            MyCustomSearchBar(
                searchQuery = searchQuery,
                onSearchQueryChange = { newQuery ->
                    searchQuery = newQuery
                },
                onSearch = { query ->
                    println("Search submitted: $query")
                },
            )
            //val numberFormatter = remember { DecimalFormat("#.##") }
            // Prepare your list of paired data
            pairedDataList = remember(master) {
                if (master == null) return@remember emptyList()

                // Adjust indices if your header generation (1..50) means columns are 0-indexed internally
                // If header = listOf("1", "2", ...), then "5" is at index 4, "10" is at index 9.
                val shinryoKoiKanjiMeishoIndex = 4  // Column "5"
                val tensuShikibetsuIndex = 10
                val tensuIndex = 11 // Column "10"

                if (master.columnsCount() > shinryoKoiKanjiMeishoIndex &&
                    master.columnsCount() > tensuShikibetsuIndex && // Check new column index
                    master.columnsCount() > tensuIndex) {
                    try {
                        val shinryoKoiKanjiMeishoList = master.columns()[shinryoKoiKanjiMeishoIndex].toList()

                        // Extract the tensuShikibetsu column and map to String (or desired type)
                        val tensuShikibetsuList = master.columns()[tensuShikibetsuIndex].toList().map { value ->
                            value?.toString() ?: "" // Or parse to Int if needed: value.toString().toIntOrNull()
                        }

                        val tensuList = master.columns()[tensuIndex].toList().map { value ->
                            value?.toString() ?: ""
                        }

                        // Combine the three lists
                        // First, zip shinryoKoiKanjiMeishoList and tensuShikibetsuList
                        shinryoKoiKanjiMeishoList.zip(tensuShikibetsuList)
                            // Result: List<Pair<Any?, String>> (shinryoKoiKanjiMeisho, tensuShikibetsu)
                            // Then, zip this result with tensuList
                            .zip(tensuList)
                            // Result: List<Pair<Pair<Any?, String>, String>>
                            // ((shinryoKoiKanjiMeisho, tensuShikibetsu), tensu)
                            .mapIndexed { index, nestedPair ->
                                val firstPair = nestedPair.first // This is Pair(shinryoKoiKanjiMeisho, tensuShikibetsu)
                                val kanjiMeisho = firstPair.first
                                val shikibetsu = firstPair.second
                                val currentTensu = nestedPair.second // This is tensu

                                PairedTextItem(
                                    shinryoKoiKanjiMeisho = kanjiMeisho,
                                    tensuShikibetsu = shikibetsu,
                                    tensu = currentTensu,
                                    originalIndex = index
                                )
                            }
                    } catch (e: IndexOutOfBoundsException) {
                        println("Error accessing columns for paired data: ${e.message}")
                        emptyList()
                    }
                } else {
                    println("Not enough columns in DataFrame for paired data.")
                    emptyList()
                }
            }

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

            var clickedItemForNavigation by remember { mutableStateOf<PairedTextItem?>(null) }

            fun updateAndSaveItems(updatedList: List<PairedTextItem>) {
                originalItems.clear()
                originalItems.addAll(updatedList)
                scope.launch {
                    saveListPairedData(context, originalItems)
                }
            }

            val expectedItemCount = pairedDataList.size

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
                                getStringOfSpecificLocale(context, itemData.originalIndex, englishLocale)
                            val itemNameJa =
                                getStringOfSpecificLocale(context, itemData.originalIndex, japaneseLocale)

                            val normalizedItemNameEn = normalizeTextForSearch(itemNameEn)
                            val normalizedItemNameJa = normalizeTextForSearch(itemNameJa)

                            (normalizedItemNameEn.isNotBlank() && normalizedItemNameEn.contains(
                                normalizedSearchQuery
                            )) ||
                                    (normalizedItemNameJa.isNotBlank() && normalizedItemNameJa.contains(
                                        normalizedSearchQuery
                                    ))
                        }

                        val nameMatchIds = nameMatchingItems.map { it.originalIndex }.toSet()

                        val keywordOnlyMatchingItems = originalItems.filter { itemData ->
                            if (itemData.originalIndex in nameMatchIds) {
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

            LazyColumn(
                state = lazyListState,
                modifier = Modifier
                    .padding(2.dp)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(2.dp),
            ) {
                items(
                    items = pairedDataList,
                    key = { pairedItem ->
                        pairedItem.originalIndex // Use the original row index as a stable key
                    }
                ) { pairedItem ->
                    MedGuidelinesCard(
                        modifier = Modifier.padding(Dimensions.cardPadding)
                    ) {
                        // Use a Row to display items side-by-side
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(Dimensions.textPadding),
                            verticalAlignment = Alignment.CenterVertically // Optional: align items vertically
                        ) {
                            Text(
                                text = pairedItem.shinryoKoiKanjiMeisho?.toString() ?: "N/A",
                                modifier = Modifier
                                    .weight(4f)
                                    .padding(Dimensions.textPadding),
                            )
                            val displayTensu = pairedItem.tensu.removeSuffix(".00")
                            Text(
                                text = "${displayTensu}" +
                                        if (pairedItem.tensuShikibetsu == "1"){
                                        "円"} else {"点"},
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(Dimensions.textPadding),
                                textAlign = TextAlign.End
                            )
                        }
                    }
                }
            }
            MedGuidelinesCard {
                if (master != null) {
                    // Now you can work with 'master' DataFrame
                    // Example: Display the number of rows
                    Text("Number of rows: ${master.rowsCount()}")

                    // Example: Print the first 5 rows to logcat (for debugging)
                    master.head().print()

                    // Example: Get a specific column as a list
                    // val firstColumnData = master.columns().firstOrNull()?.toList()

                    // Example: Filter and display
                    // val filteredData = master.filter { it["SomeColumnName"] > someValue }
                    // LazyColumn {
                    //     items(filteredData.rows().toList()) { row ->
                    //         Text(row.toString()) // Customize how you display each row
                    //     }
                    // }

                } else {
                    Text("Could not load data.")
                }
            }
        }
    }
}

private suspend fun saveListPairedData(context: Context, item: MutableList<PairedTextItem>) {
    context.dataStore.edit { settings ->
        val jsonString = Json.encodeToString(item)
        settings[LIST_ITEM_DATA_KEY] = jsonString
    }
}

fun doAnyDecodeStringsMatchAnyPairedItemsStrings(
    context: Context,
    decodeList: List<PairedTextItem>,
    itemsList: List<PairedTextItem>
): Boolean {
    // Get the set of strings from decodeList.nameResID (Set A)
    val decodeNameResIdStrings = decodeList.mapNotNull { listItem ->
        try {
            context.getString(listItem.originalIndex)
        } catch (e: Exception) {
            // Handle invalid resource ID, filter out if needed
            println("Error: Invalid resource ID in decodeList: ${listItem.originalIndex}")
            null // Exclude this string from the set
        }
    }.toSet()


    // Get the set of strings from itemsList.nameResID (Set B)
    val itemNameResIdStrings = itemsList.mapNotNull { listItem ->
        try {
            context.getString(listItem.originalIndex)
        } catch (e: Exception) {
            // Handle invalid resource ID, filter out if needed
            println("Error: Invalid resource ID in itemsList: ${listItem.originalIndex}")
            null // Exclude this string from the set
        }
    }.toSet()

//    val itemKeywordStrings: Set<String> = itemsList.flatMap { listItem ->
//        // listItem.keywords is now List<Int>
//        listItem.keywords.mapNotNull { keywordResId -> // Iterate over each keyword ID in the list
//            try {
//                context.getString(keywordResId)
//            } catch (e: Exception) {
//                println("Error: Invalid resource ID in decodeList keywords: $keywordResId")
//                null
//            }
//        }
//    }.toSet()
    // Check if there is any intersection between the two sets of strings
    val checkName = decodeNameResIdStrings == itemNameResIdStrings
    //val checkKeywords = decodeKeywordStrings == itemKeywordStrings
    return checkName //&& checkKeywords
}

val LIST_PAIRED_DATA_KEY = stringPreferencesKey("list_paired_data")

private fun loadListPairedData(context: Context, expectedItemCount: Int): Flow<List<PairedTextItem>> {

    return context.dataStore.data.map { preferences ->
        val jsonString = preferences[LIST_PAIRED_DATA_KEY]

        if (jsonString != null) {
            try {
                val decodedList = Json.decodeFromString<List<PairedTextItem>>(jsonString)
                if (
                    decodedList.size == expectedItemCount
                    && doAnyDecodeStringsMatchAnyPairedItemsStrings(context, decodedList, pairedDataList)
                ) {
                    //if (areAnyDecodeNamesInItemsList(decodedList, itemsList)) {
                    decodedList
                    //}
                } else {
                    context.dataStore.edit {
                            mutablePreferences ->
                        mutablePreferences.remove(LIST_ITEM_DATA_KEY)
                    }
                    pairedDataList
                }
            } catch (e: kotlinx.serialization.SerializationException) {
                context.dataStore.edit { mutablePreferences ->
                    mutablePreferences.remove(LIST_ITEM_DATA_KEY)
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
fun IkaShinryokoiMasterScreenPreview(){
    IkaShinryokoiMasterScreen(navController = NavHostController(LocalContext.current))
}