package com.hele.webview.model

import androidx.annotation.Keep
import com.google.gson.JsonObject

@Keep
data class JsParam(val handlerName: String, val params: JsonObject? = null)
