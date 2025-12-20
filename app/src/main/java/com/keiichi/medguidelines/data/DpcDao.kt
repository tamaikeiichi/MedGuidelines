package com.keiichi.medguidelines.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DpcDao {
    // --- ICDマスターテーブル (icd_master) に対する操作 ---

    /**
     * ICDマスターテーブルに複数のデータを一括で挿入します。
     * 既にあるデータは上書きします。
     * @param icdList 挿入したいIcdEntityのリスト
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllIcd(icdList: List<IcdEntity>)

    /**
     * ICDマスターテーブルの全件数を取得します。
     * データが投入済みかどうかのチェックに使用します。
     * @return テーブルの行数
     */
    @Query("SELECT COUNT(*) FROM icd_master")
    suspend fun getIcdCount(): Int

    /**
     * 名称(icdNumber)またはICDコード(icdCode)で部分一致検索を行います。
     * 検索結果はFlowでラップされているため、データ変更が自動的にUIに通知されます。
     * @param query 検索文字列 (例: "%検索語%")
     * @return 条件に一致したIcdEntityのリストをFlowで返す
     */
    @Query("SELECT * FROM icd_master WHERE icdName LIKE :query OR icdCode LIKE :query")
    fun searchIcd(query: String): Flow<List<IcdEntity>>


    // --- 病態マスターテーブル (byotai_master) に対する操作 ---


    // --- 必要に応じて、他の11個のテーブルに対する操作もここに追加していきます ---

}