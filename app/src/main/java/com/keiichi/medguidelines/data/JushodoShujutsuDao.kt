package com.keiichi.medguidelines.data

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Query

data class JushodoShujutsuJoken(
    // データベースの列名と一致するように@ColumnInfoを使うのが確実
    @ColumnInfo(name = "joken_name") val jokenName: String,
    @ColumnInfo(name = "mdc_code") val mdcCode: String,
    @ColumnInfo(name = "bunrui_code") val bunruiCode: String,
    @ColumnInfo(name = "joken1_name") val joken1Name: String,
    @ColumnInfo(name = "joken1_code") val joken1Code: String,
    @ColumnInfo(name = "joken2_name") val joken2Name: String,
    @ColumnInfo(name = "joken2_code") val joken2Code: String
)

@Dao
interface JushodoShujutsuDao {
    /**
     * Shujutsuマスターテーブルの件数を取得します。
     */
    @Query("SELECT COUNT(*) FROM jushodo_shujutsu_master")
    suspend fun getCount(): Int

    @Query("SELECT joken_name FROM jushodo_shujutsu_master WHERE mdc_code = :mdcCode AND bunrui_code = :bunruiCode")
    suspend fun getNames(mdcCode: String, bunruiCode: String): List<String>

    @androidx.room.Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun insertAlldata(list: List<JushodoShujutsuEntity>)

    @Query("SELECT EXISTS(SELECT 1 FROM jushodo_shujutsu_master WHERE bunrui_code = :bunruiCode LIMIT 1)")
    suspend fun existsBunruiInMaster(bunruiCode: String): Boolean

    /**
     * 指定されたMDCコードと分類コードに一致する行から、
     * joken1とjoken2に関連するすべての値を一度に取得します。
     *      * @return JushodoJokenオブジェクト。見つからなければnull。
     *      */
    @Query(
        "SELECT joken_name, mdc_code, bunrui_code, joken1_name, joken1_code, joken2_code, joken2_name " +
                "FROM jushodo_shujutsu_master " +
                "WHERE mdc_code = :mdcCode AND bunrui_code = :bunruiCode " +
                "LIMIT 1"
    )
    suspend fun getJushodoJoken(mdcCode: String, bunruiCode: String): List<JushodoShujutsuJoken>
}


