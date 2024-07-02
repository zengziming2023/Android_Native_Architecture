package com.hele.android_native_architecture.viewmodel

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import com.hele.base.viewmodel.BaseViewModel

inline fun <reified T : BaseViewModel> globalSharedViewModel(): T {
    return ViewModelProvider(AppViewModelStoreOwner)[T::class.java]
}

object AppViewModelStoreOwner : ViewModelStoreOwner {

    private val mViewModelStore by lazy {
        ViewModelStore()
    }

    override val viewModelStore: ViewModelStore
        get() = mViewModelStore

    fun clear() {
        viewModelStore.clear()
    }
}