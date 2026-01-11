package com.keiichi.medguidelines.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.ColumnInfo

/**
 * nenrei_masterテーブルからjoken関連の値をまとめて取得するためのデータクラス
 */
data class NenreiJoken(
    @ColumnInfo(name = "nenrei_joken_name") val jokenName: String,
    @ColumnInfo(name = "joken1_ijo") val joken1Ijo: String?,
    @ColumnInfo(name = "joken1_miman") val joken1Miman: String?,
    @ColumnInfo(name = "joken1_value") val joken1Value: String?,
    @ColumnInfo(name = "joken2_ijo") val joken2Ijo: String?,
    @ColumnInfo(name = "joken2_miman") val joken2Miman: String?,
    @ColumnInfo(name = "joken2_value") val joken2Value: String?,
    @ColumnInfo(name = "joken3_ijo") val joken3Ijo: String?,
    @ColumnInfo(name = "joken3_miman") val joken3Miman: String?,
    @ColumnInfo(name = "joken3_value") val joken3Value: String?,
    @ColumnInfo(name = "joken4_ijo") val joken4Ijo: String?,
    @ColumnInfo(name = "joken4_miman") val joken4Miman: String?,
    @ColumnInfo(name = "joken4_value") val joken4Value: String?,
    @ColumnInfo(name = "joken5_ijo") val joken5Ijo: String?,
    @ColumnInfo(name = "joken5_miman") val joken5Miman: String?,
    @ColumnInfo(name = "joken5_value") val joken5Value: String?
)

// data/dao/NenreiDao.kt

@Dao
interface NenreiDao {
// ... insertAll, getCount, checkBunruiExists... のメソッドはそのまま
//
//    // --- ここからが修正箇所 ---
//
    /**
    * 指定されたMDCコードと分類コードに一致する行から、
    * joken1〜5に関連するすべての値を一度に取得します。
    * @return NenreiJokenオブジェクト。見つからなければnull。
    */
    @Query("SELECT nenrei_joken_name," +
            "joken1_ijo, joken1_miman, joken1_value, " +
            "joken2_ijo, joken2_miman, joken2_value, " +
            "joken3_ijo, joken3_miman, joken3_value, "+
            "joken4_ijo, joken4_miman, joken4_value, " +
            "joken5_ijo, joken5_miman, joken5_value " +
            "FROM nenrei_master " +
            "WHERE mdc_code = :mdcCode AND bunrui_code = :bunruiCode " +
            "LIMIT 1"    )
    suspend fun getNenreiJoken(mdcCode: String, bunruiCode: String): NenreiJoken?


}







