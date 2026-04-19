package com.keiichi.medguidelines.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shobyomei_master")
data class ShobyomeiEntity(
    @PrimaryKey val code: String,        // 傷病名コード
    val name: String,                    // 漢字名称
    val kanaName: String,               // カナ名称
    val icd_10_1: String,                  // ICD10_1
    val icd_10_2: String = "",           // ICD10_2
    val normalizedName: String = "",    // 検索用（正規化済み名称）
    val normalizedKanaName: String = "", // 検索用（正規化済みカナ）
    val isFavorite: Boolean = false      // お気に入りフラグ
)
