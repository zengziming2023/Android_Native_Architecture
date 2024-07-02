package com.hele.webview.cache

import android.content.Context
import android.content.MutableContextWrapper
import com.elvishew.xlog.XLog
import com.github.lzyzsd.jsbridge.BaseWebView
import com.github.lzyzsd.jsbridge.BaseWebViewClient
import com.hele.base.extensions.applicationContext
import com.tencent.smtt.sdk.WebChromeClient
import java.util.Stack
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.write
import kotlin.properties.Delegates

object WebViewCacheHolder {
    private var maxCacheNum by Delegates.notNull<Int>()
    private val webViewCacheStack by lazy {
        Stack<BaseWebView>()
    }
    private val readWriteLock by lazy {
        ReentrantReadWriteLock()
    }

    fun init(maxCacheNum: Int = 1) {
        this.maxCacheNum = maxCacheNum
        prepareWebView()
    }

    private fun prepareWebView() {
        readWriteLock.write {
            if (webViewCacheStack.size < maxCacheNum) {
                do {
                    webViewCacheStack.push(createWebView(MutableContextWrapper(applicationContext())))
                } while (webViewCacheStack.size < maxCacheNum)
            }
        }
    }

    /**
     * acquire webView from cache stack.
     */
    fun acquireWebViewInternal(context: Context): BaseWebView {
        return readWriteLock.write {
            if (webViewCacheStack.isEmpty()) {
                return createWebView(context)
            }
            webViewCacheStack.pop().apply {
                (this.context as? MutableContextWrapper)?.baseContext = context
                this.stopLoading()
                XLog.d("acquireWebViewInternal webView = ${this.hashCode()}")
            }
        }
    }

    fun recycleWebView(webView: BaseWebView) {
        readWriteLock.write {
            if (webViewCacheStack.size < maxCacheNum) {
                webViewCacheStack.push(webView.apply {
                    (context as? MutableContextWrapper)?.baseContext = applicationContext()
                })
            }
        }
    }

    private fun createWebView(context: Context): BaseWebView {
        return BaseWebView(MutableContextWrapper(context)).apply {
            webViewClient = BaseWebViewClient()
            webChromeClient = WebChromeClient()
        }
    }

}