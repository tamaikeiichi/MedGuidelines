package com.keiichi.medguidelines.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * トップ画面の並び順・お気に入りの保存形式。
 * 文字列リソースIDはアプリの更新で変わるため保存せず，画面の種類（ActionTypeの名前）で照合する。
 * 旧形式（ListItemDataをそのまま保存）も actionType と isFavorite だけを読み取って引き継ぐ。
 */
@Serializable
private data class SavedListItem(
    val actionType: String,
    val isFavorite: Boolean = false
)

private val savedListJson = Json { ignoreUnknownKeys = true }

suspend fun saveListItemData(context: Context, item: List<ListItemData>) {
    val savedList = item.map { SavedListItem(it.actionType.name, it.isFavorite) }
    context.dataStore.edit { settings ->
        settings[DataStoreKeys.LIST_ITEM_DATA_KEY] = savedListJson.encodeToString(savedList)
    }
}

/**
 * 保存された並び順・お気に入りを [initialItemsLists]（現在のバージョンの項目）に適用する。
 * 保存後に追加された項目は末尾に加え，削除された項目や表示対象外の項目は除く。
 */
fun loadListItemData(
    context: Context,
    initialItemsLists: List<ListItemData> = itemsList
): Flow<List<ListItemData>> {
    return context.dataStore.data.map { preferences ->
        val jsonString = preferences[DataStoreKeys.LIST_ITEM_DATA_KEY]
            ?: return@map initialItemsLists
        val savedList = try {
            savedListJson.decodeFromString<List<SavedListItem>>(jsonString)
        } catch (e: SerializationException) {
            return@map initialItemsLists
        } catch (e: IllegalArgumentException) {
            return@map initialItemsLists
        }
        applySavedOrder(savedList, initialItemsLists)
    }
}

private fun applySavedOrder(
    savedList: List<SavedListItem>,
    currentItems: List<ListItemData>
): List<ListItemData> {
    val currentByActionType = currentItems.associateBy { it.actionType.name }
    // 共有の itemsList の要素を書き換えないよう copy する
    val orderedItems = savedList
        .distinctBy { it.actionType }
        .mapNotNull { saved ->
            currentByActionType[saved.actionType]?.copy(isFavorite = saved.isFavorite)
        }
    val orderedActionTypes = orderedItems.map { it.actionType }.toSet()
    val newItems = currentItems.filter { it.actionType !in orderedActionTypes }
    return orderedItems + newItems
}
