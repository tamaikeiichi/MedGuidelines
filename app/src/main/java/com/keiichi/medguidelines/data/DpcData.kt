package com.keiichi.medguidelines.data

import androidx.constraintlayout.helper.widget.Flow
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase

// Create a new file, e.g., DpcData.kt
@Entity(tableName = "dpc_data")
data class DpcEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val column1: String, // Rename these to match your actual Excel columns
    val column2: String,
    val column3: Double? // Use appropriate data types
)

@Dao
interface DpcDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entries: List<DpcEntry>)

    @Query("SELECT * FROM dpc_data")
    fun getAll(): Flow<List<DpcEntry>> // Use Flow to get real-time updates

    @Query("SELECT COUNT(*) FROM dpc_data")
    suspend fun getCount(): Int
}

@Database(entities = [DpcEntry::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dpcDao(): DpcDao
}
