package com.example.medguidelines

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.medguidelines.ui.screen.ListItemData
import kotlinx.coroutines.flow.internal.NoOpContinuation.context
import okio.Path.Companion.toPath

data class UserPreferences(val itemOrder: List<Int>)

// shared/src/androidMain/kotlin/createDataStore.android.kt

// shared/src/androidMain/kotlin/createDataStore.kt

/**
 * Gets the singleton DataStore instance, creating it if necessary.
 */
object PreferenceKeys {
    var ID = intPreferencesKey ("id")
    val NAME = stringPreferencesKey ("name")
}

val TEXT_KEY = intPreferencesKey("example_text")

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

fun saveListItemData(items: List<ListItemData>) : List<Int> {
    fun createDataStore(producePath: () -> String): DataStore<Preferences> =
        PreferenceDataStoreFactory.createWithPath(
            produceFile = { producePath().toPath() }
        )

    val dataStore : DataStore <Preferences> by lazy {
        createDataStore(producePath = { "example" })
    }

    val itemsMap = mutableMapOf(
        items[0] to 0,
        items[1] to 1,
    )
    val itemOrder = itemsMap.values.toList()

    PreferenceKeys.ID = itemOrder

    dataStore.edit { settings ->
        settings[TEXT_KEY] = itemOrder
    }



}




class UserPreferencesRepository(
    private val userPreferencesStore: DataStore<UserPreferences>,
    context: Context
) { ... }


