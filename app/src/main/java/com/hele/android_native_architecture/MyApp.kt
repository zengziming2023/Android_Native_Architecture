package com.hele.android_native_architecture

import com.elvishew.xlog.XLog
import com.hele.base.BaseApplication
import com.hele.base.launcher.task.Task
import java.util.ServiceLoader

class MyApp : BaseApplication() {

    override fun onCreate() {
        super.onCreate()
    }

    override fun getTasks(): List<Task>? {
        return ServiceLoader.load(Task::class.java, Task::class.java.classLoader)
            .toList().apply {
                forEach {
                    XLog.d("add task : $it")
                }
            }

//        return listOf(
////            CCTask(),
//////            EpicTask(),
////            KoinTask(),
////            LoadSirTask()
//        )
    }

}