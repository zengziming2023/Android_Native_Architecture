package com.hele.android_native_architecture

import com.hele.android_native_architecture.launcher.CCTask
import com.hele.android_native_architecture.launcher.EpicTask
import com.hele.android_native_architecture.launcher.KoinTask
import com.hele.android_native_architecture.launcher.LoadSirTask
import com.hele.base.BaseApplication
import com.hele.base.launcher.task.Task

class MyApp : BaseApplication() {

    override fun onCreate() {
        super.onCreate()
    }

    override fun getTasks(): List<Task>? {
        return listOf(
            CCTask(),
//            EpicTask(),
            KoinTask(),
            LoadSirTask()
        )
    }

}