package com.keiichi.medguidelines.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first

/*
 * 同梱マスタCSVの取り込み状態をDataStoreで管理する。
 * テーブルごとに取り込み済みCSVのリソース名（例: s_20261013）を記録し、
 * CSVを差し替えてリソース名が変わったら再取り込みが必要と判定する。
 */

private fun loadedMasterKey(table: String) = stringPreferencesKey("loaded_master_$table")

/** テーブルが空、または記録済みのCSVと[resourceId]が異なる場合にtrue */
internal suspend fun needsMasterReload(context: Context, table: String, resourceId: Int, count: Int): Boolean {
    if (count == 0) return true
    val loaded = context.dataStore.data.first()[loadedMasterKey(table)]
    return loaded != context.resources.getResourceEntryName(resourceId)
}

/** 取り込みが完了したCSVを記録する（取り込み成功後に呼ぶこと） */
internal suspend fun markMasterLoaded(context: Context, table: String, resourceId: Int) {
    val name = context.resources.getResourceEntryName(resourceId)
    context.dataStore.edit { it[loadedMasterKey(table)] = name }
}
