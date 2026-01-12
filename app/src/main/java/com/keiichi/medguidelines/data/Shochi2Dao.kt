package com.keiichi.medguidelines.data


import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Query

data class Shochi2Joken(
    // データベースの列名と一致するように@ColumnInfoを使うのが確実
    //@ColumnInfo(name = "mdc_code") val mdcCode: String,
    //@ColumnInfo(name = "bunrui_code") val bunruiCode: String,
    @ColumnInfo(name = "code") val code: String,
    @ColumnInfo(name = "shochi1_name") val shochi1Name: String,
)

@Dao
interface Shochi2Dao {
    /**
     * Shujutsuマスターテーブルの件数を取得します。
     */
    @androidx.room.Query("SELECT COUNT(*) FROM shochi2_master")
    suspend fun getCount(): Int

    @androidx.room.Query("SELECT code, shochi1_name FROM shochi2_master WHERE mdc_code = :mdcCode AND bunrui_code = :bunruiCode")
    suspend fun getNames(mdcCode: String, bunruiCode: String): List<Shochi2Joken>

    @androidx.room.Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun insertAlldata(list: List<Shochi2Entity>)

    @Query("SELECT EXISTS(SELECT 1 FROM shochi2_master WHERE bunrui_code = :bunruiCode LIMIT 1)")
    suspend fun existsBunruiInMaster(bunruiCode: String): Boolean

    @androidx.room.Query("SELECT code FROM shochi1_master WHERE shochi2_name = :name LIMIT 1")
    suspend fun getCodeByName(name: String): String?

}