package com.hele.android_native_architecture.room.entity

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey


@Keep
@Entity(tableName = "User")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "name")
    val name: String? = null,
    @ColumnInfo(name = "age")
    val age: Int? = -1
) {
    @Ignore
    var ignoreField: String = ""    //
}
