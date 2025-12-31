package com.keiichi.medguidelines.data

import androidx.room.Dao

@Dao
interface ShujutsuDao {
    /**
     * Shujutsuマスターテーブルの件数を取得します。
     */
    @androidx.room.Query("SELECT COUNT(*) FROM shujutsu_master")
    suspend fun getShujutsuCount(): Int
}