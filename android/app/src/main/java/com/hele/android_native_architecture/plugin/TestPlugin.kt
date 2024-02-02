package com.hele.android_native_architecture.plugin

import android.os.Build
import androidx.annotation.Keep
import androidx.annotation.RequiresApi
import com.elvishew.xlog.XLog
import com.hele.base.annotation.RequestLogin
import com.hele.base.annotation.TraceMethod
import com.hele.base.utils.requestLogin

@Keep
object TestPlugin {

    @TraceMethod
    fun test() {
//        TraceMethodManager.traceMethodStart()
        printlnStart()
        XLog.d("test log.")
        printlnEnd()
//        TraceMethodManager.traceMethodEnd()
    }

    private fun printlnStart() {
        println("method start")
    }

    private fun printlnEnd() {
        println("method end")
    }

    @RequestLogin
    fun testLoginRequest() {
//        requestLogin {
        testLambda {
            XLog.d("before login")
            XLog.d("testLoginRequest")
        }


//        }
    }

    fun testLambda(funC: () -> Unit) {
        requestLogin(funC)
    }

    @RequestLogin
    fun testAfterLogin(message: String) {
        XLog.d(("have login, do something, message:$message"))
    }

    @RequestLogin
    fun testAfterLogin(message: String, code: Int) {
        XLog.d(("have login, do something, message:$message, code = $code"))
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

    fun testLambda(age: Int, name: String) {
        val funC = {
            testLambdaSecond(age, name)
        }
        funC.invoke()
    }

    fun testLambdaSecond(age: Int, name: String) {
        println("age = $age, name = $name")
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