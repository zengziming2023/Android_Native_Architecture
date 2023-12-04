package com.hele.webview

import androidx.viewbinding.ViewBinding
import com.hele.base.ui.BaseActivity
import com.hele.base.viewmodel.BaseViewModel
import com.hele.webview.cache.WebViewCacheHolder

/**
 * BaseH5BrowserActivity should run in a sub application.
 */
abstract class BaseH5BrowserActivity<VB : ViewBinding, VM : BaseViewModel> :
    BaseActivity<VB, VM>() {

    private val webView by lazy {
        WebViewCacheHolder.acquireWebViewInternal(this)
    }


    override fun onDestroy() {
        super.onDestroy()
        // when release webView, it will recycle auto.
        webView.release()
    }
}