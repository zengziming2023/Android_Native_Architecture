package com.hele.plugin_template.test

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.FieldVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.commons.AdviceAdapter


class TestClassVisitor(api: Int, cv: ClassVisitor?) : ClassVisitor(api, cv) {

    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
//        println("TestClassVisitor visit: $name extends $superName")
        super.visit(version, access, name, signature, superName, interfaces)
    }

    override fun visitField(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        value: Any?
    ): FieldVisitor {
//        println("TestClassVisitor visitField: $name, $descriptor")
        return super.visitField(access, name, descriptor, signature, value)
    }

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
//        println("TestClassVisitor visitMethod: $name, $descriptor")
        val methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions)
        return if (name == "myMethod") {
            // 无状态，在上下插入对应的asm代码即可
            object : AdviceAdapter(api, methodVisitor, access, name, descriptor) {
                override fun onMethodEnter() {
                    super.onMethodEnter()
                    mv.visitVarInsn(ALOAD, 0)
                    mv.visitMethodInsn(
                        INVOKESPECIAL,
                        "com/hele/plugin_template/test/MyClass",
                        "printlnStart",
                        "()V",
                        false
                    )
                }

                override fun onMethodExit(opcode: Int) {
                    super.onMethodExit(opcode)
                    mv.visitVarInsn(ALOAD, 0)
                    mv.visitMethodInsn(
                        INVOKESPECIAL,
                        "com/hele/plugin_template/test/MyClass",
                        "printlnEnd",
                        "()V",
                        false
                    )
                }
            }
        } else methodVisitor
    }

    override fun visitEnd() {
        super.visitEnd()
    }
}