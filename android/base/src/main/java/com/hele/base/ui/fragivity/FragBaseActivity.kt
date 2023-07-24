package com.hele.base.ui.fragivity

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.proxyFragmentFactory
import androidx.navigation.fragment.NavHostFragment
import com.github.fragivity.loadRoot
import com.hele.base.R
import com.hele.base.databinding.ActivityFragHostBinding
import com.hele.base.ui.BaseActivity
import com.hele.base.viewmodel.BaseViewModel
import kotlin.reflect.KClass

abstract class FragBaseActivity<VM : BaseViewModel> : BaseActivity<ActivityFragHostBinding, VM>() {

    abstract fun getRootFragmentClazz(): KClass<out Fragment>

    private val navHostFragment by lazy {
        supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        proxyFragmentFactory()
        super.onCreate(savedInstanceState)
        navHostFragment.loadRoot(getRootFragmentClazz())
    }
}