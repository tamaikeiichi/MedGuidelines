package com.keiichi.medguidelines.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

// ICD-O-3.2とICD-O-4は同じテーブルに格納し、すべての問い合わせを版（edition）で絞り込む
@Dao
interface IcdO3Dao {
    @Query("""
        SELECT * FROM icd_o3_master
        WHERE edition = :edition
        AND (code LIKE :query OR normalizedTermEn LIKE :query OR normalizedTermJa LIKE :query)
        LIMIT 200
    """)
    fun search(edition: String, query: String): Flow<List<IcdO3Entity>>

    @Query("""
        SELECT * FROM icd_o3_master
        WHERE edition = :edition
        AND (code LIKE :w1 OR normalizedTermEn LIKE :w1 OR normalizedTermJa LIKE :w1)
        AND (code LIKE :w2 OR normalizedTermEn LIKE :w2 OR normalizedTermJa LIKE :w2)
        AND (code LIKE :w3 OR normalizedTermEn LIKE :w3 OR normalizedTermJa LIKE :w3)
        LIMIT 200
    """)
    fun searchMulti(edition: String, w1: String, w2: String, w3: String): Flow<List<IcdO3Entity>>

    // カードタップ時に、そのコードのカード1枚だけを表示するための完全一致検索
    @Query("SELECT * FROM icd_o3_master WHERE edition = :edition AND code = :code")
    fun searchByCode(edition: String, code: String): Flow<List<IcdO3Entity>>

    @Query("SELECT * FROM icd_o3_master WHERE edition = :edition AND code IN (:codes) AND level = 'Preferred'")
    suspend fun getPreferredByCodes(edition: String, codes: List<String>): List<IcdO3Entity>

    @Query("SELECT * FROM icd_o3_master WHERE edition = :edition AND code IN (:codes) AND level != 'Preferred' ORDER BY code, level")
    suspend fun getAlternateTermsByCodes(edition: String, codes: List<String>): List<IcdO3Entity>

    @Query("SELECT COUNT(*) FROM icd_o3_master WHERE edition = :edition")
    suspend fun getCount(edition: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<IcdO3Entity>)

    @Query("SELECT * FROM icd_o3_master WHERE edition = :edition AND isFavorite = 1")
    suspend fun getFavoriteRows(edition: String): List<IcdO3Entity>

    @Query("DELETE FROM icd_o3_master WHERE edition = :edition")
    suspend fun deleteAll(edition: String)

    /** マスタ更新時に、その版の旧データを破棄して入れ替える */
    @Transaction
    suspend fun replaceAll(edition: String, list: List<IcdO3Entity>) {
        deleteAll(edition)
        insertAll(list)
    }

    @Query("UPDATE icd_o3_master SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: Long, isFavorite: Boolean)

    @Query("SELECT * FROM icd_o3_master WHERE edition = :edition AND isFavorite = 1 AND level = 'Preferred' ORDER BY code")
    fun getFavorites(edition: String): Flow<List<IcdO3Entity>>
}
