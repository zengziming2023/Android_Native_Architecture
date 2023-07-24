package com.hele.base.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.hele.base.viewmodel.BaseViewModel
import java.lang.reflect.ParameterizedType

abstract class BaseActivity<VB : ViewBinding, VM : BaseViewModel> : AppCompatActivity() {

    private val types by lazy {
        val parameterizedType: ParameterizedType = javaClass.genericSuperclass as ParameterizedType
        parameterizedType.actualTypeArguments
    }

    protected val mViewBinding: VB by lazy {
        (types[0] as Class<*>).getMethod("inflate").invoke(null, layoutInflater) as VB
    }

    protected val mViewModel: VM by lazy {
        ViewModelProvider(this)[types[1] as Class<VM>]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mViewBinding.root)
        setUpView()
        applyViewModel()

    }

    abstract fun setUpView()

    abstract fun applyViewModel()
}