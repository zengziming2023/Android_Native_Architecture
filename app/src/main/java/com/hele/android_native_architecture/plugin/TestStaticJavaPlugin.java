package com.hele.android_native_architecture.plugin;

import com.elvishew.xlog.XLog;
import com.hele.base.annotation.RequestLogin;

public class TestStaticJavaPlugin {

    @RequestLogin
    public static void testJavaStatic(String message, int code) {
        XLog.d("testJavaStatic message = " + message + ", code = " + code);
    }
}
