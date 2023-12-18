package com.hele.plugin_template.base

import com.hele.plugin_template.TemplatePlugin
import com.hele.plugin_template.method.BaseMethodHandler
import com.hele.plugin_template.method.MethodHandlerManager
import com.hele.plugin_template.method.TraceMethodHandler
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.commons.AdviceAdapter

class TemplateClassVisitor(nextClassVisitor: ClassVisitor) : ClassVisitor(
    TemplatePlugin.getASMVersion(), nextClassVisitor
) {
    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        super.visit(version, access, name, signature, superName, interfaces)
//        println("visit clazz name = $name")
    }

    override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor {
        return super.visitAnnotation(descriptor, visible)
    }

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        val visitor = super.visitMethod(access, name, descriptor, signature, exceptions)

        return TemplateAdviceAdapter(visitor, access, name, descriptor)

    }
}

class TemplateAdviceAdapter(
    visitor: MethodVisitor,
    access: Int,
    name: String?,
    descriptor: String?
) : AdviceAdapter(TemplatePlugin.getASMVersion(), visitor, access, name, descriptor) {

    private var methodHandler: BaseMethodHandler? = null

    override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor {
        methodHandler =
            MethodHandlerManager.getMethodHandler(descriptor, visible, this, methodHandler)
        if (methodHandler != null) {
            println("descriptor = $descriptor, methodHandler = $methodHandler")
        }
        return super.visitAnnotation(descriptor, visible)
    }

    override fun onMethodEnter() {
        super.onMethodEnter()
        methodHandler?.onMethodEnter()
    }

    override fun onMethodExit(opcode: Int) {
        super.onMethodExit(opcode)
        methodHandler?.onMethodExit(opcode)
    }

    fun getNextLocal(): Int {
        return nextLocal
    }
}