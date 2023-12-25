package com.hele.plugin_template.method

import com.hele.plugin_template.super_class.SuperClassReplace
import org.objectweb.asm.MethodVisitor

class SuperClassMethodVisitor(
    api: Int,
    methodVisitor: MethodVisitor,
    private val className: String?
) :
    MethodVisitor(api, methodVisitor) {

    override fun visitMethodInsn(
        opcode: Int,
        owner: String?,
        name: String?,
        descriptor: String?,
        isInterface: Boolean
    ) {
        // change Thread super()
        // to BaseThread super()
        if ("<init>" == name) {
            val replaceName = SuperClassReplace.replaceSuperClass(className, owner)
            mv.visitMethodInsn(opcode, replaceName, name, descriptor, isInterface)
            return
        }

        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface)
    }
}