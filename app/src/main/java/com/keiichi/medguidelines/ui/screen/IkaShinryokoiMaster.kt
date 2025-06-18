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
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
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
)// : IndexableItem

@Composable
fun IkaShinryokoiMasterScreen(navController: NavHostController) {

    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    val lazyListState = remember(calculation = { LazyListState() })
    val numberOfColumns = 50
    val scope = rememberCoroutineScope()
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
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
    // State for loading indicator
    var isFiltering by remember { mutableStateOf(false) }

    // Debounce search query
    LaunchedEffect(searchQuery) {
        delay(350L) // Debounce time
        effectiveSearchQuery = searchQuery
    }

    var displayedItems by remember { mutableStateOf<List<PairedTextItem>>(emptyList()) }

    LaunchedEffect(effectiveSearchQuery, originalItems.toList()) {
        //var displayedItems: List<PairedTextItem> = emptyList() //by remember { mutableStateOf<List<PairedTextItem>>(emptyList()) }
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
            saveListData(context, originalItems//.toMutableList()
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

    val pairedDataList: List<PairedTextItem> = remember(master) {
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

    val expectedItemCount = pairedDataList.size

    LaunchedEffect(Unit) {
        loadListPairedData(
            context = context,
            expectedItemCount = expectedItemCount,
            pairedDataList = pairedDataList
        ).collect { loadedItems ->
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
                isLoading = isFiltering
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
    onFavoriteClick: (item: PairedTextItem) -> Unit, // Callback for star click
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
                IconButton(onClick = { onFavoriteClick(pairedItem) }) { // Star icon
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

//val Context.dataStoreIka: DataStore<Preferences> by preferencesDataStore(name = "settings_ika")
val LIST_PAIRED_DATA_KEY = stringPreferencesKey("list_paired_data")

suspend fun saveListData(context: Context, items: MutableList<PairedTextItem>) {
//    saveListToDataStore(
//        context = context,
//        dataStoreKey = LIST_PAIRED_DATA_KEY,
//        items = items,
//        //serializer = ListSerializer(PairedTextItem.serializer()) // Provide the serializer
//    )
    context.dataStore.edit { settings ->
        val jsonString = Json.encodeToString(items)
        settings[LIST_PAIRED_DATA_KEY] = jsonString
    }
}

private fun doAnyDecodeStringsMatchAnyPairedItemsStrings(
    context: Context,
    decodeList: List<PairedTextItem>,
    itemsList: List<PairedTextItem>
): Boolean {
//    return compareResourceStringSetsFromIndexable(
//        context = context,
//        listA = decodeList,
//        listB = itemsList
//    )
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

private fun loadListPairedData(
    context: Context,
    expectedItemCount: Int,
    pairedDataList: List<PairedTextItem>
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