package com.hele.android_native_architecture.ui.activity

import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.facade.annotation.Route
import com.hele.base.ui.BaseActivity
import com.hele.base.viewmodel.BaseViewModel

@Route(path = "base/routerB")
class ArouterTestBctivity : BaseActivity<ViewBinding, BaseViewModel>() {
    override fun setUpView() {
    }

    override fun applyViewModel() {
    }
}