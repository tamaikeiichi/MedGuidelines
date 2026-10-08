package com.keiichi.medguidelines.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shika_shinryokoi_master")
data class ShikaShinryokoiEntity(
    @PrimaryKey val id: Int,               // CSVの行番号（PairedTextItem.originalIndex）
    val kanjiMeisho: String?,              // 漢字名称
    val kanaMeisho: String?,               // カナ名称
    val tensuShikibetsu: String,           // 点数識別
    val tensu: String,                     // 点数
    val normalizedKanji: String = "",      // 検索用（正規化済み漢字名称）
    val normalizedKana: String = "",       // 検索用（正規化済みカナ名称）
    val isFavorite: Boolean = false,
    val sortOrder: Long = 0                // 表示順（小さいほど上。お気に入り操作で移動する）
)
