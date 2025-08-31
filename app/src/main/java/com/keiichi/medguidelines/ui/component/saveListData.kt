package com.keiichi.medguidelines.ui.component

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.keiichi.medguidelines.data.PairedTextItem
import com.keiichi.medguidelines.data.dataStore
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

suspend fun saveListData(
    context: Context,
    items: List<PairedTextItem>, // It's good practice to use List instead of MutableList for parameters if you don't modify it inside
    dataStoreKey: Preferences.Key<String>
) {
    context.dataStore.edit { settings ->
        val jsonString = Json.encodeToString(items)
        settings[dataStoreKey] = jsonString
    }
}
