package com.hele.android_native_architecture.ui.fragment

import com.github.fragivity.navigator
import com.github.fragivity.push
import com.hele.android_native_architecture.viewmodel.MainViewModel
import com.hele.android_native_architecture.databinding.FragmentMainBinding
import com.hele.base.ui.BaseFragment

class MainFragment : BaseFragment<FragmentMainBinding, MainViewModel>() {
    override fun setUpView() {
        mViewBinging.apply {
            btnTest.setOnClickListener {
                navigator.push(Test1Fragment::class)
            }
        }
    }

    override fun applyViewModel() {
    }
}