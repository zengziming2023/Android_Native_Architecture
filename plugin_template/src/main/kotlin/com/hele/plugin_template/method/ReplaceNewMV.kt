package com.hele.plugin_template.method

import com.hele.plugin_template.classvisitor.sub.replaceclazz.ReplaceClazzManager
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class ReplaceNewMV(
    api: Int,
    methodVisitor: MethodVisitor,
    private val className: String?
) :
    MethodVisitor(api, methodVisitor) {

    override fun visitTypeInsn(opcode: Int, type: String?) {
        if (opcode == Opcodes.NEW) {
            // change new Thread
            // to new BaseThread
            val replaceClazz = ReplaceClazzManager.replaceSuperClass(className, type)
            if (replaceClazz != type) {
                println("NEW replace super clazz: $type to $replaceClazz")
                mv.visitTypeInsn(Opcodes.NEW, replaceClazz)
                return
            }
        }

        super.visitTypeInsn(opcode, type)
    }

    override fun visitMethodInsn(
        opcode: Int,
        owner: String?,
        name: String?,
        descriptor: String?,
        isInterface: Boolean
    ) {
        val replaceClazz = ReplaceClazzManager.replaceSuperClass(className, owner)
        if (replaceClazz != owner) {
            println("visitMethodInsn replace super clazz: $owner to $replaceClazz")
            super.visitMethodInsn(opcode, replaceClazz, name, descriptor, isInterface)
            return
        }

        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface)
    }
}