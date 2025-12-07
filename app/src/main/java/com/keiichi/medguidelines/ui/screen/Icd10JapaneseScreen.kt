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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.keiichi.medguidelines.ui.component.TextAndUrl
import com.keiichi.medguidelines.ui.component.TitleTopAppBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import java.io.File
import java.nio.charset.Charset
import kotlin.collections.isNotEmpty

@Serializable
data class Icd10JapaneseItem(
    val code: String?,
    val description: String,
    val descriptionKana: String,
    val codeReseden: String,
    val originalIndex: Int,
    var isFavorite: Boolean = false,
    val normalizedDescriptionKana: String// = normalizeTextForSearch(description)
)

@Composable
fun Icd10JapaneseScreen(
    navController: NavHostController,
){
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    val lazyListState = remember(calculation = { LazyListState() })
    val numberOfColumns = 11
    val scope = rememberCoroutineScope()
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    var clickedItemForNavigation by remember { mutableStateOf<Icd10JapaneseItem?>(null) } // Renamed for clarity
    var icd10JapaneseItemList by rememberSaveable { mutableStateOf<List<Icd10JapaneseItem>>(emptyList()) }
    var isLoadingPairedData by remember { mutableStateOf(false) } // Optional loading state

    val originalItems = rememberSaveable(
        saver = listSaver(
            save = { it.map { item -> Json.encodeToString(item) } },
            restore = { restored ->
                restored.map { item -> Json.decodeFromString<Icd10JapaneseItem>(item) }
                    .toMutableStateList()
            }
        )
    ) {
        mutableStateListOf<Icd10JapaneseItem>()
    }
    // State for the query that actually triggers filtering
    var effectiveSearchQuery by remember { mutableStateOf("") }
    // State for the items to be displayed by LazyColumn
    var displayedItems by remember { mutableStateOf<List<Icd10JapaneseItem>>(emptyList()) }
    // State for loading indicator
    var isFiltering by remember { mutableStateOf(false) }

    // Debounce search query
    LaunchedEffect(searchQuery) {
        delay(350L) // Debounce time
        effectiveSearchQuery = searchQuery
    }

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
//                    val code = normalizeTextForSearch(itemData.code.toString()) // Use direct property if String?
//                    val description = normalizeTextForSearch(itemData.description.toString())   // Use direct property if String?
                    val code = itemData.code ?: ""
                    val description = itemData.description
                    val descriptionKana = itemData.normalizedDescriptionKana

                    // Check if ALL search terms are found in the item's text fields.
                    searchTerms.all { term -> // <<< Key change: using .all {}
                        // An item matches if the term is in EITHER kanjiText OR kanaText
                        code.contains(term) ||
                                description.contains(term) ||
                                descriptionKana.contains(term)
                    }
                }
            }
        }
        displayedItems = filtered
        isFiltering = false
    }
    fun updateAndSaveItems(updatedList: List<Icd10JapaneseItem>) {
        originalItems.clear()
        originalItems.addAll(updatedList)
        scope.launch {
            saveListIcd10Data(
                context, originalItems//.toMutableList()
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
        isLoadingPairedData = true
        val inputStream: InputStream = context.resources.openRawResource(R.raw.nmain516)
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

    LaunchedEffect(master) {
        if (master == null) {
            icd10JapaneseItemList = emptyList()
            isLoadingPairedData = false
            return@LaunchedEffect
        }
        isLoadingPairedData = true
        val processedList =
            withContext(Dispatchers.Default) { // Use Dispatchers.Default for CPU-bound work
                val codeIndex = 6
                val descriptionIndex = 2
                val descriptionKanaIndex = 3
                val codeResedenIndex = 10
                if (master.columnsCount() > codeIndex &&
                    master.columnsCount() > descriptionIndex &&
                    master.columnsCount() > descriptionKanaIndex &&
                    master.columnsCount() > codeResedenIndex
                ) {
                    try {
                        val codeListCol = master.columns()[codeIndex]
                        val descriptionListCol =
                            master.columns()[descriptionIndex]
                        val descriptionKanaListCol =
                            master.columns()[descriptionKanaIndex]
                        val codeResedenListCol =
                            master.columns()[codeResedenIndex]

                        //val normalizedCodeList: List<String> = codeListCol.values().map { item ->
                        //    normalizeTextForSearch(item?.toString() ?: "") }
                        val normalizedDescriptionKanaList: List<String> = descriptionKanaListCol.values().map { item ->
                            normalizeTextForSearch(item?.toString() ?: "") }

                        (0 until master.rowsCount()).map { index ->
                            val code = codeListCol[index]?.toString()
                            val description = descriptionListCol[index]?.toString() ?: ""
                            val descriptionKana = descriptionKanaListCol[index]?.toString() ?: ""
                            val codeReseden = codeResedenListCol[index]?.toString() ?: ""

                            Icd10JapaneseItem(
                                code = code,
                                description = description,
                                descriptionKana = descriptionKana,
                                codeReseden = codeReseden,
                                originalIndex = index,
                                //normalizedCode = normalizedCodeList.getOrElse(index) { "" },
                                normalizedDescriptionKana = normalizedDescriptionKanaList.getOrElse(index) {""}
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
        icd10JapaneseItemList = processedList
        isLoadingPairedData = false
    }

    val expectedItemCount = icd10JapaneseItemList.size

    LaunchedEffect(icd10JapaneseItemList) { // Key on the processed list
        if (icd10JapaneseItemList.isNotEmpty()) { // Or some other condition to ensure it's ready
            loadListIcdJapanesePairedData(
                context = context,
                expectedItemCount = expectedItemCount, // or directly pairedDataList.size
                icd10JapaneseItemList = icd10JapaneseItemList
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

    MedGuidelinesScaffold(
        topBar = {
            TitleTopAppBar(
                title = R.string.icd10japanese,
                navController = navController,
                references = listOf(
                    TextAndUrl(R.string.icd10, R.string.icd10JapaneseUrl)
                )
            )
        },
    ) { innerPadding ->
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
                isLoading = isFiltering || isLoadingPairedData,
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
                    Icd10JapaneseScreenItemCard(
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
                                    compareByDescending<Icd10JapaneseItem> { it.isFavorite }
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
fun Icd10JapaneseScreenItemCard(
    pairedItem: Icd10JapaneseItem,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit, // Callback for star click
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.onPrimary,
    contentColor: Color = MaterialTheme.colorScheme.primary,
) {
    Column {
        Card(
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
                    text = pairedItem.code?.toString() ?: "N/A",
                    modifier = Modifier
                        .weight(2f)
                        .padding(Dimensions.textPadding),
                )
                Text(
                    text = pairedItem.description?.toString() ?: "N/A",
                    modifier = Modifier
                        .weight(6f)
                        .padding(Dimensions.textPadding),
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

val LIST_ICD_JAPANESE_DATA_KEY = stringPreferencesKey("list_icd_japanese_data")

private suspend fun saveListIcd10Data(context: Context, item: MutableList<Icd10JapaneseItem>) {
    context.dataStore.edit { settings ->
        val jsonString = Json.encodeToString(item)
        settings[LIST_ICD_JAPANESE_DATA_KEY] = jsonString
    }
}

private fun loadListIcdJapanesePairedData(
    context: Context,
    expectedItemCount: Int,
    icd10JapaneseItemList: List<Icd10JapaneseItem>
): Flow<List<Icd10JapaneseItem>> {

    return context.dataStore.data.map { preferences ->
        val jsonString = preferences[LIST_ICD_JAPANESE_DATA_KEY]

        if (jsonString != null) {
            try {
                val decodedList = Json.decodeFromString<List<Icd10JapaneseItem>>(jsonString)
                if (
                    decodedList.size == expectedItemCount
                    && doAnyDecodeStringsMatchAnyIcd10ItemsStrings(
                        context,
                        decodedList,
                        icd10JapaneseItemList
                    )
                ) {
                    //if (areAnyDecodeNamesInItemsList(decodedList, itemsList)) {
                    decodedList
                    //}
                } else {
                    context.dataStore.edit { mutablePreferences ->
                        mutablePreferences.remove(LIST_ICD_JAPANESE_DATA_KEY)
                    }
                    icd10JapaneseItemList
                }
            } catch (e: kotlinx.serialization.SerializationException) {
                context.dataStore.edit { mutablePreferences ->
                    mutablePreferences.remove(LIST_ICD_JAPANESE_DATA_KEY)
                }
                icd10JapaneseItemList
            }
        } else {
            icd10JapaneseItemList
        }
    }
}

private fun doAnyDecodeStringsMatchAnyIcd10ItemsStrings(
    context: Context,
    decodeList: List<Icd10JapaneseItem>,
    itemsList: List<Icd10JapaneseItem>
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