package com.hele.plugin_template.test

class MyClass {
    private fun myMethod() {
        // Original method body
        println("Hello from original method!")
    }

    private fun printlnStart() {
        println("method start")
    }

    private fun printlnEnd() {
        println("method end")
    }

    private fun testLambda(func: (() -> Unit)?) {
        func?.invoke()
    }

    fun test(){
        testLambda {
            println("test")
        }
    }

//    inner class MyInnerClass {
//        fun innerMethod() {
//            println("inner method")
//        }
//    }
}