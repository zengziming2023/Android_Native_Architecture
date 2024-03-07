package com.hele.base.ui.fragivity

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.proxyFragmentFactory
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.NavHostFragment
import androidx.viewbinding.ViewBinding
import com.elvishew.xlog.XLog
import com.github.fragivity.loadRoot
import com.hele.base.R
import com.hele.base.databinding.ActivityFragHostBinding
import com.hele.base.ui.BaseActivity
import com.hele.base.viewmodel.BaseViewModel
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import kotlin.reflect.KClass

abstract class FragBaseActivity<VM : BaseViewModel> : BaseActivity<ActivityFragHostBinding, VM>() {

    abstract fun getRootFragmentClazz(): KClass<out Fragment>

    private val liveCycleCallback = object : FragmentManager.FragmentLifecycleCallbacks() {
        override fun onFragmentCreated(
            fm: FragmentManager,
            f: Fragment,
            savedInstanceState: Bundle?
        ) {
            super.onFragmentCreated(fm, f, savedInstanceState)
            XLog.e("onFragmentCreated: ${f::class.java.simpleName}")
        }

        override fun onFragmentViewCreated(
            fm: FragmentManager,
            f: Fragment,
            v: View,
            savedInstanceState: Bundle?
        ) {
            super.onFragmentViewCreated(fm, f, v, savedInstanceState)
            XLog.e("onFragmentViewCreated: ${f::class.java.simpleName}")
        }

        override fun onFragmentStarted(fm: FragmentManager, f: Fragment) {
            super.onFragmentStarted(fm, f)
            XLog.e("onFragmentStarted: ${f::class.java.simpleName}")
        }

        override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
            super.onFragmentResumed(fm, f)
            XLog.e("onFragmentResumed: ${f::class.java.simpleName}")
        }

        override fun onFragmentPaused(fm: FragmentManager, f: Fragment) {
            super.onFragmentPaused(fm, f)
            XLog.e("onFragmentPaused: ${f::class.java.simpleName}")
        }

        override fun onFragmentStopped(fm: FragmentManager, f: Fragment) {
            super.onFragmentStopped(fm, f)
            XLog.e("onFragmentStopped: ${f::class.java.simpleName}")
        }

        override fun onFragmentViewDestroyed(fm: FragmentManager, f: Fragment) {
            super.onFragmentViewDestroyed(fm, f)
            XLog.e("onFragmentViewDestroyed: ${f::class.java.simpleName}")
        }

        override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
            super.onFragmentDestroyed(fm, f)
            XLog.e("onFragmentDestroyed: ${f::class.java.simpleName}")
        }
    }


    private val navHostFragment by lazy {
        supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        proxyFragmentFactory()
        super.onCreate(savedInstanceState)
        supportFragmentManager.registerFragmentLifecycleCallbacks(liveCycleCallback, true)
        navHostFragment.loadRoot(getRootFragmentClazz())
    }

    override fun onDestroy() {
        super.onDestroy()
        supportFragmentManager.unregisterFragmentLifecycleCallbacks(liveCycleCallback)
    }
}