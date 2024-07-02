package com.hele.base.ui.fragivity

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.proxyFragmentFactory
import androidx.navigation.fragment.NavHostFragment
import com.elvishew.xlog.XLog
import com.github.fragivity.loadRoot
import com.hele.base.R
import com.hele.base.databinding.ActivityFragHostBinding
import com.hele.base.ui.BaseActivity
import com.hele.base.viewmodel.BaseViewModel
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
            if (f is NavHostFragment) return
            XLog.e("onFragmentCreated: ${f::class.java.simpleName}")
        }

        override fun onFragmentViewCreated(
            fm: FragmentManager,
            f: Fragment,
            v: View,
            savedInstanceState: Bundle?
        ) {
            super.onFragmentViewCreated(fm, f, v, savedInstanceState)
            if (f is NavHostFragment) return
            XLog.e("onFragmentViewCreated: ${f::class.java.simpleName}")
        }

        override fun onFragmentStarted(fm: FragmentManager, f: Fragment) {
            super.onFragmentStarted(fm, f)
            if (f is NavHostFragment) return
            XLog.e("onFragmentStarted: ${f::class.java.simpleName}")
        }

        override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
            super.onFragmentResumed(fm, f)
            if (f is NavHostFragment) return
            XLog.e("onFragmentResumed: ${f::class.java.simpleName}")
        }

        override fun onFragmentPaused(fm: FragmentManager, f: Fragment) {
            super.onFragmentPaused(fm, f)
            if (f is NavHostFragment) return
            XLog.e("onFragmentPaused: ${f::class.java.simpleName}")
        }

        override fun onFragmentStopped(fm: FragmentManager, f: Fragment) {
            super.onFragmentStopped(fm, f)
            if (f is NavHostFragment) return
            XLog.e("onFragmentStopped: ${f::class.java.simpleName}")
        }

        override fun onFragmentViewDestroyed(fm: FragmentManager, f: Fragment) {
            super.onFragmentViewDestroyed(fm, f)
            if (f is NavHostFragment) return
            XLog.e("onFragmentViewDestroyed: ${f::class.java.simpleName}")
        }

        override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
            super.onFragmentDestroyed(fm, f)
            if (f is NavHostFragment) return
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