package com.hele.android_native_architecture.ui.activity

import androidx.viewbinding.ViewBinding
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.hele.android_native_architecture.ui.controller.base.BaseTypedEpoxyController
import com.hele.base.ui.BaseActivity
import com.hele.base.viewmodel.BaseViewModel

@Route(path = "base/routerA")
class ArouterTestActivity : BaseActivity<ViewBinding, BaseViewModel>() {

    private val controller by lazy {
        object : BaseTypedEpoxyController<Any>() {
            override fun buildModels(data: Any?) {
                // controller 来处理数据，并构建逻辑，粒度细化
            }

        }
    }

    override fun setUpView() {
        ARouter.getInstance().build("base/routerB").navigation(this)

        // 使用epoxy 来做解耦， controller 来处理数据，并构建逻辑，粒度细化
//        recycleView.setController(controller)
    }

    override fun applyViewModel() {
    }
}