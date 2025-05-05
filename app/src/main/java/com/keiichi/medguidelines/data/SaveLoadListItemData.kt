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
//        if (jsonString != null) {
//                Json.decodeFromString<List<ListItemData>>(jsonString)
//            //val test = "test"
//           // Log.println(Log.DEBUG, "loadListItemData", jsonString)
//        } else {
//            // Return the default itemsList if jsonString is null
//            itemsList
//        }
        if (jsonString != null) {
            try {
            //Log.println(Log.DEBUG, "loadListItemData", jsonString)
            val decodedList = Json.decodeFromString<List<ListItemData>>(jsonString)
            //Log.println(Log.DEBUG, "loadListItemData", decodedList.toString())
            // Check if the decoded list has the expected size
            if (decodedList.size == expectedItemCount) {
                // If the size matches, restore the items
                decodedList
            } else {
                context.dataStore.edit { mutablePreferences ->
                    mutablePreferences.remove(LIST_ITEM_DATA_KEY)
                }
                itemsList
            }
            } catch (e: kotlinx.serialization.SerializationException) {
                // Handle the deserialization error
                // Remove the incorrect value
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

