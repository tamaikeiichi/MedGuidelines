package com.keiichi.medguidelines.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Transaction
import androidx.sqlite.db.SupportSQLiteQuery
import kotlinx.coroutines.flow.Flow

@Dao
interface ShikaShinryokoiDao {
    @Query("SELECT * FROM shika_shinryokoi_master ORDER BY isFavorite DESC, sortOrder ASC")
    fun getAll(): Flow<List<ShikaShinryokoiEntity>>

    // 検索ワード数が可変のため、WHERE句はリポジトリ側で組み立てる
    @RawQuery(observedEntities = [ShikaShinryokoiEntity::class])
    fun search(query: SupportSQLiteQuery): Flow<List<ShikaShinryokoiEntity>>

    @Query("SELECT COUNT(*) FROM shika_shinryokoi_master")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<ShikaShinryokoiEntity>)

    @Query("SELECT * FROM shika_shinryokoi_master WHERE isFavorite = 1")
    suspend fun getFavorites(): List<ShikaShinryokoiEntity>

    @Query("DELETE FROM shika_shinryokoi_master")
    suspend fun deleteAll()

    /** マスタ更新時に旧データを破棄して入れ替える */
    @Transaction
    suspend fun replaceAll(list: List<ShikaShinryokoiEntity>) {
        deleteAll()
        insertAll(list)
    }

    @Query("UPDATE shika_shinryokoi_master SET isFavorite = :isFavorite, sortOrder = :sortOrder WHERE id = :id")
    suspend fun updateFavoriteAndOrder(id: Int, isFavorite: Boolean, sortOrder: Long)

    @Query("SELECT COALESCE(MIN(sortOrder), 0) FROM shika_shinryokoi_master")
    suspend fun getMinSortOrder(): Long

    @Query("SELECT MIN(sortOrder) FROM shika_shinryokoi_master WHERE isFavorite = 0")
    suspend fun getMinNonFavoriteSortOrder(): Long?

    @Query("SELECT COALESCE(MAX(sortOrder), 0) FROM shika_shinryokoi_master")
    suspend fun getMaxSortOrder(): Long

    /**
     * お気に入りの切り替え。
     * 登録時はリスト全体の先頭へ、解除時はお気に入り以外の先頭（お気に入りの直下）へ移動する。
     */
    @Transaction
    suspend fun setFavorite(id: Int, isFavorite: Boolean) {
        val newOrder = if (isFavorite) {
            getMinSortOrder() - 1
        } else {
            getMinNonFavoriteSortOrder()?.minus(1) ?: (getMaxSortOrder() + 1)
        }
        updateFavoriteAndOrder(id, isFavorite, newOrder)
    }
}
