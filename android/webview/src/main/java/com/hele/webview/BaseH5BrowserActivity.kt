package com.hele.webview

import androidx.viewbinding.ViewBinding
import com.hele.base.ui.BaseActivity
import com.hele.base.viewmodel.BaseViewModel

/**
 * BaseH5BrowserActivity should run in a sub application.
 */
abstract class BaseH5BrowserActivity<VB : ViewBinding, VM : BaseViewModel> :
    BaseActivity<VB, VM>() {
}