package com.hele.annotation_template

@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS)
annotation class Greeting(val name: String, val age: Int)
