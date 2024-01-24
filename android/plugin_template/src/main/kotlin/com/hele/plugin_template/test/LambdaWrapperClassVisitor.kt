package com.hele.plugin_template.test

import org.objectweb.asm.*
import org.objectweb.asm.tree.*

class LambdaWrapperClassVisitor(api: Int, cv: ClassVisitor?) : ClassVisitor(api, cv) {

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        val methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions)

        return if (name == "myMethod") LambdaMethodVisitor(methodVisitor) else methodVisitor

//        return object : MethodNode(api, access, name, descriptor, signature, exceptions) {
//            override fun visitEnd() {
//                super.visitEnd()
//
//                // 在方法的最后插入字节码，将方法体包装成Lambda表达式
//                val instructions = instructions.toArray()
//                instructions.forEachIndexed { index, insnNode ->
//                    if (insnNode is InsnNode && insnNode.opcode == Opcodes.RETURN) {
//                        instructions[index - 1] = LdcInsnNode("Hello from Lambda!")
//                        instructions[index] = MethodInsnNode(
//                            Opcodes.INVOKESTATIC,
//                            "kotlin/jvm/internal/Lambda",
//                            "access\$000",
//                            "()Ljava/lang/Object;",
//                            false
//                        )
//                        instructions[index + 1] = TypeInsnNode(
//                            Opcodes.CHECKCAST,
//                            "kotlin/jvm/functions/Function0"
//                        )
//                        instructions[index + 2] = InsnNode(Opcodes.ARETURN)
//
//                        instructions.forEach { insn ->
//                            insn.accept(methodVisitor)
//                        }
//                    }
//                }
//            }
//        }
    }

    inner class LambdaMethodVisitor(methodVisitor: MethodVisitor) :
        MethodVisitor(Opcodes.ASM9, methodVisitor) {
        override fun visitCode() {
            super.visitCode()

            println("add lambda start")
            // 在方法体前插入字节码，创建 Lambda 表达式
//            mv.visitMethodInsn(
//                Opcodes.INVOKESTATIC,
//                "kotlin/jvm/internal/Lambda",
//                "lambda",
//                "()Lkotlin/jvm/internal/Lambda;",
//                false
//            )
//            mv.visitTypeInsn(Opcodes.CHECKCAST, "kotlin/jvm/internal/Lambda")
//            mv.visitVarInsn(Opcodes.ASTORE, 1)
//            mv.visitVarInsn(Opcodes.ALOAD, 1)
//            mv.visitMethodInsn(
//                Opcodes.INVOKEVIRTUAL,
//                "kotlin/jvm/internal/Lambda",
//                "invoke",
//                "()V",
//                false
//            )

            // Insert code to save the original method's logic to a variable.
            mv.visitVarInsn(Opcodes.ASTORE, 1)
            // Insert code to create a new function.
            mv.visitTypeInsn(Opcodes.NEW, "java/lang/Runnable")
            mv.visitInsn(Opcodes.DUP)
            mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Runnable", "<init>", "()V", false)
            mv.visitVarInsn(Opcodes.ASTORE, 2)
            // Insert code to invoke the new function.
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "kotlin/Unit", "invoke", "()V", false)
            // Insert code to execute the original method's logic.
            mv.visitVarInsn(Opcodes.ALOAD, 2)
            mv.visitVarInsn(Opcodes.ALOAD, 1)
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Runnable", "run", "()V", false)
            mv.visitInsn(Opcodes.RETURN)
            println("add lambda end")
        }

        override fun visitInsn(opcode: Int) {
//            // 在方法体后插入字节码
//            if (opcode == Opcodes.RETURN) {
//                // 添加其他操作
//            }
            super.visitInsn(opcode)
        }

//        override fun visitMaxs(maxStack: Int, maxLocals: Int) {
//            super.visitMaxs(maxStack + 1, maxLocals + 1)
//        }
    }
}
