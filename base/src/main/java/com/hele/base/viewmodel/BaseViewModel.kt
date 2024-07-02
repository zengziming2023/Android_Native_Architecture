package com.hele.base.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

open class BaseViewModel : ViewModel() {


    protected fun <T> executeSuspend(
        block: suspend () -> T,
        onError: ((throwable: Throwable) -> Unit)? = null
    ) {
        viewModelScope.launch(Dispatchers.IO + CoroutineExceptionHandler { _, throwable ->
            onError?.invoke(throwable)
        }) {
            block()
        }
    }

}