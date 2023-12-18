package com.hele.plugin_template.method

import com.hele.plugin_template.base.TemplateAdviceAdapter

abstract class BaseMethodHandler(
    val methodVisitor: TemplateAdviceAdapter,
    val parent: BaseMethodHandler? = null
) {
    abstract fun matchAnnotation(descriptor: String?, visible: Boolean): Boolean

    open fun onMethodEnter() {
        parent?.onMethodEnter()
    }

    open fun onMethodExit(opcode: Int) {
        parent?.onMethodExit(opcode)
    }
}