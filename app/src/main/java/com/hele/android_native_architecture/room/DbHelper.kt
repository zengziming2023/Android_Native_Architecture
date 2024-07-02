package com.hele.android_native_architecture.room

import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.hele.base.extensions.applicationContext

object DbHelper {

    private const val DB_NAME = "heleDb"

    val db by lazy {
        Room.databaseBuilder(applicationContext(), AppDb::class.java, DB_NAME)
//            .addMigrations(MIGRATION_1_2)
            .build()
    }

    // 数据库升级需要用到
//    private val MIGRATION_1_2 = object : Migration(1, 2) {
//        override fun migrate(database: SupportSQLiteDatabase) {
//            database.execSQL("CREATE TABLE IF NOT EXISTS `Age` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `userAge` TEXT, `age` INTEGER)")
//        }
//    }

}