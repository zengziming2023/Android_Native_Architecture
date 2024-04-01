package com.hele.annotation_template

@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS)
annotation class Greeting(val message: String, val code: Int)
