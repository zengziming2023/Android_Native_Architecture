package com.hele.plugin_template.base

import com.hele.plugin_template.TemplatePlugin
import com.hele.plugin_template.method.BaseMethodHandler
import com.hele.plugin_template.method.MethodHandlerManager
import com.hele.plugin_template.method.ProxyMethodVisitor
import com.hele.plugin_template.method.SuperClassMethodVisitor
import com.hele.plugin_template.super_class.SuperClassReplace
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.commons.AdviceAdapter

class TemplateClassVisitor(nextClassVisitor: ClassVisitor) : ClassVisitor(
    TemplatePlugin.getASMVersion(), nextClassVisitor
) {
    private var shouldReplaySuperClazz = false
    private var curClazzName: String? = null

    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {

        // change classA extend Thread
        // to classA extend BaseThread
        val replaceSuperClazz = SuperClassReplace.replaceSuperClass(name, superName)
        shouldReplaySuperClazz = replaceSuperClazz != superName
        curClazzName = name
        super.visit(version, access, name, signature, replaceSuperClazz, interfaces)
//        println("visit clazz name = $name, superName = $superName")
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
        val visitor = super.visitMethod(access, name, descriptor, signature, exceptions).let {
            if (shouldReplaySuperClazz) {
                SuperClassMethodVisitor(TemplatePlugin.getASMVersion(), it, curClazzName)
            } else {
                ProxyMethodVisitor(TemplatePlugin.getASMVersion(), it, curClazzName)
            }
        }

        return TemplateAdviceAdapter(visitor, access, name, descriptor)

    }
}

class TemplateAdviceAdapter(
    private val orgMethodVisitor: MethodVisitor,
    private val access: Int,
    private val name: String?,
    private val descriptor: String?
) : AdviceAdapter(TemplatePlugin.getASMVersion(), orgMethodVisitor, access, name, descriptor) {

    private var methodHandler: BaseMethodHandler? = null

    override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor {
        methodHandler =
            MethodHandlerManager.getMethodHandler(descriptor, visible, this, methodHandler)
        if (methodHandler != null) {
            println("descriptor = $descriptor, methodHandler = $methodHandler")
        }
        return super.visitAnnotation(descriptor, visible)
    }

    override fun visitCode() {
        super.visitCode()
    }

    override fun onMethodEnter() {
        super.onMethodEnter()
        methodHandler?.onMethodEnter(orgMethodVisitor, access, name, descriptor)
    }

    override fun onMethodExit(opcode: Int) {
        super.onMethodExit(opcode)
        methodHandler?.onMethodExit(opcode, orgMethodVisitor, access, name, descriptor)
    }

    fun getNextLocal(): Int {
        return nextLocal
    }
}