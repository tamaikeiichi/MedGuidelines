package com.keiichi.medguidelines.data

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Query

data class Shochi1Joken(
    // データベースの列名と一致するように@ColumnInfoを使うのが確実
    //@ColumnInfo(name = "mdc_code") val mdcCode: String,
    //@ColumnInfo(name = "bunrui_code") val bunruiCode: String,
    @ColumnInfo(name = "code") val code: String,
    @ColumnInfo(name = "shochi1_name") val shochi1Name: String,
)

@Dao
interface Shochi1Dao {
    /**
     * Shujutsuマスターテーブルの件数を取得します。
     */
    @androidx.room.Query("SELECT COUNT(*) FROM shochi1_master")
    suspend fun getCount(): Int

    @androidx.room.Query("SELECT code, shochi1_name FROM shochi1_master WHERE mdc_code = :mdcCode AND bunrui_code = :bunruiCode")
    suspend fun getNames(mdcCode: String, bunruiCode: String): List<Shochi1Joken>

    @androidx.room.Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun insertAlldata(shochi1List: List<Shochi1Entity>)

    @Query("SELECT EXISTS(SELECT 1 FROM shochi1_master WHERE mdc_code = :mdcCode AND bunrui_code = :bunruiCode LIMIT 1)")
    suspend fun existsMdcAndBunruiInMaster(mdcCode: String, bunruiCode: String): Boolean

    @androidx.room.Query("SELECT shochi1_name FROM shochi1_master WHERE shochi1_name = :shochi1Name LIMIT 1")
    suspend fun getCodeByName(shochi1Name: String): String?

}