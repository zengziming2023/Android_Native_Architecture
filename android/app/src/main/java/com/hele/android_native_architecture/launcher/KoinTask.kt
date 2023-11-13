package com.hele.android_native_architecture.launcher

import com.hele.android_native_architecture.viewmodel.MainViewModel
import com.hele.base.extensions.applicationContext
import com.hele.base.launcher.task.Task
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module

class KoinTask : Task() {

    private val appModule by lazy {
        module {
            single { MainViewModel() }
//            scope<MainActivity> { MainViewModel() }
        }
    }

    override fun isRunOnMainThread(): Boolean = false

    override fun run() {
        startKoin {
            androidContext(applicationContext())
            modules(listOf(appModule))
        }
    }
}