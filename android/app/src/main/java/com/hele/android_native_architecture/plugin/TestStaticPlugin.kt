package com.hele.android_native_architecture.plugin

import com.elvishew.xlog.XLog
import com.hele.base.annotation.RequestLogin

class TestStaticPlugin {

    companion object {
        @RequestLogin
        fun testStatic(message: String, code: Int) {
            XLog.d("testStatic message = $message, code = $code")
        }
    }
}