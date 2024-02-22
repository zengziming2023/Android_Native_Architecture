package com.hele.plugin_template.classvisitor.sub

import com.hele.plugin_template.classvisitor.base.BaseClassVisitor
import com.hele.plugin_template.method.ReplaceNewMV
import com.hele.plugin_template.method.ReplaceSuperClazzMV
import com.hele.plugin_template.classvisitor.sub.replaceclazz.ReplaceClazzManager
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor

class ReplaceClazzCV(nextClassVisitor: ClassVisitor) : BaseClassVisitor(nextClassVisitor) {

    private var shouldReplaySuperClazz = false

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
        val replaceSuperClazzName = ReplaceClazzManager.replaceSuperClass(name, superName)
        shouldReplaySuperClazz = replaceSuperClazzName != superName
        // 处理继承extends的场景
        super.visit(version, access, name, signature, replaceSuperClazzName, interfaces)
    }

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        val visitor = super.visitMethod(access, name, descriptor, signature, exceptions).let { mv ->
            if (shouldReplaySuperClazz) {
                // 处理Thread构造函数的场景
                ReplaceSuperClazzMV(api, mv, curClazzName)
            } else {
                // TODO: 这里感觉是有性能问题的
                // 处理new Thread
                ReplaceNewMV(api, mv, curClazzName)
            }
        }
        return visitor
    }


    override fun visitEnd() {
        super.visitEnd()
    }


}