package com.hele.android_native_architecture.ui.activity

import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.hele.base.ui.BaseActivity
import com.hele.base.viewmodel.BaseViewModel

@Route(path = "base/routerA")
class ArouterTestActivity : BaseActivity<ViewBinding, BaseViewModel>() {
    override fun setUpView() {
        ARouter.getInstance().build("base/routerB").navigation(this)
    }

    override fun applyViewModel() {
    }
}