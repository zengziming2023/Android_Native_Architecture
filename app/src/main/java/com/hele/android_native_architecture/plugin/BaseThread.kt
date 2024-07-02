package com.hele.android_native_architecture.plugin

import android.os.Build
import androidx.annotation.RequiresApi
import com.elvishew.xlog.XLog

open class BaseThread : Thread {
    constructor() : super() {
        XLog.d("base thread constructor")
    }

    constructor(target: Runnable?) : super(target) {
        XLog.d("base thread constructor Runnable")

    }

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

    override fun run() {
        XLog.d("base thread before run.")
        super.run()
        XLog.d("base thread after run.")
    }
}