package com.hele.android_native_architecture.launcher

import com.elvishew.xlog.XLog
import com.hele.base.launcher.task.Task
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers

class EpicTask : Task() {
    override fun isRunOnMainThread(): Boolean = false

    override fun run() {
        initEpicHook()
    }

    private fun initEpicHook() {
        hookThread()
    }

    private fun hookThread() {
        // hook thread class and all subclass
        XposedHelpers.findAndHookConstructor(Thread::class.java, object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam?) {
                super.afterHookedMethod(param)
                param?.let {
                    val clazz = it.thisObject::class.java
                    if (clazz != Thread::class.java) {
                        XLog.d("found class extend Thread:$clazz")
                        XposedHelpers.findAndHookMethod(clazz, "run", ThreadMethodHook())
                    }
                }
            }
        })
        // hook run method.
        XposedHelpers.findAndHookMethod(Thread::class.java, "run", ThreadMethodHook())
    }

    inner class ThreadMethodHook : XC_MethodHook() {
        override fun beforeHookedMethod(param: MethodHookParam?) {
            super.beforeHookedMethod(param)
            param?.apply {
                XLog.d("before hook this = ${thisObject}, method = ${method}, args = ${args.joinToString()}")
            }
        }

        override fun afterHookedMethod(param: MethodHookParam?) {
            super.afterHookedMethod(param)
            param?.apply {
                XLog.d("after hook this = ${thisObject}, method = ${method}, args = ${args.joinToString()}")
            }
        }
    }
}