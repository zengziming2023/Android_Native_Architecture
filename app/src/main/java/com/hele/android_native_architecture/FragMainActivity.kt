package com.hele.android_native_architecture

import androidx.fragment.app.Fragment
import com.hele.android_native_architecture.ui.fragment.MainFragment
import com.hele.android_native_architecture.viewmodel.MainViewModel
import com.hele.base.ui.fragivity.FragBaseActivity
import kotlin.reflect.KClass

class FragMainActivity : FragBaseActivity<MainViewModel>() {
    override fun getRootFragmentClazz(): KClass<out Fragment> = MainFragment::class

    override fun setUpView() {

    }

    override fun applyViewModel() {

    }
}