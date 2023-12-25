package com.hele.base.utils

import androidx.annotation.Keep
import com.elvishew.xlog.XLog

@Keep
object LoginUtil {

    private var isLogin = false

    fun isLogin() = isLogin

    fun login() {
        // TODO : login logic
        isLogin = true
    }
}

@Keep
fun requestLogin(block: () -> Unit) {
    if (LoginUtil.isLogin()) {
        block()
    } else {
        XLog.d("this function request login first..")
    }
}