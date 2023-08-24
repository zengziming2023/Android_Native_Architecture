package com.hele.android_native_architecture

import android.app.Application
import com.billy.cc.core.component.CC
import com.hele.android_native_architecture.cc.ComponentTest

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()

        initCC()
    }

    private fun initCC() {
        CC.enableDebug(BuildConfig.DEBUG)
        CC.enableVerboseLog(BuildConfig.DEBUG)
        CC.enableRemoteCC(BuildConfig.DEBUG)

        // 组件注册 -- 可以用autoService 来进行自动注册 -- 使用反射，比asm在性能上要差一点点，但是兼容性比asm要好很多
        CC.registerComponent(ComponentTest())

    }
}