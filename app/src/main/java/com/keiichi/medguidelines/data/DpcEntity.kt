package com.keiichi.medguidelines.data

import androidx.room.ColumnInfo
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

@Entity(tableName = "byotai_master") // テーブル名を "byotai_master" に設定
data class ByotaiEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, // 主キー
    @ColumnInfo(name = "mdc_code") // 1列目
    val mdcCode: String?,
    @ColumnInfo(name = "bunrui_code") // 2列目
    val bunruiCode: String?,
    @ColumnInfo(name = "byotai_code") // 3列目
    val byotaiCode: String?,
    // 4〜7列目は使わないので省略
    @ColumnInfo(name = "byotai_name") // 8列目
    val byotaiKubunMeisho: String?
)

@Entity(tableName = "bunrui_master") // テーブル名を "bunrui_master" に設定
data class BunruiEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "mdc_code") // 1列目
    val mdcCode: String?,
    @ColumnInfo(name = "bunrui_code") // 2列目分類コード
    val bunruiCode: String?,

    @ColumnInfo(name = "bunrui_name") // 3列目分類名称
    val bunruiName: String?
    // 他に必要な列があればここに追加
)
@Entity(tableName = "mdc_master") // テーブル名を "mdc_master" に設定
data class MdcEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "mdc_code") // MDCコード
    val mdcCode: String?,

    @ColumnInfo(name = "mdc_name") // MDC名称
    val mdcName: String?
)