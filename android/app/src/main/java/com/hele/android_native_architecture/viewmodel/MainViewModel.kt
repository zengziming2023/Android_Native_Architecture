package com.hele.android_native_architecture.viewmodel

import com.elvishew.xlog.XLog
import com.hele.base.viewmodel.BaseViewModel

class MainViewModel : BaseViewModel() {

    fun testKoin() {
        XLog.d("testKoin this.hashCode = ${this.hashCode()}")
    }

}