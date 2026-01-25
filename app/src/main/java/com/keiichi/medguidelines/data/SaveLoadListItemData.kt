package com.keiichi.medguidelines.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
//import com.keiichi.medguidelines.ui.screen.localeAwareAppItems

// Usage with DataStore:
//val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
//val LIST_ITEM_DATA_KEY = stringPreferencesKey("list_item_data")
//
//val COEFF_KEY = androidx.datastore.preferences.core.doublePreferencesKey("coeff_key")
suspend fun saveListItemData(context: Context, item: MutableList<ListItemData>) {
    context.dataStore.edit { settings ->
        val jsonString = Json.encodeToString(item)
        settings[DataStoreKeys.LIST_ITEM_DATA_KEY] = jsonString
    }
}

fun loadListItemData(
    context: Context,
    expectedItemCount: Int,
    initialItemsLists: List<ListItemData> = itemsList
): Flow<List<ListItemData>> {
    return context.dataStore.data.map { preferences ->
        val jsonString = preferences[DataStoreKeys.LIST_ITEM_DATA_KEY]

        if (jsonString != null) {
            try {
            val decodedList = Json.decodeFromString<List<ListItemData>>(jsonString)
            if (
                decodedList.size == expectedItemCount
                && doAnyDecodeStringsMatchAnyItemsStrings(
                    context, decodedList, initialItemsLists
                 )
                ) {
                //if (areAnyDecodeNamesInItemsList(decodedList, itemsList)) {
                    decodedList
                //}
            } else {
                context.dataStore.edit {
                    mutablePreferences ->
                    mutablePreferences.remove(DataStoreKeys.LIST_ITEM_DATA_KEY)
                }
                initialItemsLists
            }
            } catch (e: kotlinx.serialization.SerializationException) {
                context.dataStore.edit { mutablePreferences ->
                    mutablePreferences.remove(DataStoreKeys.LIST_ITEM_DATA_KEY)
                }
                initialItemsLists
            }
        } else {
            // If jsonString is null, return the default itemsList
            initialItemsLists
        }
    }
}

fun doAnyDecodeStringsMatchAnyItemsStrings(
    context: Context,
    decodeList: List<ListItemData>,
    itemsList: List<ListItemData>
): Boolean {
    // Get the set of strings from decodeList.nameResID (Set A)
    val decodeNameResIdStrings = decodeList.mapNotNull { listItem ->
        try {
            context.getString(listItem.nameResId)
        } catch (e: Exception) {
            // Handle invalid resource ID, filter out if needed
            println("Error: Invalid resource ID in decodeList: ${listItem.nameResId}")
            null // Exclude this string from the set
        }
    }.toSet()

    val decodeKeywordStrings: Set<String> = decodeList.flatMap { listItem ->
        // listItem.keywords is now List<Int>
        listItem.keywords.mapNotNull { keywordResId -> // Iterate over each keyword ID in the list
            try {
                context.getString(keywordResId)
            } catch (e: Exception) {
                println("Error: Invalid resource ID in decodeList keywords: $keywordResId")
                null
            }
        }
    }.toSet()
    // Get the set of strings from itemsList.nameResID (Set B)
    val itemNameResIdStrings = itemsList.mapNotNull { listItem ->
        try {
            context.getString(listItem.nameResId)
        } catch (e: Exception) {
            // Handle invalid resource ID, filter out if needed
            println("Error: Invalid resource ID in itemsList: ${listItem.nameResId}")
            null // Exclude this string from the set
        }
    }.toSet()

    val itemKeywordStrings: Set<String> = itemsList.flatMap { listItem ->
        // listItem.keywords is now List<Int>
        listItem.keywords.mapNotNull { keywordResId -> // Iterate over each keyword ID in the list
            try {
                context.getString(keywordResId)
            } catch (e: Exception) {
                println("Error: Invalid resource ID in decodeList keywords: $keywordResId")
                null
            }
        }
    }.toSet()
    // Check if there is any intersection between the two sets of strings
    val checkName = decodeNameResIdStrings == itemNameResIdStrings
    val checkKeywords = decodeKeywordStrings == itemKeywordStrings
    return checkName && checkKeywords
}

