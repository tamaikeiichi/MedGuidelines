package com.keiichi.medguidelines.data

import android.content.Context
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


// Usage with DataStore:
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
val LIST_ITEM_DATA_KEY = stringPreferencesKey("list_item_data")

suspend fun saveListItemData(context: Context, item: MutableList<ListItemData>) {
    context.dataStore.edit { settings ->
        val jsonString = Json.encodeToString(item)
        settings[LIST_ITEM_DATA_KEY] = jsonString
    }
}

fun loadListItemData(context: Context): Flow<List<ListItemData>> {
    return context.dataStore.data.map { preferences ->
        val jsonString = preferences[LIST_ITEM_DATA_KEY]
        if (jsonString != null) {
            Json.decodeFromString<List<ListItemData>>(jsonString)
        } else {
            itemsList
        }
    }
}