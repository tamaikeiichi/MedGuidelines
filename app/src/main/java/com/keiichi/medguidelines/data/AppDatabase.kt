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
        JushodoStrokeEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    /**
     * DPC関連のデータアクセスオブジェクト(DAO)を取得します。
     * Roomがこのメソッドの実装を自動生成します。
     * @return DpcDaoのインスタンス
     */
    abstract fun dpcDao(): DpcDao
    abstract fun shujutsuDao(): ShujutsuDao // <--- この行を追加
    abstract fun shochi1Dao(): Shochi1Dao // <--- この行を追加
    abstract fun shochi2Dao(): Shochi2Dao
    abstract fun fukushobyoDao(): FukushobyoDao
    abstract fun jushodoJcsDao(): JushodoJcsDao
    abstract fun nenreiDao(): NenreiDao
    abstract fun jushodoShujutsuDao(): JushodoShujutsuDao
    abstract fun jushodoStrokeDao(): JushodoStrokeDao




    companion object {
        // @Volatileアノテーションにより、INSTANCE変数が複数スレッドからアクセスされても
        // 常に最新の値であることが保証されます。
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * データベースのシングルトンインスタンスを取得します。
         * インスタンスがまだ存在しない場合は、スレッドセーフに新しいデータベースを生成します。
         *
         * @param context アプリケーションコンテキスト
         * @return AppDatabaseのシングルトンインスタンス
         */
        fun getDatabase(context: Context): AppDatabase {
            // INSTANCEがnullでなければそれを返し、nullならデータベースを生成します
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "dpc_database" // データベースファイル名
                )
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                // 生成したインスタンスを返す
                instance
            }
        }
    }
}
