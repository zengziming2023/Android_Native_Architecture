package com.hele.android_native_architecture.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hele.android_native_architecture.room.dao.UserDao
import com.hele.android_native_architecture.room.entity.User

@Database(entities = [User::class], version = 1, exportSchema = true)
abstract class AppDb : RoomDatabase() {
    abstract fun userDao(): UserDao
}