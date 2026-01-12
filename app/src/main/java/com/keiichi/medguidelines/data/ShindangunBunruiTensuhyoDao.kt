package com.keiichi.medguidelines.data

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Query

data class ShindangunBunruiTensuhyoJoken(
    @ColumnInfo(name = "code") val code: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "nyuinBiI") val nyuinbiI: String,
    @ColumnInfo(name = "nyuinBiII") val nyuinbiII: String,
    @ColumnInfo(name = "nyuinBiIII") val nyuinbiIII: String,
    @ColumnInfo(name = "nyuinKikanI") val nyuinKikanI: String,
    @ColumnInfo(name = "nyuinKikanII") val nyuinKikanII: String,
    @ColumnInfo(name = "nyuinKikanIII") val nyuinKikanIII: String,
)

@Dao
interface ShindangunBunruiTensuhyoDao {
    /**
     * Shujutsuマスターテーブルの件数を取得します。
     */
    @androidx.room.Query("SELECT COUNT(*) FROM shindangunBunruiTensuhyo_master")
    suspend fun getCount(): Int

    @androidx.room.Query("SELECT code, name, nyuinBiI, nyuinBiII, nyuinBiIII, nyuinKikanI, nyuinKikanII, nyuinKikanIII" +
            " FROM shindangunBunruiTensuhyo_master WHERE code LIKE :code")
    suspend fun getNames(code: String): List<ShindangunBunruiTensuhyoJoken>

    @androidx.room.Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun insertAlldata(list: List<ShindangunBunruiTensuhyoEntity>)

    @Query("SELECT EXISTS(SELECT code FROM shindangunBunruiTensuhyo_master WHERE code = :code LIMIT 1)")
    suspend fun existsCodeInMaster(code: String): Boolean

    @androidx.room.Query("SELECT code FROM shindangunBunruiTensuhyo_master WHERE name = :name LIMIT 1")
    suspend fun getCodeByName(name: String): String?

}