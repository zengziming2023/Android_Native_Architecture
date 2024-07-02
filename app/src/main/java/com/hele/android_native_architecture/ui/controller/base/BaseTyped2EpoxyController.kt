package com.hele.android_native_architecture.ui.controller.base

import android.os.Handler
import com.airbnb.epoxy.EpoxyAsyncUtil
import com.airbnb.epoxy.Typed2EpoxyController

abstract class BaseTyped2EpoxyController<T, U>(
    asyncBackgroundHandler: Handler = EpoxyAsyncUtil.getAsyncBackgroundHandler(),
    asyncBackgroundHandler1: Handler = EpoxyAsyncUtil.getAsyncBackgroundHandler()
) : Typed2EpoxyController<T, U>(asyncBackgroundHandler, asyncBackgroundHandler1) {

    init {
        setFilterDuplicates(true)
    }

}