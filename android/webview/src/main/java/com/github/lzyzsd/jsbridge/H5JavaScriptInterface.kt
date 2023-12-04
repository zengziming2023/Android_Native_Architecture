package com.github.lzyzsd.jsbridge

import android.webkit.JavascriptInterface
import com.elvishew.xlog.XLog
import com.github.lzyzsd.jsbridge.BridgeWebView.BaseJavascriptInterface
import com.hele.base.utils.GsonUtil
import com.hele.webview.commands.CommandsManager
import com.hele.webview.model.JsParam
import java.lang.ref.WeakReference

internal class H5JavaScriptInterface(
    mCallbacks: Map<String, OnBridgeCallback>,
    private val baseWebView: WeakReference<BaseWebView>
) : BaseJavascriptInterface(mCallbacks) {
    override fun send(p0: String?): String {
        return p0 ?: "this is default string"
    }

    /**
     * entry point: function for js call native
     */
    @JavascriptInterface
    fun nativeForJs(jsParam: String?, callbackId: String? = null) {
        XLog.d("js call native: $jsParam, callbackId = $callbackId")
        jsParam.takeIf { !it.isNullOrEmpty() }?.let {
            kotlin.runCatching {
                val jsParamObj = GsonUtil.fromJson<JsParam>(it)
                // dispatch to handler
                CommandsManager.executeCommand(
                    jsParamObj.handlerName,
                    GsonUtil.toJson(jsParamObj.params),
                    callbackId,
                    baseWebView
                )

            }.onFailure {
                XLog.e(it)
            }
        } ?: kotlin.run {
            XLog.d("js call native exception: jsParam = $jsParam")
        }
    }
}