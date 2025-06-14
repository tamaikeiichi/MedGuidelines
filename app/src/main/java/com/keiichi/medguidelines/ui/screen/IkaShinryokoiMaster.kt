package com.keiichi.medguidelines.ui.screen

import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.keiichi.medguidelines.ui.component.MedGuidelinesScaffold
import com.keiichi.medguidelines.R
import com.keiichi.medguidelines.ui.component.MyCustomSearchBar
import java.io.InputStream
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
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
import androidx.navigation.NavHostController
import com.keiichi.compose.DarkYellow
import com.keiichi.compose.Yellow
import com.keiichi.medguidelines.data.dataStore
import com.keiichi.medguidelines.ui.component.Dimensions
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.keiichi.medguidelines.ui.component.TextAndUrl
import com.keiichi.medguidelines.ui.component.TitleTopAppBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable

@Serializable
data class PairedTextItem(
    val kanjiMeisho: String?,
    val tensuShikibetsu: String,
    val tensu: String,
    val kanaMeisho: String?,
    val originalIndex: Int,
    var isFavorite: Boolean = false,
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
    val expectedItemCount = pairedDataList.size
    var clickedItemForNavigation by remember { mutableStateOf<PairedTextItem?>(null) } // Renamed for clarity

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
    var displayedItems by remember { mutableStateOf<List<PairedTextItem>>(emptyList()) }
    // State for loading indicator
    var isFiltering by remember { mutableStateOf(false) }

    // Debounce search query
    LaunchedEffect(searchQuery) {
        delay(350L) // Debounce time
        effectiveSearchQuery = searchQuery
    }

    // Perform filtering in the background when effectiveSearchQuery or originalItems change
//    LaunchedEffect(effectiveSearchQuery, originalItems.toList()) { // Use .toList() to react to content changes
//        if (effectiveSearchQuery.isBlank()) {
//            displayedItems = originalItems.toList() // Show all if query is blank
//            isFiltering = false
//            return@LaunchedEffect
//        }
//
//        isFiltering = true
//        val currentQuery = effectiveSearchQuery // Capture current query
//        val currentList = originalItems.toList() // Capture current list
//
//        val filtered = withContext(Dispatchers.Default) {
//            val normalizedQuery = normalizeTextForSearch(currentQuery)
//            if (normalizedQuery.isBlank()){
//                currentList // Should be handled by outer if, but good to be safe
//            } else {
//                currentList.filter { itemData ->
//                    // Assuming normalizeTextForSearch handles nulls gracefully (e.g. returns "")
//                    val kanjiText = normalizeTextForSearch(itemData.kanjiMeisho?.toString().toString())
//                    val kanaText = normalizeTextForSearch(itemData.kanaMeisho?.toString().toString())
//
//                    kanjiText.contains(normalizedQuery) || kanaText.contains(normalizedQuery)
//                }
//            }
//        }
//        displayedItems = filtered
//        isFiltering = false
//    }

    LaunchedEffect(effectiveSearchQuery, originalItems.toList()) {
        if (effectiveSearchQuery.isBlank()) {
            displayedItems = originalItems.toList()
            isFiltering = false
            return@LaunchedEffect
        }

        isFiltering = true
        val currentRawQuery = effectiveSearchQuery
        val currentList = originalItems.toList()

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
                    val kanjiText = normalizeTextForSearch(itemData.kanjiMeisho.toString().toString()) // Use direct property if String?
                    val kanaText = normalizeTextForSearch(itemData.kanaMeisho.toString().toString())   // Use direct property if String?

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
            saveListPairedData(context, originalItems//.toMutableList()
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
    } finally {
    }

    pairedDataList = remember(master) {
        if (master == null) return@remember emptyList()

        val kanjiMeishoIndex = 4  // Column "5"
        val tensuShikibetsuIndex = 10
        val tensuIndex = 11 // Column "10"
        val kanaMeishoIndex = 6

        if (master.columnsCount() > kanjiMeishoIndex &&
            master.columnsCount() > tensuShikibetsuIndex &&
            master.columnsCount() > tensuIndex &&
            master.columnsCount() > kanaMeishoIndex
        ) {
            try {
                val kanjiMeishoList = master.columns()[kanjiMeishoIndex].toList()
                val tensuShikibetsuList =
                    master.columns()[tensuShikibetsuIndex].toList().map { value ->
                        value?.toString()
                            ?: "" // Or parse to Int if needed: value.toString().toIntOrNull()
                    }
                val tensuList = master.columns()[tensuIndex].toList().map { value ->
                    value?.toString() ?: ""
                }
                val kanaMeishoList = master.columns()[kanaMeishoIndex].toList()

                kanjiMeishoList.zip(tensuShikibetsuList)
                    .zip(tensuList)
                    .zip(kanaMeishoList)
                    .mapIndexed { index, nestedPair ->
                        val firstPair = nestedPair.first.first
                        val kanjiMeisho = firstPair.first
                        val shikibetsu = firstPair.second
                        val currentTensu = nestedPair.first.second
                        val kanaMeisho = nestedPair.second

                        PairedTextItem(
                            kanjiMeisho = kanjiMeisho.toString(),
                            tensuShikibetsu = shikibetsu,
                            tensu = currentTensu,
                            kanaMeisho = kanaMeisho.toString(),
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


//    val displayedItems = remember(searchQuery, originalItems, context) {
//        if (searchQuery.isBlank()) {
//            originalItems
//        } else {
//            val normalizedSearchQuery = normalizeTextForSearch(searchQuery)
//            originalItems.filter { itemData ->
//                val kanjiText =
//                    normalizeTextForSearch(itemData.kanjiMeisho?.toString().toString())
//                val kanaText =
//                    normalizeTextForSearch(itemData.kanaMeisho?.toString().toString())
//                kanjiText.contains(normalizedSearchQuery) || kanaText.contains(
//                    normalizedSearchQuery
//                )
//            }
//        }
//    }

    LaunchedEffect(Unit) {
        loadListPairedData(context, expectedItemCount).collect { loadedItems ->
            originalItems.clear()
            originalItems.addAll(loadedItems)
        }
    }

    MedGuidelinesScaffold (
        topBar = {
            TitleTopAppBar(
                title = R.string.IkaShinryokoiMaster,
                navController = navController,
                references = listOf(
                    TextAndUrl(R.string.IkaShinryokoiMaster, R.string.IkaShinryokoiMasterUrl)
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
                    IkaShinryokoiMasterScreenItemCard(
                        pairedItem = pairedItem,
                        isFavorite = pairedItem.isFavorite,
                        onFavoriteClick = {
                            val currentList = originalItems.toMutableList()
                            val itemIndex =
                                currentList.indexOfFirst { it.originalIndex == pairedItem.originalIndex }
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
                                    compareByDescending<PairedTextItem> { it.isFavorite }
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
}

@Composable
fun IkaShinryokoiMasterScreenItemCard(
    pairedItem: PairedTextItem,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit, // Callback for star click
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.onPrimary,
    contentColor: Color = MaterialTheme.colorScheme.primary,
) {
    Column {
        Card (
            modifier = modifier
                .fillMaxWidth()
                .padding(Dimensions.cardPadding),
            colors = CardDefaults.cardColors(
                containerColor = containerColor,
                contentColor = contentColor
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween // To push star to the end
            ) {
                Text(
                    text = pairedItem.kanjiMeisho?.toString() ?: "N/A",
                    modifier = Modifier
                        .weight(4f)
                        .padding(Dimensions.textPadding),
                )
                val displayTensu = pairedItem.tensu.removeSuffix(".00")
                Text(
                    text = "${displayTensu}" +
                            if (pairedItem.tensuShikibetsu == "1") {
                                "円"
                            } else {
                                "点"
                            },
                    modifier = Modifier
                        .weight(1f)
                        .padding(Dimensions.textPadding),
                    textAlign = TextAlign.End
                )
                IconButton(onClick = onFavoriteClick) { // Star icon
                    val starColor = if (isFavorite) {
                        if (isSystemInDarkTheme()) {
                            DarkYellow // Use your defined dark yellow for dark theme
                        } else {
                            Yellow // Standard yellow for light theme
                        }
                    } else {
                        LocalContentColor.current // Or MaterialTheme.colorScheme.onSurface.copy(alpha = ContentAlpha.medium)
                    }
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.StarOutline,
                        contentDescription = if (isFavorite) "Unmark as favorite" else "Mark as favorite",
                        tint = if (isFavorite) starColor else LocalContentColor.current,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
        }
    }
}

// Ensure normalizeTextForSearch is efficient and handles nulls:
private fun normalizeTextForSearchForMaster(text: String?): String {
    return text?.trim()?.lowercase() ?: ""
    // Consider more advanced Japanese normalization here if needed.
    // Every operation here is magnified by the number of items * number of fields.
}

private suspend fun saveListPairedData(context: Context, item: MutableList<PairedTextItem>) {
    context.dataStore.edit { settings ->
        val jsonString = Json.encodeToString(item)
        settings[LIST_PAIRED_DATA_KEY] = jsonString
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


    // Check if there is any intersection between the two sets of strings
    val checkName = decodeNameResIdStrings == itemNameResIdStrings
    //val checkKeywords = decodeKeywordStrings == itemKeywordStrings
    return checkName //&& checkKeywords
}

val LIST_PAIRED_DATA_KEY = stringPreferencesKey("list_paired_data")

private fun loadListPairedData(
    context: Context,
    expectedItemCount: Int
): Flow<List<PairedTextItem>> {

    return context.dataStore.data.map { preferences ->
        val jsonString = preferences[LIST_PAIRED_DATA_KEY]

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
                        mutablePreferences.remove(LIST_PAIRED_DATA_KEY)
                    }
                    pairedDataList
                }
            } catch (e: kotlinx.serialization.SerializationException) {
                context.dataStore.edit { mutablePreferences ->
                    mutablePreferences.remove(LIST_PAIRED_DATA_KEY)
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
fun IkaShinryokoiMasterScreenPreview() {
    IkaShinryokoiMasterScreen(navController = NavHostController(LocalContext.current))
}