package com.keiichi.medguidelines.data

import androidx.room.Dao
import androidx.room.Query

@Dao
interface Shochi1Dao {
    /**
     * Shujutsuマスターテーブルの件数を取得します。
     */
    @androidx.room.Query("SELECT COUNT(*) FROM shochi1_master")
    suspend fun getCount(): Int

    @androidx.room.Query("SELECT shochi1_name FROM shochi1_master WHERE mdc_code = :mdcCode AND bunrui_code = :bunruiCode")
    suspend fun getNames(mdcCode: String, bunruiCode: String): List<String>

    @androidx.room.Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun insertAlldata(shochi1List: List<Shochi1Entity>)

    @Query("SELECT EXISTS(SELECT 1 FROM shochi1_master WHERE bunrui_code = :bunruiCode LIMIT 1)")
    suspend fun existsBunruiInMaster(bunruiCode: String): Boolean

    @androidx.room.Query("SELECT taiou_code FROM shochi1_master WHERE shochi1_name = :shochi1Name LIMIT 1")
    suspend fun getCodeByName(shochi1Name: String): String?

}