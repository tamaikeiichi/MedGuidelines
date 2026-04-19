package com.keiichi.medguidelines.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ShobyomeiDao {
    @Query("""
        SELECT * FROM shobyomei_master 
        WHERE (name LIKE :query OR normalizedName LIKE :query OR normalizedKanaName LIKE :query OR code LIKE :query OR icd_10_1 LIKE :query OR icd_10_2 LIKE :query) 
        LIMIT 100
    """)
    fun search(query: String): Flow<List<ShobyomeiEntity>>

    @Query("""
        SELECT * FROM shobyomei_master 
        WHERE (name LIKE :w1 OR normalizedName LIKE :w1 OR normalizedKanaName LIKE :w1 OR code LIKE :w1 OR icd_10_1 LIKE :w1 OR icd_10_2 LIKE :w1)
        AND (name LIKE :w2 OR normalizedName LIKE :w2 OR normalizedKanaName LIKE :w2 OR code LIKE :w2 OR icd_10_1 LIKE :w2 OR icd_10_2 LIKE :w2)
        AND (name LIKE :w3 OR normalizedName LIKE :w3 OR normalizedKanaName LIKE :w3 OR code LIKE :w3 OR icd_10_1 LIKE :w3 OR icd_10_2 LIKE :w3)
        LIMIT 100
    """)
    fun searchMulti(w1: String, w2: String, w3: String): Flow<List<ShobyomeiEntity>>

    @Query("SELECT COUNT(*) FROM shobyomei_master")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<ShobyomeiEntity>)

    @Query("SELECT name FROM shobyomei_master LIMIT 20")
    suspend fun getDebugNames(): List<String>

    @Query("UPDATE shobyomei_master SET isFavorite = :isFavorite WHERE code = :code")
    suspend fun updateFavorite(code: String, isFavorite: Boolean)

    @Query("SELECT * FROM shobyomei_master WHERE isFavorite = 1")
    fun getFavorites(): Flow<List<ShobyomeiEntity>>
}
