package com.example.medguidelines.data

import android.content.Context
import androidx.core.content.edit
import androidx.datastore.core.DataStore
import kotlin.text.first
import kotlin.text.map
import kotlin.text.split
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

data class IndexSequenceDataStore(private  val context: Context) {
    private val Context.dataStore: DataStore<androidx.datastore.preferences.core.Preferences> by preferencesDataStore(name = "index_preferences")

    suspend fun getItemOrder(): List<Int> {
        return context.dataStore.data.map { preferences ->
            preferences[PreferencesKeys.ITEM_ORDER]?.split(",")?.map { it.toInt() } ?: emptyList()
        }.first()
    }

    suspend fun saveItemOrder(itemOrder: List<Int>) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.ITEM_ORDER] = itemOrder.joinToString(",")
        }
    }

    private object PreferencesKeys {
        val ITEM_ORDER = stringPreferencesKey("item_order")
    }
}
