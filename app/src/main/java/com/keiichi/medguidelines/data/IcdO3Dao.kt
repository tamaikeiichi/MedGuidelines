package com.keiichi.medguidelines.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface IcdO3Dao {
    @Query("""
        SELECT * FROM icd_o3_master
        WHERE (code LIKE :query OR normalizedTermEn LIKE :query OR normalizedTermJa LIKE :query)
        LIMIT 200
    """)
    fun search(query: String): Flow<List<IcdO3Entity>>

    @Query("""
        SELECT * FROM icd_o3_master
        WHERE (code LIKE :w1 OR normalizedTermEn LIKE :w1 OR normalizedTermJa LIKE :w1)
        AND (code LIKE :w2 OR normalizedTermEn LIKE :w2 OR normalizedTermJa LIKE :w2)
        AND (code LIKE :w3 OR normalizedTermEn LIKE :w3 OR normalizedTermJa LIKE :w3)
        LIMIT 200
    """)
    fun searchMulti(w1: String, w2: String, w3: String): Flow<List<IcdO3Entity>>

    @Query("SELECT * FROM icd_o3_master WHERE code IN (:codes) AND level = 'Preferred'")
    suspend fun getPreferredByCodes(codes: List<String>): List<IcdO3Entity>

    @Query("SELECT * FROM icd_o3_master WHERE code IN (:codes) AND level != 'Preferred' ORDER BY code, level")
    suspend fun getAlternateTermsByCodes(codes: List<String>): List<IcdO3Entity>

    @Query("SELECT COUNT(*) FROM icd_o3_master")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<IcdO3Entity>)

    @Query("UPDATE icd_o3_master SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: Long, isFavorite: Boolean)

    @Query("SELECT * FROM icd_o3_master WHERE isFavorite = 1 AND level = 'Preferred' ORDER BY code")
    fun getFavorites(): Flow<List<IcdO3Entity>>
}
