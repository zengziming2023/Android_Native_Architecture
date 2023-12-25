package com.hele.plugin_template.method

import com.hele.plugin_template.base.TemplateAdviceAdapter
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes.GETSTATIC
import org.objectweb.asm.Opcodes.INVOKEVIRTUAL
import org.objectweb.asm.commons.AdviceAdapter


class TraceMethodHandler(methodVisitor: TemplateAdviceAdapter, parent: BaseMethodHandler?) :
    BaseMethodHandler(methodVisitor, parent) {
    companion object {
        const val MATCH_ANNOTATION = "Lcom/hele/base/annotation/TraceMethod;"
    }

    override fun matchAnnotation(descriptor: String?, visible: Boolean): Boolean {
        return MATCH_ANNOTATION == descriptor
    }

    override fun onMethodEnter(
        visitor: MethodVisitor,
        access: Int,
        name: String?,
        descriptor: String?
    ) {
        super.onMethodEnter(visitor, access, name, descriptor)
        methodVisitor.visitFieldInsn(
            GETSTATIC,
            "com/hele/android_native_architecture/plugin/TraceMethodManager",
            "INSTANCE",
            "Lcom/hele/android_native_architecture/plugin/TraceMethodManager;"
        )
        methodVisitor.visitMethodInsn(
            INVOKEVIRTUAL,
            "com/hele/android_native_architecture/plugin/TraceMethodManager",
            "traceMethodStart",
            "()V",
            false
        )
    }

    override fun onMethodExit(
        opcode: Int, visitor: MethodVisitor,
        access: Int,
        name: String?,
        descriptor: String?
    ) {
        super.onMethodExit(opcode, visitor, access, name, descriptor)
        methodVisitor.visitFieldInsn(
            GETSTATIC,
            "com/hele/android_native_architecture/plugin/TraceMethodManager",
            "INSTANCE",
            "Lcom/hele/android_native_architecture/plugin/TraceMethodManager;"
        )
        methodVisitor.visitMethodInsn(
            INVOKEVIRTUAL,
            "com/hele/android_native_architecture/plugin/TraceMethodManager",
            "traceMethodEnd",
            "()V",
            false
        )
    }
}