package com.github.lzyzsd.jsbridge

import android.content.Context
import android.os.Build
import android.os.Looper
import android.os.SystemClock
import android.util.AttributeSet
import android.view.ViewGroup
import com.elvishew.xlog.XLog
import com.hele.base.utils.GsonUtil
import com.hele.webview.cache.WebViewCacheHolder
import com.hele.webview.h5process.H5ToMainAidlManager
import com.tencent.smtt.sdk.WebView
import org.json.JSONObject
import java.lang.ref.WeakReference
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.write

class BaseWebView : WebView, WebViewJavascriptBridge, IWebView,
    BridgeWebViewClient.OnLoadJSListener {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    companion object {
        private const val URL_MAX_CHARACTER_NUM = 2097152
    }

    private val mCallbacks by lazy {
        mutableMapOf<String, OnBridgeCallback>()
    }
    private var mMessages: MutableList<Any>? = mutableListOf()

    private val readWriteLock by lazy {
        ReentrantReadWriteLock()
    }

    private var mUniqueId: Long = 0L

    init {
        initSetting()
        // inject js bridge.
        addJsInterface()
        // init aidl
        H5ToMainAidlManager.initAidlConnect()
    }

    private fun initSetting() {
        settings.apply {
            javaScriptEnabled = true
            allowContentAccess = true
            databaseEnabled = true
            domStorageEnabled = true
            setAppCacheEnabled(true)
            savePassword = false
            saveFormData = false
            useWideViewPort = true
            loadWithOverviewMode = true
            userAgentString = "${this.userAgentString} android"
        }
    }

    private fun addJsInterface() {
        addJavascriptInterface(
            H5JavaScriptInterface(mCallbacks, WeakReference(this)),
            "WebViewJavascriptBridge"
        )
    }

    fun handleCallback(callbackId: String?, response: String?) {
        XLog.d("handleCallback callbackId: $callbackId, response = $response")
        callbackId?.let {
            sendResponse(it, response)
        }
    }

    private fun sendResponse(callbackId: String, response: String?) {
        callbackId.takeIf { it.isNotEmpty() }?.let {
            val response = JSResponse().apply {
                responseId = it
                responseData = response
            }
            if (Thread.currentThread() == Looper.getMainLooper().thread) {
                dispatchMessage(response)
            } else {
                post { dispatchMessage(response) }
            }
        }
    }

    private fun dispatchMessage(response: Any) {
        val messageJson = JSONObject.quote(GsonUtil.toJson(response))
        val jsCommand = String.format(BridgeUtil.JS_HANDLE_MESSAGE_FROM_JAVA, messageJson)
        XLog.d("dispatchMessage: $jsCommand")
        if (Thread.currentThread() == Looper.getMainLooper().thread) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && jsCommand.length >= URL_MAX_CHARACTER_NUM) {
                evaluateJavascript(jsCommand, null)
            } else {
                loadUrl(jsCommand)
            }
        }
    }

    override fun sendToWeb(data: String?) {
        sendToWeb(data, null)
    }

    /**
     * entry point: native send message to h5 and get response from callback
     * h5 will send response data by `response` function in BaseJavascriptInterface
     */
    override fun sendToWeb(data: String?, callback: OnBridgeCallback?) {
        doSend(null, data, callback)
    }

    override fun sendToWeb(data: String?, vararg values: Any?) {
        doSend(null, data?.let { String.format(it, values) }, null)
    }

    override fun onLoadStart() {
        XLog.d("onLoadStart")
    }

    override fun onLoadFinished() {
        XLog.d("onLoadFinished")
    }

    private fun doSend(handlerName: String?, data: String?, callback: OnBridgeCallback?) {
        val request = JSRequest().apply {
            this.data = data
            this.handlerName = handlerName

            callback?.let {
                val callbackId = String.format(
                    BridgeUtil.CALLBACK_ID_FORMAT,
                    "${++mUniqueId}${BridgeUtil.UNDERLINE_STR}${SystemClock.currentThreadTimeMillis()}"
                )
                mCallbacks[callbackId] = it
                this.callbackId = callbackId
            }
        }

        queueMessage(request)
    }

    private fun queueMessage(request: JSRequest) {
        mMessages?.let {
            readWriteLock.write {
                it.add(request)
            }
        } ?: dispatchMessage(request)
    }

    override fun destroy() {
        super.destroy()
        clearInternal()
    }

    private fun clearInternal() {
        mCallbacks.clear()
        readWriteLock.write {
            mMessages?.clear()
            mMessages = null
        }

        mUniqueId = 0
    }

    fun release() {
        loadUrl("")
        clearInternal()
        webChromeClient = null
        webViewClient = null
        clearHistory()
        (parent as? ViewGroup)?.removeView(this)
        WebViewCacheHolder.recycleWebView(this)
    }

}