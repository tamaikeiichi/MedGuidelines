package com.keiichi.medguidelines.data

import androidx.room.Dao
import androidx.room.Query

@Dao
interface ShujutsuDao {
    /**
     * Shujutsuマスターテーブルの件数を取得します。
     */
    @androidx.room.Query("SELECT COUNT(*) FROM shujutsu_master")
    suspend fun getShujutsuCount(): Int

    @androidx.room.Query("SELECT shujutsu1_name FROM shujutsu_master WHERE mdc_code = :mdcCode AND bunrui_code = :bunruiCode")
    suspend fun getNames(mdcCode: String, bunruiCode: String): List<String>

    @androidx.room.Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun insertAllShujutsu(shujutsuList: List<ShujutsuEntity>)

    @Query("SELECT EXISTS(SELECT 1 FROM shujutsu_master WHERE bunrui_code = :bunruiCode LIMIT 1)")
    suspend fun existsBunruiInShujutsuMaster(bunruiCode: String): Boolean

    @androidx.room.Query("SELECT taiou_code FROM shujutsu_master WHERE shujutsu1_name = :shujutsu1Name LIMIT 1")
    suspend fun getShujutsu1CodeByName(shujutsu1Name: String): String?
}