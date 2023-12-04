package com.github.lzyzsd.jsbridge

import com.github.lzyzsd.jsbridge.BridgeWebViewClient.OnLoadJSListener
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient
import java.net.URLDecoder

internal open class BaseWebViewClient(private val loadJsLsn: OnLoadJSListener? = null) :
    WebViewClient() {

    override fun onPageFinished(webView: WebView?, url: String?) {
        super.onPageFinished(webView, url)
        loadJsLsn?.onLoadStart()
        // load js code to create bridge.
        webViewLoadLocalJs(webView, "WebViewJavascriptBridge.js")
        loadJsLsn?.onLoadFinished()
    }

    private fun webViewLoadLocalJs(webView: WebView?, path: String?) {
        val jsContent = BridgeUtil.assetFile2Str(webView?.context, path)
        webView?.loadUrl("javascript:$jsContent")
    }

    override fun shouldOverrideUrlLoading(webview: WebView?, url: String?): Boolean {
        return if (interceptUrl(url)) true else super.shouldOverrideUrlLoading(webview, url)
    }

    private fun interceptUrl(url: String?): Boolean {
        val finalUrl = kotlin.runCatching {
            URLDecoder.decode(url, "UTF-8")
        }.getOrDefault(url.orEmpty())

        return finalUrl.startsWith(BridgeUtil.YY_RETURN_DATA) || finalUrl.startsWith(BridgeUtil.YY_OVERRIDE_SCHEMA)
    }
}