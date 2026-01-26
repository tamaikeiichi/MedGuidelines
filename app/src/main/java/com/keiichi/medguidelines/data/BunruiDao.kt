package com.keiichi.medguidelines.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BunruiDao {
    @androidx.room.Query("SELECT COUNT(*) FROM bunrui_master")
    suspend fun getCount(): Int

    @androidx.room.Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun insertAll(bunruiList: List<BunruiEntity>)

    @Query("""
        SELECT * FROM bunrui_master 
        WHERE (normalized_bunrui_name LIKE :word1 OR mdc_Code LIKE :word1 OR bunrui_Code LIKE :word1 LIKE :word1)
        AND (normalized_bunrui_name LIKE :word2 OR mdc_Code LIKE :word2 OR bunrui_Code LIKE :word2 LIKE :word2)
        AND (normalized_bunrui_name LIKE :word3 OR mdc_Code LIKE :word3 OR bunrui_Code LIKE :word3 LIKE :word3)
        AND (normalized_bunrui_name LIKE :word4 OR mdc_Code LIKE :word4 OR bunrui_Code LIKE :word4 LIKE :word4)
    """)
    fun searchBunruiMulti(
        word1: String,
        word2: String = "%%",
        word3: String = "%%",
        word4: String = "%%"
    ): Flow<List<BunruiEntity>>
}