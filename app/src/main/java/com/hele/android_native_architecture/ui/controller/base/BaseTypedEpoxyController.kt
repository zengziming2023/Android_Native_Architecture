package com.hele.android_native_architecture.ui.controller.base

import android.os.Handler
import com.airbnb.epoxy.EpoxyAsyncUtil
import com.airbnb.epoxy.TypedEpoxyController

abstract class BaseTypedEpoxyController<T>(
    asyncBackgroundHandler: Handler = EpoxyAsyncUtil.getAsyncBackgroundHandler(),
    asyncBackgroundHandler1: Handler = EpoxyAsyncUtil.getAsyncBackgroundHandler()
) : TypedEpoxyController<T>(asyncBackgroundHandler, asyncBackgroundHandler1) {

    init {
        setFilterDuplicates(true)
    }

}