package com.hele.android_native_architecture.plugin

import com.elvishew.xlog.XLog
import com.hele.base.annotation.TraceMethod

object TestPlugin {

    @TraceMethod
    fun test() {
//        TraceMethodManager.traceMethodStart()
        XLog.d("test log.")
//        TraceMethodManager.traceMethodEnd()
    }
}