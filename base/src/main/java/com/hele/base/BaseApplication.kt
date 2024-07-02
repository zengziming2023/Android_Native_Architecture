package com.hele.base

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.elvishew.xlog.LogConfiguration
import com.elvishew.xlog.LogLevel
import com.elvishew.xlog.XLog
import com.hele.base.launcher.AutoSizeTask
import com.hele.base.launcher.dispatcher.TaskDispatcher
import com.hele.base.launcher.task.Task

open class BaseApplication : Application() {

    lateinit var curActivity: Activity

    override fun onCreate() {
        super.onCreate()
        initXlog()
        registerActivityCallbacks()

        TaskDispatcher
            .apply {
                addTask(AutoSizeTask())
                getTasks()?.forEach {
                    addTask(it)
                }
            }
            .start()
            .await()
    }

    private fun initXlog() {
        XLog.init(
            LogConfiguration.Builder().logLevel(
                LogLevel.ALL
            ).enableThreadInfo()
                .threadFormatter {
                    "[${it.name}]"
                }
                .build()
        )
    }

    private fun registerActivityCallbacks() {
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                XLog.v(
                    "onActivityCreated ${activity::class.java.simpleName}--savedInstanceState = $savedInstanceState"
                )
            }

            override fun onActivityStarted(activity: Activity) {
                XLog.v("onActivityStarted ${activity::class.java.simpleName}")
            }

            override fun onActivityResumed(activity: Activity) {
                XLog.v("onActivityResumed ${activity::class.java.simpleName}")
                curActivity = activity
            }

            override fun onActivityPaused(activity: Activity) {
                XLog.v("onActivityPaused ${activity::class.java.simpleName}")
            }

            override fun onActivityStopped(activity: Activity) {
                XLog.v("onActivityStopped ${activity::class.java.simpleName}")
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
                XLog.v(

                    "onActivitySaveInstanceState ${activity::class.java.simpleName}--outState = $outState"
                )
            }

            override fun onActivityDestroyed(activity: Activity) {
                XLog.v("onActivityDestroyed ${activity::class.java.simpleName}")
            }

        })
    }

    open fun getTasks(): List<Task>? = null

}