package com.keiichi.medguidelines.data

import androidx.room.Dao
import androidx.room.Query

@Dao
interface FukushobyoDao {
    /**
     * Shujutsuマスターテーブルの件数を取得します。
     */
    @androidx.room.Query("SELECT COUNT(*) FROM fukushobyo_master")
    suspend fun getCount(): Int

    @androidx.room.Query("SELECT name FROM fukushobyo_master WHERE mdc_code = :mdcCode AND bunrui_code = :bunruiCode")
    suspend fun getNames(mdcCode: String, bunruiCode: String): List<String>

    @androidx.room.Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun insertAlldata(list: List<FukushobyoEntity>)

    @Query("SELECT EXISTS(SELECT 1 FROM fukushobyo_master WHERE bunrui_code = :bunruiCode LIMIT 1)")
    suspend fun existsBunruiInMaster(bunruiCode: String): Boolean

    @androidx.room.Query("SELECT taiou_code FROM fukushobyo_master WHERE name = :name LIMIT 1")
    suspend fun getCodeByName(name: String): String?

}