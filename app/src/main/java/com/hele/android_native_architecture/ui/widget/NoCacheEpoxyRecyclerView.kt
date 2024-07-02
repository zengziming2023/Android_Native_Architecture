package com.hele.android_native_architecture.ui.widget

import android.content.Context
import android.util.AttributeSet
import com.airbnb.epoxy.EpoxyRecyclerView

class NoCacheEpoxyRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : EpoxyRecyclerView(context, attrs, defStyleAttr) {

    // cache是activity级别，防止内存泄露
    override fun shouldShareViewPoolAcrossContext(): Boolean = false
}