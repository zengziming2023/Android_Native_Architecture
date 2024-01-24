package com.hele.plugin_template.test

import com.android.tools.r8.internal.mv
import groovyjarjarasm.asm.ClassWriter.COMPUTE_FRAMES
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.FieldNode
import org.objectweb.asm.tree.InnerClassNode
import org.objectweb.asm.tree.InsnList
import org.objectweb.asm.tree.InsnNode
import org.objectweb.asm.tree.LabelNode
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.MethodNode
import org.objectweb.asm.tree.TypeInsnNode
import org.objectweb.asm.tree.VarInsnNode


class TestNodeClassVisitor(
    api: Int,
    private val classVisitor: ClassVisitor?,
    private val path: String?
) :
    ClassNode(api) {

    private var clazzName: String = ""

    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        println("visit name = $name, sourceFile = $sourceFile")
        super.visit(version, access, name, signature, superName, interfaces)
        clazzName = name.orEmpty()

    }

    override fun visitInnerClass(
        name: String?,
        outerName: String?,
        innerName: String?,
        access: Int
    ) {
        super.visitInnerClass(name, outerName, innerName, access)
        println("visitInnerClass name = $name")
    }

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        println("visitMethod name = $name")
        return super.visitMethod(access, name, descriptor, signature, exceptions)
    }


    override fun visitEnd() {
        super.visitEnd()
        println("visitEnd")

        // 放在visitEnd 处理的理由是，visitEnd只调用一次，避免了多次调用的总是

        // clear method body for myMethod method
        methods.firstOrNull {
            it.name == "myMethod"
        }?.let { methodNode ->
            // 创建一个方法
            createReplaceMethod(methodNode)

            methodNode.instructions.forEach {
                println("insn node: $it, op = ${it.opcode}, type = ${it.type}")
                val op = it.opcode
                // TODO: 7. 这个判断是有问题的，因为一个方法里可能会有多个return，需要优化一下
                if ((op in IRETURN..RETURN) || op == ATHROW) {
                    // 生成内部类
                    generateInnerClazz(methodNode)

                    createInnerClazzAndCallLambda(methodNode, it)

                } else {
                    // 清空原来的逻辑
                    if (it !is LabelNode) {
                        methodNode.instructions.remove(it)
                    }
                }
            }
        }

        accept(classVisitor)
    }

    /**
     * 创建内部类实例，并把实例作为入参，传递给另外一个方法使用
     */
    private fun createInnerClazzAndCallLambda(
        methodNode: MethodNode,
        it: AbstractInsnNode?
    ) {
        val innerClazzName = "$clazzName\$Inner${methodNode.name}"
        println("inner class construct desc = (${methodNode.localVariables[0].desc})V")
        println("inner class desc = L$innerClazzName;")
        // 创建内部类实例，差把他赋值给一个入参
        val tailInsns = InsnList().apply {
            // // "L$clazzName\$Inner${methodNode.name};"
            add(TypeInsnNode(NEW, innerClazzName))
            add(InsnNode(DUP))
            add(VarInsnNode(ALOAD, 0))
            // TODO: 8. 加载的入参 + 构造函数的 desc 需要重新处理一下
            add(
                MethodInsnNode(
                    INVOKESPECIAL,
                    innerClazzName,
                    "<init>",
                    "(${methodNode.localVariables[0].desc})V",
                )
            )

            // TODO: 9. 保存到第几个本地变量中，这理论上需要计算
            add(VarInsnNode(ASTORE, 1))
            add(VarInsnNode(ALOAD, 0))
            add(VarInsnNode(ALOAD, 1))

            // TODO: 10. 这里应该需要外部配置，
            add(
                MethodInsnNode(
                    INVOKESPECIAL,
                    clazzName,
                    "testLambda",
                    "(Lkotlin/jvm/functions/Function0;)V", false
                )
            )
        }
        methodNode.instructions.insertBefore(it, tailInsns)

        // TODO: 11. maxStack maxLocals 可以自动计算吗？
        methodNode.maxStack = 3
        methodNode.maxLocals = 2
    }

    /**
     * 创建一个新的方法，与methodNode签名一致，强制public，并把原来methodNode的逻辑迁移过来
     */
    private fun createReplaceMethod(methodNode: MethodNode) {
        methods.add(MethodNode(
            api,
            ACC_PUBLIC,
            "${methodNode.name}_hele",
            methodNode.desc,
            methodNode.signature,
            methodNode.exceptions.toTypedArray()
        ).apply {
            copyInstructions(methodNode, this)
            maxLocals = methodNode.maxLocals
            maxStack = methodNode.maxStack
        })
    }

    private fun generateInnerClazz(methodNode: MethodNode) {
        val clazzThis = methodNode.localVariables[0]
        val clazzDesc = clazzThis.desc

        println("clazzName = $clazzName, clazzDesc = $clazzDesc")

        val innerClazzName = "$clazzName\$Inner${methodNode.name}"

        val innerClass = InnerClassNode(
            innerClazzName,
            clazzName,
            methodNode.name,
            ACC_PUBLIC
        )
        // 添加内部类到外部类的 InnerClasses 属性 -- 把内部类跟外部类关联起来，这个很关键
        this.visitInnerClass(
            innerClass.name,
            innerClass.outerName,
            innerClass.innerName,
            innerClass.access
        )

        // 创建内部类
        val innerClassNode = ClassNode(api).apply {
            access = ACC_PUBLIC + ACC_FINAL + ACC_SUPER
            name = innerClazzName
            superName = "java/lang/Object"
            interfaces = arrayOf("kotlin/jvm/functions/Function0").toMutableList()
            version = this@TestNodeClassVisitor.version
        }
        // 创建对应的属性
        innerClassNode.fields.add(
            FieldNode(
                ACC_PRIVATE + ACC_FINAL,
                "outerClazz",
                clazzDesc,
                null,
                null
            )
        )
        // TODO: 1. 需要根据methodNode的入参，创建对应内部类的属性
        // TODO: 2. 需要判断methodNode的acces，如果是static的话，则不需要创建outerClazz
        // TODO: 3. 构造函数的签名，需要根据方法入参来拼接

        // 创建构造函数
        val constructor = MethodNode(
            ACC_PUBLIC,
            "<init>",
            "(${clazzDesc})V",
            null,
            null
        )
        constructor.visitCode()
        constructor.visitVarInsn(ALOAD, 0) // 加载this
        constructor.visitMethodInsn(
            INVOKESPECIAL,
            "java/lang/Object",
            "<init>",
            "()V",
            false
        ) // 调用父类构造函数
        // 将参数赋值给类的属性
        constructor.visitVarInsn(ALOAD, 0) // 加载this
        constructor.visitVarInsn(ALOAD, 1) // 加载第一个参数
        constructor.visitFieldInsn(
            PUTFIELD,
            innerClazzName,
            "outerClazz",
            clazzDesc
        ) // 将参数赋值给属性
        // TODO: 4. 需要根据方法入参，来赋值给类属性 -- 并把入参保存在一个list中


        constructor.visitInsn(RETURN)
        // TODO: 5. stack, locals 的计算，需要处理一下
        constructor.visitMaxs(2, 2)
        constructor.visitEnd()

        // 创建invoke 方法，并调用固定的方法
        val invokeMethod =
            MethodNode(ACC_PUBLIC, "invoke", "()V", null, null)
        invokeMethod.visitCode()
        invokeMethod.visitVarInsn(ALOAD, 0)
        invokeMethod.visitFieldInsn(GETFIELD, innerClazzName, "outerClazz", clazzDesc)
        // TODO: 6. 其它参数的加载也需要处理一下
        invokeMethod.visitMethodInsn(
            INVOKESPECIAL,
            clazzName,
            "${methodNode.name}_hele",
            methodNode.desc, false
        )
        invokeMethod.visitInsn(RETURN)
        // TODO: 7. stack, locals 的计算，需要处理一下
        invokeMethod.visitMaxs(2, 1)
        invokeMethod.visitEnd()

        val invokeBridgeMethod = MethodNode(
            ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, "invoke", "()Ljava/lang/Object;", null, null
        )
        invokeBridgeMethod.visitCode()
        // Start of method
        val l0 = Label()
        invokeBridgeMethod.visitLabel(l0)

        // Load 'this' reference
        invokeBridgeMethod.visitVarInsn(ALOAD, 0)

        // Invoke virtual method on 'this'
        invokeBridgeMethod.visitMethodInsn(
            INVOKEVIRTUAL,
            innerClazzName,
            "invoke",
            "()V",
            false
        )

        // Getstatic for kotlin.Unit.INSTANCE
        invokeBridgeMethod.visitFieldInsn(GETSTATIC, "kotlin/Unit", "INSTANCE", "Lkotlin/Unit;")

        // Return kotlin.Unit.INSTANCE
        invokeBridgeMethod.visitInsn(ARETURN)
        // TODO: 7. stack, locals 的计算，需要处理一下
        invokeMethod.visitMaxs(2, 2)
        invokeBridgeMethod.visitEnd()


        innerClassNode.methods.addAll(listOf(constructor, invokeMethod, invokeBridgeMethod))

//        innerClassNode.accept(classVisitor)
//        innerClass.accept(innerClassNode)
//        innerClassNode.accept(classVisitor)

        innerClassNode.let {

            val cw = ClassWriter(COMPUTE_FRAMES)
            it.accept(cw)
            val innerClazzByteArray = cw.toByteArray()
            println("inner clazz byte array = $innerClazzByteArray")
            val saveInnerClazzResult = saveByteArrayToFile(
                innerClazzByteArray,
                "$path/${innerClazzName.substring(innerClazzName.lastIndexOf("/"))}.class"
            )
            println("saveInnerClazzResult = $saveInnerClazzResult")
        }
    }

    private fun copyInstructions(sourceMethod: MethodNode, targetMethod: MethodNode) {
        val labelMap = mutableMapOf<LabelNode, LabelNode>()

        // 克隆标签
        for (insnNode in sourceMethod.instructions) {
            if (insnNode is LabelNode) {
                labelMap[insnNode] = LabelNode()
            }
        }

        // 克隆指令并处理标签
        for (insnNode in sourceMethod.instructions) {
            val clonedInsn = insnNode.clone(labelMap)
            targetMethod.instructions.add(clonedInsn)
        }
    }
}