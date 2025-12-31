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
    val icdCode: String?,      // 4列目: ICD10コード,
    @ColumnInfo(name = "normalized_icd_name")
    val normalizedIcdName: String?
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

@Entity(tableName = "nenrei_master")
data class NenreiEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "mdc_code")
    val mdcCode: String?,
    @ColumnInfo(name = "bunrui_code")
    val bunruiCode: String?,
    @ColumnInfo(name = "nenrei_joken_kubun")
    val jokenKubun: String?,
    @ColumnInfo(name = "nenrei_joken_name")
    val jokenName: String?,

    @ColumnInfo(name = "nenrei_joken1_ijo")   // 修正: _ijo
    val joken1Ijo: String?,

    @ColumnInfo(name = "nenrei_joken1_miman") // 修正: _miman
    val joken1Miman: String?,

    @ColumnInfo(name = "nenrei_joken1_value")
    val joken1Value: String?,

    @ColumnInfo(name = "nenrei_joken2_ijo")   // 修正: _ijo
    val joken2Ijo: String?,
    @ColumnInfo(name = "nenrei_joken2_miman") // 修正: _miman
    val joken2Miman: String?,
    @ColumnInfo(name = "nenrei_joken2_value")
    val joken2Value: String?,

    @ColumnInfo(name = "nenrei_joken3_ijo")
    val joken3Ijo: String?,
    @ColumnInfo(name = "nenrei_joken3_miman")
    val joken3Miman: String?,
    @ColumnInfo(name = "nenrei_joken3_value")
    val joken3Value: String?,
    @ColumnInfo(name = "nenrei_joken4_ijo")
    val joken4Ijo: String?,
    @ColumnInfo(name = "nenrei_joken4_miman")
    val joken4Miman: String?,
    @ColumnInfo(name = "nenrei_joken4_value")
    val joken4Value: String?,

    @ColumnInfo(name = "nenrei_joken5_ijo")
    val joken5Ijo: String?,
    @ColumnInfo(name = "nenrei_joken5_miman") // 修正: _miman
    val joken5Miman: String?,
    @ColumnInfo(name = "nenrei_joken5_value") // 修正: _value
    val joken5Value: String?,

    @ColumnInfo(name = "nenrei_stroke_kubun")
    val strokeKubun: String?,
    @ColumnInfo(name = "nenrei_stroke_name")
    val strokeName: String?,
)

@Entity(tableName = "shujutsu_master")
data class ShujutsuEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "mdc_code")
    val mdcCode: String?,
    @ColumnInfo(name = "bunrui_code")
    val bunruiCode: String?,
    @ColumnInfo(name = "taiou_code")
    val taiouCode: String?,
    @ColumnInfo(name = "shujutsu1_name")
    val shujutsu1Name: String?,
    @ColumnInfo(name = "shujutsu2_name")
    val shujutsu2Name: String?,
)
