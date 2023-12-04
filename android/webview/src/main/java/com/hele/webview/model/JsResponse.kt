package com.hele.webview.model

import androidx.annotation.Keep
import com.hele.base.utils.GsonUtil

@Keep
data class JsResponse<T>(val code: Int, val msg: String, val data: T?) {
    companion object {
        private const val CODE_SUCCESS = 0
        private const val CODE_FAIL = -1

        fun <T> success(data: T?): String {
            return GsonUtil.toJson(JsResponse(0, "", data))
        }

        fun <T> error(code: Int = -1, msg: String = "", data: T? = null): String {
            return GsonUtil.toJson(JsResponse(code, msg, data))
        }
    }
}


/**
 * for transfer binder big data.
 */
@Keep
data class JsSegmentResponse(val index: Int, val total: Int, val result: String)

const val MAX_JS_LENGTH = 250 * 1_000
fun String.largerThanBinderSize() = this.length > MAX_JS_LENGTH

fun String.toSegmentList(): List<JsSegmentResponse> {
    return if (this.largerThanBinderSize()) {
        val segmentResult = mutableListOf<JsSegmentResponse>()
        val size = this.length / MAX_JS_LENGTH

        var start = 0
        var end: Int
        for (i in 0..size) {
            end = if (i == size) {
                this.length
            } else {
                start + MAX_JS_LENGTH
            }
            this.substring(start, end).let {
                segmentResult.add(JsSegmentResponse(i, size, it))
            }
            start += MAX_JS_LENGTH
        }
        segmentResult
    } else {
        listOf(JsSegmentResponse(0, 1, this))
    }
}
