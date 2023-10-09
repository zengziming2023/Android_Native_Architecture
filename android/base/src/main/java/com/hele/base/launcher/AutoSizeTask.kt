package com.hele.base.launcher

import com.hele.base.launcher.task.Task

class AutoSizeTask : Task() {
    override fun isRunOnMainThread(): Boolean = false

    override fun run() {
        // 屏幕适配方案初始化 setExcludeFontScale-true, 屏幕系统字体的变更
//        AutoSizeConfig.getInstance().apply {
//            isExcludeFontScale = true
//            isCustomFragment = true
//
//            setLog(BuildConfig.DEBUG)
//        }
    }
}