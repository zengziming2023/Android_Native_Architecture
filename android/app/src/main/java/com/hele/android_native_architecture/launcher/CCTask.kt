package com.hele.android_native_architecture.launcher

import com.billy.cc.core.component.CC
import com.hele.android_native_architecture.BuildConfig
import com.hele.android_native_architecture.cc.ComponentTest
import com.hele.base.launcher.task.Task

class CCTask : Task() {
    override fun isRunOnMainThread(): Boolean = false

    override fun run() {
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