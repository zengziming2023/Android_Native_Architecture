package com.hele.base.ui

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.elvishew.xlog.XLog
import com.hele.base.viewmodel.BaseViewModel
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

abstract class BaseActivity<VB : ViewBinding, VM : BaseViewModel> : AppCompatActivity() {

    protected open val types: Array<out Type> by lazy {
//        val parameterizedType: ParameterizedType = javaClass.genericSuperclass as ParameterizedType
//        parameterizedType.actualTypeArguments

        var targetClazz: Class<*> = javaClass
        var viewBindingType: Type? = null
        var viewModelType: Type? = null

        while (targetClazz != Any::class.java) {
            val parameterizedType = targetClazz.genericSuperclass as? ParameterizedType
            if (parameterizedType != null) {
                val args = parameterizedType.actualTypeArguments
                args.forEach { type ->
                    if ((type as? Class<*>)?.let {
                            ViewBinding::class.java.isAssignableFrom(it)
                        } == true) {
                        viewBindingType = type
                    } else if ((type as? Class<*>)?.let {
                            BaseViewModel::class.java.isAssignableFrom(it)
                        } == true) {
                        viewModelType = type
                    }
                }
                if (viewBindingType != null && viewModelType != null) {
                    break
                }
            }

            targetClazz = targetClazz.superclass
        }

        arrayOf(viewBindingType!!, viewModelType!!)
    }

    protected val mViewBinding: VB by lazy {
        (types[0] as Class<*>).getMethod("inflate", LayoutInflater::class.java)
            .invoke(null, layoutInflater) as VB
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