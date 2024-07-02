package com.hele.android_native_architecture.launcher

import android.app.Application
import com.alibaba.android.arouter.launcher.ARouter
import com.google.auto.service.AutoService
import com.hele.base.extensions.applicationContext
import com.hele.base.launcher.task.Task

@AutoService(Task::class)
class ARouterTask : Task() {
    override fun isRunOnMainThread(): Boolean = false

    override fun run() {
        ARouter.init(applicationContext() as Application)
    }
}