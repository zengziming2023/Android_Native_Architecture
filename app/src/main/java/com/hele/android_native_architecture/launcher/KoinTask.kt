package com.hele.android_native_architecture.launcher

import com.google.auto.service.AutoService
import com.hele.android_native_architecture.viewmodel.MainViewModel
import com.hele.base.extensions.applicationContext
import com.hele.base.launcher.task.Task
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.annotation.KoinReflectAPI
import org.koin.core.context.startKoin
import org.koin.dsl.module

@AutoService(Task::class)
class KoinTask : Task() {

    @OptIn(KoinReflectAPI::class)
    private val appModule by lazy {
        module {
            single { MainViewModel() }
//            scope<MainActivity> { MainViewModel() }
            viewModel {
                MainViewModel()
            }
            // 下面的写法会用到反射。。
            viewModel<MainViewModel>()

            factory {
                MainViewModel()
            }
            scope<MainViewModel> {

            }
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