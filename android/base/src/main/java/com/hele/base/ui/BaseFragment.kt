package com.hele.base.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.hele.base.viewmodel.BaseViewModel
import java.lang.reflect.ParameterizedType

abstract class BaseFragment<VB : ViewBinding, VM : BaseViewModel> : Fragment() {
    private val types by lazy {
        val parameterizedType: ParameterizedType = javaClass.genericSuperclass as ParameterizedType
        parameterizedType.actualTypeArguments
    }

    protected val mViewBinging: VB by lazy {
        (types[0] as Class<*>).getMethod("inflate", LayoutInflater::class.java)
            .invoke(null, layoutInflater) as VB
    }

    protected val mViewModel: VM by lazy {
        ViewModelProvider(this)[types[1] as Class<VM>]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return mViewBinging.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpView()
        applyViewModel()
    }

    abstract fun setUpView()

    abstract fun applyViewModel()
}