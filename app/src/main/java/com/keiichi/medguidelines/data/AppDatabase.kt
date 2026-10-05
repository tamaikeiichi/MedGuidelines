// C:/Users/tamaikeiichi/StudioProjects/MedGuidelines/app/src/main/java/com/keiichi/medguidelines/data/AppDatabase.kt

package com.keiichi.medguidelines.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

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
        IcdO3Entity::class,
        IcdO3TopographyEntity::class,
        IkaShinryokoiEntity::class,
        ShikaShinryokoiEntity::class,
    ],
    version = 8,
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
    abstract fun icdO3Dao(): IcdO3Dao
    abstract fun icdO3TopographyDao(): IcdO3TopographyDao
    abstract fun ikaShinryokoiDao(): IkaShinryokoiDao
    abstract fun shikaShinryokoiDao(): ShikaShinryokoiDao

    companion object {
        // 医科診療行為マスタのテーブル追加のみ。既存テーブル（お気に入り等）は保持する
        private val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `ika_shinryokoi_master` (" +
                        "`id` INTEGER NOT NULL, `kanjiMeisho` TEXT, `kanaMeisho` TEXT, " +
                        "`tensuShikibetsu` TEXT NOT NULL, `tensu` TEXT NOT NULL, " +
                        "`normalizedKanji` TEXT NOT NULL, `normalizedKana` TEXT NOT NULL, " +
                        "`isFavorite` INTEGER NOT NULL, `sortOrder` INTEGER NOT NULL, " +
                        "PRIMARY KEY(`id`))"
                )
            }
        }

        // 歯科診療行為マスタのテーブル追加のみ。既存テーブル（お気に入り等）は保持する
        private val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `shika_shinryokoi_master` (" +
                        "`id` INTEGER NOT NULL, `kanjiMeisho` TEXT, `kanaMeisho` TEXT, " +
                        "`tensuShikibetsu` TEXT NOT NULL, `tensu` TEXT NOT NULL, " +
                        "`normalizedKanji` TEXT NOT NULL, `normalizedKana` TEXT NOT NULL, " +
                        "`isFavorite` INTEGER NOT NULL, `sortOrder` INTEGER NOT NULL, " +
                        "PRIMARY KEY(`id`))"
                )
            }
        }

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "dpc_database"
                )
                    .addMigrations(MIGRATION_6_7, MIGRATION_7_8)
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
