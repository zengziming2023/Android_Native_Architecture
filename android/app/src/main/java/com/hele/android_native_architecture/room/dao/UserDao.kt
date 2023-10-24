package com.hele.android_native_architecture.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.hele.android_native_architecture.room.entity.User

@Dao
interface UserDao {

    @get:Query("SELECT * FROM user")
    val all: List<User?>?

    @Query("SELECT * FROM user WHERE 'id' IN (:userIds)")
    suspend fun loadAllByUserIds(userIds: IntArray?): List<User?>?

    @Query("SELECT * FROM user LIMIT 1")
    suspend fun findUser(): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User): Long

    @Delete
    suspend fun delete(vararg user: User): Int

    @Query("DELETE FROM user WHERE 'id' == (:id)")
    suspend fun deleteById(id: Int): Int

    @Update
    suspend fun update(vararg user: User): Int

    @Query("DELETE FROM user")
    suspend fun deleteAllUser(): Int

    @Query("SELECT COUNT(*) FROM user")
    suspend fun countAll(): Int
}