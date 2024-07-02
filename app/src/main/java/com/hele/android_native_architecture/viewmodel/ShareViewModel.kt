package com.hele.android_native_architecture.viewmodel

import com.hele.base.viewmodel.BaseViewModel

class ShareViewModel : BaseViewModel() {

    private var shareCount = 1

    fun updateShareCount() = shareCount++

    fun queryShareCount() = shareCount

}