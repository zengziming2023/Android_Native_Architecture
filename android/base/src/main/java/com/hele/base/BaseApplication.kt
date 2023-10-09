package com.hele.base

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import com.hele.base.launcher.AutoSizeTask
import com.hele.base.launcher.dispatcher.TaskDispatcher
import com.hele.base.launcher.task.Task

open class BaseApplication : Application() {

    val TAG by lazy {
        this::class.java.simpleName
    }

    lateinit var curActivity: Activity

    override fun onCreate() {
        super.onCreate()
        registerActivityCallbacks()

        TaskDispatcher
            .addTask(AutoSizeTask()).apply {
                getTasks()?.forEach {
                    addTask(it)
                }
            }
            .start()
            .await()
    }

    private fun registerActivityCallbacks() {
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                Log.v(
                    TAG,
                    "onActivityCreated ${activity::class.java.simpleName}--savedInstanceState = $savedInstanceState"
                )
            }

            override fun onActivityStarted(activity: Activity) {
                Log.v(TAG, "onActivityStarted ${activity::class.java.simpleName}")
            }

            override fun onActivityResumed(activity: Activity) {
                Log.v(TAG, "onActivityResumed ${activity::class.java.simpleName}")
                curActivity = activity
            }

            override fun onActivityPaused(activity: Activity) {
                Log.v(TAG, "onActivityPaused ${activity::class.java.simpleName}")
            }

            override fun onActivityStopped(activity: Activity) {
                Log.v(TAG, "onActivityStopped ${activity::class.java.simpleName}")
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
                Log.v(
                    TAG,
                    "onActivitySaveInstanceState ${activity::class.java.simpleName}--outState = $outState"
                )
            }

            override fun onActivityDestroyed(activity: Activity) {
                Log.v(TAG, "onActivityDestroyed ${activity::class.java.simpleName}")
            }

        })
    }

    open fun getTasks(): List<Task>? = null

}