package com.keiichi.medguidelines.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "icd_o3_topography",
    indices = [
        Index(value = ["code"]),
        Index(value = ["normalizedTermEn"]),
        Index(value = ["normalizedTermJa"])
    ]
)
data class IcdO3TopographyEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val code: String,                      // ICD-O-3.2局在コード (例: C00.0)
    val level: String,                     // category / specific
    val termType: String,                  // 優先用語 / 同義語
    val termEn: String,                    // 英語名称
    val termJa: String = "",               // 日本語名称
    val needsReview: Boolean = false,
    val normalizedTermEn: String = "",     // 検索用（正規化済み英語名称）
    val normalizedTermJa: String = "",     // 検索用（正規化済み日本語名称）
    val isFavorite: Boolean = false
)
