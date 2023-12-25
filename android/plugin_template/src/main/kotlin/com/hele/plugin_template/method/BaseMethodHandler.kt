package com.hele.plugin_template.method

import com.hele.plugin_template.base.TemplateAdviceAdapter
import org.objectweb.asm.MethodVisitor

abstract class BaseMethodHandler(
    val methodVisitor: TemplateAdviceAdapter,
    val parent: BaseMethodHandler? = null
) {
    abstract fun matchAnnotation(descriptor: String?, visible: Boolean): Boolean

    open fun onMethodEnter(
        visitor: MethodVisitor,
        access: Int,
        name: String?,
        descriptor: String?
    ) {
        parent?.onMethodEnter(visitor, access, name, descriptor)
    }

    open fun onMethodExit(
        opcode: Int, visitor: MethodVisitor,
        access: Int,
        name: String?,
        descriptor: String?
    ) {
        parent?.onMethodExit(opcode, visitor, access, name, descriptor)
    }
}