package com.keiichi.medguidelines.data

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.keiichi.medguidelines.ui.screen.itemsList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.collections.remove


// Usage with DataStore:
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
val LIST_ITEM_DATA_KEY = stringPreferencesKey("list_item_data")

suspend fun saveListItemData(context: Context, item: MutableList<ListItemData>) {
    context.dataStore.edit { settings ->
        val jsonString = Json.encodeToString(item)
        settings[LIST_ITEM_DATA_KEY] = jsonString
    }
}

fun loadListItemData(context: Context, expectedItemCount: Int): Flow<List<ListItemData>> {

    return context.dataStore.data.map { preferences ->
        val jsonString = preferences[LIST_ITEM_DATA_KEY]

        if (jsonString != null) {
            try {
            val decodedList = Json.decodeFromString<List<ListItemData>>(jsonString)
            if (
                decodedList.size == expectedItemCount
                && doAnyDecodeStringsMatchAnyItemsStrings(context, decodedList, itemsList)
                ) {
                //if (areAnyDecodeNamesInItemsList(decodedList, itemsList)) {
                    decodedList
                //}
            } else {
                context.dataStore.edit {
                    mutablePreferences ->
                    mutablePreferences.remove(LIST_ITEM_DATA_KEY)
                }
                itemsList
            }
            } catch (e: kotlinx.serialization.SerializationException) {
                context.dataStore.edit { mutablePreferences ->
                    mutablePreferences.remove(LIST_ITEM_DATA_KEY)
                }
                itemsList
            }
        } else {
            // If jsonString is null, return the default itemsList
            itemsList
        }
    }
}

fun doAnyDecodeStringsMatchAnyItemsStrings(
    context: Context,
    decodeList: List<ListItemData>,
    itemsList: List<ListItemData>
): Boolean {
    // Get the set of strings from decodeList.nameResID (Set A)
    val decodeStrings = decodeList.mapNotNull { listItem ->
        try {
            context.getString(listItem.nameResId)
        } catch (e: Exception) {
            // Handle invalid resource ID, filter out if needed
            println("Error: Invalid resource ID in decodeList: ${listItem.nameResId}")
            null // Exclude this string from the set
        }
    }.toSet()

    // Get the set of strings from itemsList.nameResID (Set B)
    val itemStrings = itemsList.mapNotNull { listItem ->
        try {
            context.getString(listItem.nameResId)
        } catch (e: Exception) {
            // Handle invalid resource ID, filter out if needed
            println("Error: Invalid resource ID in itemsList: ${listItem.nameResId}")
            null // Exclude this string from the set
        }
    }.toSet()

    // Check if there is any intersection between the two sets of strings
    return decodeStrings == itemStrings
}

