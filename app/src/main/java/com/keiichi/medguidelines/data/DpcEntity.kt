package com.keiichi.medguidelines.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "icd_master") // テーブル名を "icd_master" に設定
data class IcdEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, // 主キーを自動生成

    val mdcCode: String?,       // 1列目: MDCコード
    val bunruiCode: String?,   // 2列目: 分類番号
    val icdName: String?,          // 3列目: 名称
    val icdCode: String?      // 4列目: ICD10コード
)


