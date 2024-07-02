package com.hele.android_native_architecture.h5

import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.facade.annotation.Route
import com.hele.base.viewmodel.BaseViewModel
import com.hele.webview.BaseH5BrowserActivity

@Route(path = "/h5/browser")
class H5BrowserActivity : BaseH5BrowserActivity<ViewBinding, BaseViewModel>() {
    override fun setUpView() {
    }

    override fun applyViewModel() {
    }
}