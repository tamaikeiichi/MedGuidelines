package com.keiichi.medguidelines.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "icd_o3_master",
    indices = [
        Index(value = ["code"]),
        Index(value = ["normalizedTermEn"]),
        Index(value = ["normalizedTermJa"])
    ]
)
data class IcdO3Entity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val code: String,                      // ICD-O-3.2コード (例: 8000/0)
    val level: String,                     // Preferred / Synonym / Related / 見出し行(1,2,3)
    val termEn: String,                    // 英語名称
    val termJa: String = "",               // 日本語名称
    val isObsolete: Boolean = false,
    val normalizedTermEn: String = "",     // 検索用（正規化済み英語名称）
    val normalizedTermJa: String = "",     // 検索用（正規化済み日本語名称）
    val isFavorite: Boolean = false
)
