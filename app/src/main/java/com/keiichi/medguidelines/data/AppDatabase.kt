// C:/Users/tamaikeiichi/StudioProjects/MedGuidelines/app/src/main/java/com/keiichi/medguidelines/data/AppDatabase.kt

package com.keiichi.medguidelines.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * アプリケーションのRoomデータベース本体。
 * このクラスは、データベースのインスタンスを生成し、DAOへのアクセスを提供します。
 */
@Database(
    entities = [
        IcdEntity::class,
        ByotaiEntity::class,
        BunruiEntity::class,
        MdcEntity::class,
        NenreiEntity::class,
        ShujutsuEntity::class,
        Shochi1Entity::class,
        Shochi2Entity::class,
        FukushobyoEntity::class,
        JushodoJcsEntity::class,
        JushodoShujutsuEntity::class,
        JushodoStrokeEntity::class,
        ShindangunBunruiTensuhyoEntity::class,
        ShobyomeiEntity::class,
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun dpcDao(): DpcDao
    abstract fun bunruiDao(): BunruiDao
    abstract fun shujutsuDao(): ShujutsuDao
    abstract fun shochi1Dao(): Shochi1Dao
    abstract fun shochi2Dao(): Shochi2Dao
    abstract fun fukushobyoDao(): FukushobyoDao
    abstract fun jushodoJcsDao(): JushodoJcsDao
    abstract fun nenreiDao(): NenreiDao
    abstract fun jushodoShujutsuDao(): JushodoShujutsuDao
    abstract fun jushodoStrokeDao(): JushodoStrokeDao
    abstract fun shindangunBunruiTensuhyoDao(): ShindangunBunruiTensuhyoDao
    abstract fun shobyomeiDao(): ShobyomeiDao // ikaShiryokoiDao から shobyomeiDao に修正

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "dpc_database"
                )
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
