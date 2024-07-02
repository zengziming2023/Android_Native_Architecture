package com.hele.android_native_architecture.launcher

import com.google.auto.service.AutoService
import com.hele.base.launcher.task.Task
import com.kingja.loadsir.callback.ProgressCallback
import com.kingja.loadsir.core.LoadSir

@AutoService(Task::class)
class LoadSirTask : Task() {
    override fun isRunOnMainThread(): Boolean = false

    override fun run() {
        LoadSir.beginBuilder().addCallback(ProgressCallback.Builder().build())
    }
}