package com.hele.base.annotation

import androidx.annotation.Keep

@Keep
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.BINARY)
annotation class TraceMethod()
