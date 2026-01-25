package com.keiichi.medguidelines.data


import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

// アプリ全体で唯一のDataStoreインスタンス
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

// すべてのキーをここにまとめる
object DataStoreKeys {
    val LIST_ITEM_DATA_KEY = stringPreferencesKey("list_item_data")
    val COEFF_KEY = doublePreferencesKey("coeff_key")
    // 今後増える設定（ユーザー名、モードなど）もここに追加していく
}