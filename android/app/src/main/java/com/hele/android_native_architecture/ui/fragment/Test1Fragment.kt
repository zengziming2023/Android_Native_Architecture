package com.hele.android_native_architecture.ui.fragment

import androidx.core.os.bundleOf
import com.alibaba.android.arouter.launcher.ARouter
import com.hele.android_native_architecture.databinding.FragmentTest1Binding
import com.hele.base.ui.BaseFragment
import com.hele.base.viewmodel.BaseViewModel

class Test1Fragment : BaseFragment<FragmentTest1Binding, BaseViewModel>() {
    override fun setUpView() {
        mViewBinging.apply {
            tvCenter.setOnClickListener {
                ARouter.getInstance().build("/base/routerA")
                    .withString("data", "data...")
                    .navigation(this@Test1Fragment.requireContext())
            }
        }
    }

    override fun applyViewModel() {

    }
}