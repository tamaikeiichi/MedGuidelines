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

    @ColumnInfo(name = "joken1_ijo")   // 修正: _ijo
    val joken1Ijo: String?,

    @ColumnInfo(name = "joken1_miman") // 修正: _miman
    val joken1Miman: String?,

    @ColumnInfo(name = "joken1_value")
    val joken1Value: String?,

    @ColumnInfo(name = "joken2_ijo")   // 修正: _ijo
    val joken2Ijo: String?,
    @ColumnInfo(name = "joken2_miman") // 修正: _miman
    val joken2Miman: String?,
    @ColumnInfo(name = "joken2_value")
    val joken2Value: String?,

    @ColumnInfo(name = "joken3_ijo")
    val joken3Ijo: String?,
    @ColumnInfo(name = "joken3_miman")
    val joken3Miman: String?,
    @ColumnInfo(name = "joken3_value")
    val joken3Value: String?,
    @ColumnInfo(name = "joken4_ijo")
    val joken4Ijo: String?,
    @ColumnInfo(name = "joken4_miman")
    val joken4Miman: String?,
    @ColumnInfo(name = "joken4_value")
    val joken4Value: String?,

    @ColumnInfo(name = "joken5_ijo")
    val joken5Ijo: String?,
    @ColumnInfo(name = "joken5_miman") // 修正: _miman
    val joken5Miman: String?,
    @ColumnInfo(name = "joken5_value") // 修正: _value
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

@Entity(tableName = "shochi1_master")
data class Shochi1Entity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "mdc_code")
    val mdcCode: String?,
    @ColumnInfo(name = "bunrui_code")
    val bunruiCode: String?,
    @ColumnInfo(name = "code")
    val code: String?,
    @ColumnInfo(name = "shochi1_name")
    val shochi1Name: String?,
    @ColumnInfo(name = "shochi1_code")
    val shochi1Code: String?,
    @ColumnInfo(name = "shochi2_name")
    val shochi2Name: String?,
    @ColumnInfo(name = "shochi2_code")
    val shochi2Code: String?,
)

@Entity(tableName = "shochi2_master")
data class Shochi2Entity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "mdc_code")
    val mdcCode: String?,
    @ColumnInfo(name = "bunrui_code")
    val bunruiCode: String?,
    @ColumnInfo(name = "code")
    val code: String?,
    @ColumnInfo(name = "shochi1_name")
    val shochi1Name: String?,
    @ColumnInfo(name = "shochi1_code")
    val shochi1Code: String?,
    @ColumnInfo(name = "shochi2_name")
    val shochi2Name: String?,
    @ColumnInfo(name = "shochi2_code")
    val shochi2Code: String?,
)

@Entity(tableName = "fukushobyo_master")
data class FukushobyoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "mdc_code")
    val mdcCode: String?,
    @ColumnInfo(name = "bunrui_code")
    val bunruiCode: String?,
    @ColumnInfo(name = "code")
    val code: String?,
    @ColumnInfo(name = "name")
    val name: String?,
    @ColumnInfo(name = "IcdCode")
    val IcdCode: String?,
)

@Entity(tableName = "jushodo_jcs_master")
data class JushodoJcsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "mdc_code")
    val mdcCode: String?,
    @ColumnInfo(name = "bunrui_code")
    val bunruiCode: String?,
    @ColumnInfo(name = "joken_name")
    val jokenName: String?,
    @ColumnInfo(name = "joken1_ijo")
    val joken1Ijo: String?,
    @ColumnInfo(name = "joken1_miman")
    val joken1Miman: String?,
    @ColumnInfo(name = "joken1_value")
    val joken1Value: String?,
    @ColumnInfo(name = "joken2_ijo")
    val joken2Ijo: String?,
    @ColumnInfo(name = "joken2_miman")
    val joken2Miman: String?,
    @ColumnInfo(name = "joken2_value")
    val joken2Value: String?,
)

@Entity(tableName = "jushodo_shujutsu_master")
data class JushodoShujutsuEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "mdc_code")
    val mdcCode: String?,
    @ColumnInfo(name = "bunrui_code")
    val bunruiCode: String?,
    @ColumnInfo(name = "joken_name")
    val jokenName: String?,
    @ColumnInfo(name = "joken1_name")
    val joken1Name: String?,
    @ColumnInfo(name = "joken1_code")
    val joken1Code: String?,
    @ColumnInfo(name = "joken2_name")
    val joken2Name: String?,
    @ColumnInfo(name = "joken2_code")
    val joken2Code: String?,
)

@Entity(tableName = "jushodo_stroke_master")
data class JushodoStrokeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "mdc_code")
    val mdcCode: String?,
    @ColumnInfo(name = "bunrui_code")
    val bunruiCode: String?,
    @ColumnInfo(name = "code")
    val code: String?,
    @ColumnInfo(name = "label")
    val label: String?,
    @ColumnInfo(name = "joken1_name")
    val joken1Name: String?,
)

@Entity(tableName = "shindangunBunruiTensuhyo_master")
data class ShindangunBunruiTensuhyoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "code")
    val code: String?,
    @ColumnInfo(name = "name")
    val name: String?,
    @ColumnInfo(name = "nyuinBiI")
    val nyuinBiI: String?,
    @ColumnInfo(name = "nyuinBiII")
    val nyuinBiII: String?,
    @ColumnInfo(name = "nyuinBiIII")
    val nyuinBiIII: String?,
    @ColumnInfo(name = "nyuinKikanI")
    val nyuinKikanI: String?,
    @ColumnInfo(name = "nyuinKikanII")
    val nyuinKikanII: String?,
    @ColumnInfo(name = "nyuinKikanIII")
    val nyuinKikanIII: String?,
)

