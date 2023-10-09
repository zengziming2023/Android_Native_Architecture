package com.hele.base.launcher.utils

import android.util.Log


object TaskLogUtil {
    private const val TAG = "AppStartTask: "
    fun showLog(log: String) {
        Log.i(TAG, log)
    }
}