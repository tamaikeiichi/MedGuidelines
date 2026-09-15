package com.keiichi.medguidelines.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface IcdO3TopographyDao {
    @Query("""
        SELECT * FROM icd_o3_topography
        WHERE (code LIKE :query OR normalizedTermEn LIKE :query OR normalizedTermJa LIKE :query)
        LIMIT 200
    """)
    fun search(query: String): Flow<List<IcdO3TopographyEntity>>

    @Query("""
        SELECT * FROM icd_o3_topography
        WHERE (code LIKE :w1 OR normalizedTermEn LIKE :w1 OR normalizedTermJa LIKE :w1)
        AND (code LIKE :w2 OR normalizedTermEn LIKE :w2 OR normalizedTermJa LIKE :w2)
        AND (code LIKE :w3 OR normalizedTermEn LIKE :w3 OR normalizedTermJa LIKE :w3)
        LIMIT 200
    """)
    fun searchMulti(w1: String, w2: String, w3: String): Flow<List<IcdO3TopographyEntity>>

    @Query("SELECT * FROM icd_o3_topography WHERE code IN (:codes) AND termType = '優先用語'")
    suspend fun getPreferredByCodes(codes: List<String>): List<IcdO3TopographyEntity>

    @Query("SELECT * FROM icd_o3_topography WHERE code IN (:codes) AND termType != '優先用語' ORDER BY code")
    suspend fun getAlternateTermsByCodes(codes: List<String>): List<IcdO3TopographyEntity>

    @Query("SELECT COUNT(*) FROM icd_o3_topography")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<IcdO3TopographyEntity>)

    @Query("UPDATE icd_o3_topography SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: Long, isFavorite: Boolean)

    @Query("SELECT * FROM icd_o3_topography WHERE isFavorite = 1 AND termType = '優先用語' ORDER BY code")
    fun getFavorites(): Flow<List<IcdO3TopographyEntity>>
}
