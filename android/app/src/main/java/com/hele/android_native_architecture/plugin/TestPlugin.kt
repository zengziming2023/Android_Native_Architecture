package com.hele.android_native_architecture.plugin

import android.os.Build
import androidx.annotation.Keep
import androidx.annotation.RequiresApi
import com.elvishew.xlog.XLog
import com.hele.base.annotation.RequestLogin
import com.hele.base.annotation.TraceMethod

@Keep
object TestPlugin {

    @TraceMethod
    fun test() {
//        TraceMethodManager.traceMethodStart()
        XLog.d("test log.")
//        TraceMethodManager.traceMethodEnd()
    }

    @RequestLogin
    fun testLoginRequest() {
//        requestLogin {
        XLog.d("before login")
//        }
    }

    @RequestLogin
    fun testAfterLogin(message: String) {
        XLog.d(("have login, do something, message:$message"))
    }

    fun testThread() {
        TestThread {
            XLog.d("test thread start.")
        }.start()

        XLog.d("test new Thread")
        Thread {
            XLog.d("new thread start..")
        }.start()
    }
}

class TestThread : Thread {
    constructor() : super()
    constructor(target: Runnable?) : super(target)
    constructor(group: ThreadGroup?, target: Runnable?) : super(group, target)
    constructor(name: String) : super(name)
    constructor(group: ThreadGroup?, name: String) : super(group, name)
    constructor(target: Runnable?, name: String) : super(target, name)
    constructor(group: ThreadGroup?, target: Runnable?, name: String) : super(group, target, name)
    constructor(
        group: ThreadGroup?,
        target: Runnable?,
        name: String,
        stackSize: Long
    ) : super(group, target, name, stackSize)

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    constructor(
        group: ThreadGroup?,
        target: Runnable?,
        name: String,
        stackSize: Long,
        inheritThreadLocals: Boolean
    ) : super(group, target, name, stackSize, inheritThreadLocals)
}