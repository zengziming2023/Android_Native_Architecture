package com.hele.android_native_architecture.ui.fragment

import com.github.fragivity.navigator
import com.github.fragivity.push
import com.hele.android_native_architecture.databinding.FragmentMainBinding
import com.hele.android_native_architecture.viewmodel.MainViewModel
import com.hele.base.ui.BaseFragment
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class MainFragment : BaseFragment<FragmentMainBinding, MainViewModel>() {

    private val mainViewModel3 by activityViewModel<MainViewModel>()

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