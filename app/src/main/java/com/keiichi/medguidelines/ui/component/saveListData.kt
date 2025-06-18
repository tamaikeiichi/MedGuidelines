package com.keiichi.medguidelines.ui.component


import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

// Extension property to access DataStore
//val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_settings") // You can keep one generic name or make it configurable
//
///**
// * Saves a list of items to DataStore as a JSON string.
// *
// * @param T The type of items in the list.
// * @param context The Android context.
// * @param dataStoreKey The Preferences.Key<String> to use for storing the data.
// * @param items The list of items to save.
// * @param serializer The KSerializer for the item type T.
// */
//suspend inline fun <reified T> saveListToDataStore(
//    context: Context,
//    dataStoreKey: Preferences.Key<String>,
//    items: List<T>,
//    //serializer: KSerializer<List<T>> // Pass the serializer for List<T>
//) {
//    context.dataStore.edit { settings ->
//        val jsonString = Json.encodeToString(items)
//        settings[dataStoreKey] = jsonString
//    }
//}