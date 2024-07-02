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
import java.lang.reflect.Type

abstract class BaseFragment<VB : ViewBinding, VM : BaseViewModel> : Fragment() {
    private val types by lazy {
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