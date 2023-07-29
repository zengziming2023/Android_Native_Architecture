package com.hele.base.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object GsonUtil {
    private val gson by lazy {
        Gson()
    }

    fun toJson(src: Any) = gson.toJson(src)

    fun <T> fromJson(src: String): T {
        val type = object : TypeToken<T>() {}.type
        return gson.fromJson(src, type)
    }
}