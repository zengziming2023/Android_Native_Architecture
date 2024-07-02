package com.hele.base.utils

import android.os.Parcelable
import com.hele.base.extensions.applicationContext
import com.tencent.mmkv.MMKV

object KvUtil {
    init {
        MMKV.initialize(applicationContext())
    }

    private val defaultMMKV by lazy {
        MMKV.defaultMMKV()
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> encode(key: String, value: T) {
        when (value) {
            is Int -> defaultMMKV.encode(key, value)
            is Long -> defaultMMKV.encode(key, value)
            is Float -> defaultMMKV.encode(key, value)
            is Double -> defaultMMKV.encode(key, value)
            is Boolean -> defaultMMKV.encode(key, value)
            is String -> defaultMMKV.encode(key, value)
            is ByteArray -> defaultMMKV.encode(key, value)
            is Parcelable -> defaultMMKV.encode(key, value)
            is Set<*> -> defaultMMKV.encode(key, value as Set<String>)
        }
    }

    @Suppress("IMPLICIT_CAST_TO_ANY", "UNCHECKED_CAST")
    fun <T> decode(key: String, default: T): T {
        return when (default) {
            is Int -> defaultMMKV.decodeInt(key, default)
            is Long -> defaultMMKV.decodeLong(key, default)
            is Float -> defaultMMKV.decodeFloat(key, default)
            is Double -> defaultMMKV.decodeDouble(key, default)
            is Boolean -> defaultMMKV.decodeBool(key, default)
            is String -> defaultMMKV.decodeString(key, default)
            is ByteArray -> defaultMMKV.decodeBytes(key, default)
            is Parcelable -> defaultMMKV.decodeParcelable(key, default.javaClass)
            is Set<*> -> defaultMMKV.decodeStringSet(key, default as Set<String>)
            else -> default
        } as T
    }
}