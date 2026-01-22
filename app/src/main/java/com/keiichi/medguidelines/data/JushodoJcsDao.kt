package com.keiichi.medguidelines.data

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Query

data class JushodoJcsJoken(
    // データベースの列名と一致するように@ColumnInfoを使うのが確実
    @ColumnInfo(name = "joken_name") val jokenName: String,
    @ColumnInfo(name = "joken1_ijo") val joken1Ijo: String,
    @ColumnInfo(name = "joken1_miman") val joken1Miman: String,
    @ColumnInfo(name = "joken1_value") val joken1Value: String,
    @ColumnInfo(name = "joken2_ijo") val joken2Ijo: String,
    @ColumnInfo(name = "joken2_miman") val joken2Miman: String,
    @ColumnInfo(name = "joken2_value") val joken2Value: String
)

@Dao
interface JushodoJcsDao {
    /**
     * Shujutsuマスターテーブルの件数を取得します。
     */
    @Query("SELECT COUNT(*) FROM jushodo_jcs_master")
    suspend fun getCount(): Int

    @Query("SELECT joken_name FROM jushodo_jcs_master WHERE mdc_code = :mdcCode AND bunrui_code = :bunruiCode")
    suspend fun getNames(mdcCode: String, bunruiCode: String): List<String>

    @androidx.room.Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun insertAlldata(list: List<JushodoJcsEntity>)

    @Query("SELECT EXISTS(SELECT 1 FROM jushodo_jcs_master WHERE mdc_code = :mdcCode AND bunrui_code = :bunruiCode LIMIT 1)")
    suspend fun existsMdcAndBunruiInMaster(mdcCode: String, bunruiCode: String): Boolean

    /**
     * 指定されたMDCコードと分類コードに一致する行から、
     * joken1とjoken2に関連するすべての値を一度に取得します。
     *      * @return JushodoJokenオブジェクト。見つからなければnull。
     *      */
    @Query(
        "SELECT joken_name, joken1_ijo, joken1_miman, joken1_value, joken2_ijo, joken2_miman, joken2_value " +
                "FROM jushodo_jcs_master " +
                "WHERE mdc_code = :mdcCode AND bunrui_code = :bunruiCode " +
                "LIMIT 1"
    )
    suspend fun getJushodoJoken(mdcCode: String, bunruiCode: String): List<JushodoJcsJoken>
}


