package com.hele.android_native_architecture.ui.activity

import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.hele.android_native_architecture.databinding.ActivityArouterTestBinding
import com.hele.android_native_architecture.rvItemTextview
import com.hele.android_native_architecture.ui.controller.base.BaseTypedEpoxyController
import com.hele.android_native_architecture.viewmodel.ArouterTestViewModel
import com.hele.base.ui.BaseActivity

@Route(path = "/base/routerA")
class ArouterTestActivity : BaseActivity<ActivityArouterTestBinding, ArouterTestViewModel>() {

    @JvmField
    @Autowired(name = "data")
    var data: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        ARouter.getInstance().inject(this)
        super.onCreate(savedInstanceState)
    }

    private val controller by lazy {
        object : BaseTypedEpoxyController<List<String>>() {
            override fun buildModels(data: List<String>) {
                // controller 来处理数据，并构建逻辑，粒度细化
                data.forEach {
                    rvItemTextview {
                        id(it.hashCode())
                        itemModel(it)
                    }
                }
            }

        }
    }

    override fun setUpView() {
//        ARouter.getInstance().build("/base/routerB").navigation(this)
        // 使用epoxy 来做解耦， controller 来处理数据，并构建逻辑，粒度细化
        mViewBinding.apply {
            recycleView.setController(controller)
        }
    }

    override fun applyViewModel() {
        mViewModel.apply {
            controller.setData(listOf("a", "b", "c"))
        }
    }
}