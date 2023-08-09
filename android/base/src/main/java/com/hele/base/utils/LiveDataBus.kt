package com.hele.base.utils

import androidx.lifecycle.LazyMutableLiveData
import androidx.lifecycle.MutableLiveData

object LiveDataBus {

    private val bus by lazy {
        mutableMapOf<String, Any>()
    }

    fun <T> with(key: String): MutableLiveData<T> {
        return bus.getOrPut(key) { LazyMutableLiveData<T>() } as MutableLiveData<T>
    }

    fun <T> with(key: String, default: T): MutableLiveData<T> {
        return bus.getOrPut(key) { LazyMutableLiveData(default) } as MutableLiveData<T>
    }
}