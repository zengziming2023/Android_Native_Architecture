package com.hele.android_native_architecture.plugin

import com.elvishew.xlog.XLog

object TraceMethodManager {

    private var traceMethodStart: Long = -1
    fun traceMethodStart() {
        XLog.d("trace method start.")
        traceMethodStart = System.currentTimeMillis()
    }

    fun traceMethodEnd() {
        XLog.d("trace method end, cost: ${System.currentTimeMillis() - traceMethodStart}")
    }
}