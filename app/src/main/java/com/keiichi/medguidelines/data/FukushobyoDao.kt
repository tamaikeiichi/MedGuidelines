package com.keiichi.medguidelines.data

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Query

data class FukushobyoJoken(
    // データベースの列名と一致するように@ColumnInfoを使うのが確実
    //@ColumnInfo(name = "mdc_code") val mdcCode: String,
    //@ColumnInfo(name = "bunrui_code") val bunruiCode: String,
    @ColumnInfo(name = "code") val code: String,
    @ColumnInfo(name = "name") val name: String,
)
@Dao
interface FukushobyoDao {
    /**
     * Shujutsuマスターテーブルの件数を取得します。
     */
    @androidx.room.Query("SELECT COUNT(*) FROM fukushobyo_master")
    suspend fun getCount(): Int

    @androidx.room.Query("SELECT code, name FROM fukushobyo_master WHERE mdc_code = :mdcCode AND bunrui_code = :bunruiCode")
    suspend fun getNames(mdcCode: String, bunruiCode: String): List<FukushobyoJoken>

    @androidx.room.Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun insertAlldata(list: List<FukushobyoEntity>)

    @Query("SELECT EXISTS(SELECT 1 FROM fukushobyo_master WHERE mdc_code = :mdcCode AND bunrui_code = :bunruiCode LIMIT 1)")
    suspend fun existsMdcAndBunruiInMaster(mdcCode: String, bunruiCode: String): Boolean

    @androidx.room.Query("SELECT code FROM fukushobyo_master WHERE name = :name LIMIT 1")
    suspend fun getCodeByName(name: String): String?

}