package com.hele.plugin_template.classvisitor.sub

import com.hele.plugin_template.TemplatePlugin
import com.hele.plugin_template.classvisitor.base.BaseClassNode
import com.hele.plugin_template.extension.getLoadType
import com.hele.plugin_template.extension.getLocalVarName
import com.hele.plugin_template.extension.getReplaceMethodName
import com.hele.plugin_template.extension.isStatic
import com.hele.plugin_template.test.saveByteArrayToFile
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Label
import org.objectweb.asm.Opcodes
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
import kotlin.math.abs

class RequestLoginCV(nextClassVisitor: ClassVisitor) : BaseClassNode(nextClassVisitor) {

    companion object {
        private const val TARGET_ANNOTATION = "Lcom/hele/base/annotation/RequestLogin;"
    }

    override fun visitEnd() {
        super.visitEnd()
        // 放在visitEnd 处理的理由是，visitEnd只调用一次，避免了多次调用的总是

        // clear method body for myMethod method
        methods.filter {
            // find target method by annotation info.
            it.invisibleAnnotations?.any {
                it.desc == TARGET_ANNOTATION
            } ?: false
        }.forEach { methodNode ->
            // 创建一个方法
            createReplaceMethod(methodNode)

            // 清空原来的逻辑,并且只保留最后一个 return or throw
            var lastReturnNode: AbstractInsnNode? = null
            methodNode.instructions.forEach { insnNode ->
                if (insnNode.opcode in Opcodes.IRETURN..Opcodes.RETURN) {
                    lastReturnNode?.let {
                        // 删除上一个return,只保留最后一个return
                        methodNode.instructions.remove(it)
                    }
                    lastReturnNode = insnNode
                } else if (insnNode !is LabelNode) {
                    methodNode.instructions.remove(insnNode)
                }
            }

            lastReturnNode?.let {
                // 生成内部类
                val innerClazzNode = generateInnerClazz(methodNode)
                // 创建内部类实例，并调用LoginUtilKt.requestLogin(new InnerClazz..)
                createInnerClazzAndCallLambda(methodNode, it, innerClazzNode)
            }
        }

        accept(nextClassVisitor)
    }

    /**
     * 创建内部类实例，并把实例作为入参，传递给另外一个方法使用
     */
    private fun createInnerClazzAndCallLambda(
        methodNode: MethodNode, oldTailNode: AbstractInsnNode?, innerClazzNode: ClassNode
    ) {
        val innerClazzName = innerClazzNode.name // "$clazzName\$Inner${methodNode.name}"
        val constructorDesc = innerClazzNode.methods.firstOrNull {
            it.name == "<init>"
        }?.desc

        println("inner class construct desc = $constructorDesc")
        println("inner class desc = L$innerClazzName;")
        // 创建内部类实例，差把他赋值给一个入参
        val tailInsns = InsnList().apply {
            // // "L$clazzName\$Inner${methodNode.name};"
            add(TypeInsnNode(Opcodes.NEW, innerClazzName))
            add(InsnNode(Opcodes.DUP))
            methodNode.localVariables?.forEachIndexed { index, localVariableNode ->
                add(VarInsnNode(localVariableNode.getLoadType(), index))
            }
            // 8. 加载的入参 + 构造函数的 desc 需要重新处理一下
            add(
                MethodInsnNode(
                    Opcodes.INVOKESPECIAL,
                    innerClazzName,
                    "<init>",
                    constructorDesc,
                )
            )

            val localVarInnerClazz = methodNode.localVariables?.size ?: 0
            println("localVarInnerClazz = $localVarInnerClazz")
            // 9. 保存到第几个本地变量中，这理论上需要计算
            add(VarInsnNode(Opcodes.ASTORE, localVarInnerClazz))
            add(VarInsnNode(Opcodes.ALOAD, 0))
            add(VarInsnNode(Opcodes.ALOAD, localVarInnerClazz))

            // 10. 这里应该需要外部配置，
            add(
                MethodInsnNode(
                    Opcodes.INVOKESTATIC,
                    "com/hele/base/utils/LoginUtilKt",
                    "requestLogin",
                    "(Lkotlin/jvm/functions/Function0;)V",
                    false
                )
            )
        }
        methodNode.instructions.insertBefore(oldTailNode, tailInsns)

        //  11. maxStack maxLocals 可以自动计算吗？
//        methodNode.maxStack = 3
//        methodNode.maxLocals = 2
    }

    /**
     * 创建一个新的方法，与methodNode签名一致，强制public，并把原来methodNode的逻辑迁移过来
     */
    private fun createReplaceMethod(methodNode: MethodNode) {
        // 判断方法的修饰符是否为 private 或 protected
        val replaceMethodAccess =
            if (methodNode.access and Opcodes.ACC_PRIVATE != 0 || methodNode.access and Opcodes.ACC_PROTECTED != 0) {
                // 将 private 或 protected 修改为 public
                (methodNode.access and (Opcodes.ACC_PRIVATE or Opcodes.ACC_PROTECTED).inv()) or Opcodes.ACC_PUBLIC
            } else {
                methodNode.access or Opcodes.ACC_PUBLIC
            }

        methods.add(
            MethodNode(
                api,
                replaceMethodAccess,
                methodNode.getReplaceMethodName(),
                methodNode.desc,
                methodNode.signature,
                methodNode.exceptions.toTypedArray()
            ).apply {
                copyInstructions(methodNode, this)
                maxLocals = methodNode.maxLocals
                maxStack = methodNode.maxStack
            })
    }

    private fun generateInnerClazz(methodNode: MethodNode): ClassNode {
        val innerClazzName =
            "$curClazzName\$Inner${methodNode.name}${abs(methodNode.desc.hashCode())}"

        val innerClass = InnerClassNode(
            innerClazzName, curClazzName, methodNode.name, Opcodes.ACC_PUBLIC
        )
        // 添加内部类到外部类的 InnerClasses 属性 -- 把内部类跟外部类关联起来，这个很关键
        nextClassVisitor?.visitInnerClass(
            innerClass.name, innerClass.outerName, innerClass.innerName, innerClass.access
        )

        // 创建内部类
        val innerClassNode = ClassNode(api).apply {
            access = Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL + Opcodes.ACC_SUPER
            name = innerClazzName
            superName = "java/lang/Object"
            interfaces = arrayOf("kotlin/jvm/functions/Function0").toMutableList()
            version = this@RequestLoginCV.version
        }

        // 1. 需要根据methodNode的入参，创建对应内部类的属性
        // 2. 需要判断methodNode的acces，如果是static的话，则不需要创建outerClazz
        // 3. 构造函数的签名，需要根据方法入参来拼接

        methodNode.localVariables?.forEach {
            innerClassNode.fields.add(
                FieldNode(
                    Opcodes.ACC_PRIVATE + Opcodes.ACC_FINAL,
                    it.getLocalVarName(),
                    it.desc,
                    null,
                    null
                )
            )
        }

        val constructorDesc = "(${
            methodNode.localVariables?.joinToString("") {
                it.desc
            }.orEmpty()
        })V"
        println("constructorDesc = $constructorDesc")
        // 创建构造函数
        val constructor = MethodNode(
            Opcodes.ACC_PUBLIC, "<init>", constructorDesc, null, null
        )
        constructor.visitCode()
        constructor.visitVarInsn(Opcodes.ALOAD, 0) // 加载this
        constructor.visitMethodInsn(
            Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false
        ) // 调用父类构造函数

        methodNode.localVariables?.forEachIndexed { index, localVariableNode ->
            // 将参数赋值给类的属性
            constructor.visitVarInsn(Opcodes.ALOAD, 0) // 加载this
            constructor.visitVarInsn(localVariableNode.getLoadType(), index + 1) // 加载第一个参数
            constructor.visitFieldInsn(
                Opcodes.PUTFIELD,
                innerClazzName,
                localVariableNode.getLocalVarName(),
                localVariableNode.desc
            ) // 将参数赋值给属性
        }

        constructor.visitInsn(Opcodes.RETURN)
        constructor.visitEnd()

        // 创建invoke 方法，并调用固定的方法
        val invokeMethod =
            MethodNode(Opcodes.ACC_PUBLIC, "invoke", "()V", null, null)
        invokeMethod.visitCode()

        methodNode.localVariables?.forEach { localVariableNode ->
            invokeMethod.visitVarInsn(Opcodes.ALOAD, 0)
            invokeMethod.visitFieldInsn(
                Opcodes.GETFIELD,
                innerClazzName,
                localVariableNode.getLocalVarName(),
                localVariableNode.desc
            )
        }
        val opcodeAndSource =
            if (methodNode.isStatic()) Opcodes.INVOKESTATIC else Opcodes.INVOKEVIRTUAL
        // 6. 其它参数的加载也需要处理一下
        invokeMethod.visitMethodInsn(
            opcodeAndSource,
            curClazzName,
            methodNode.getReplaceMethodName(),
            methodNode.desc,
            false
        )
        invokeMethod.visitInsn(Opcodes.RETURN)

        invokeMethod.visitEnd()

        val invokeBridgeMethod = MethodNode(
            Opcodes.ACC_PUBLIC + Opcodes.ACC_BRIDGE + Opcodes.ACC_SYNTHETIC,
            "invoke",
            "()Ljava/lang/Object;",
            null,
            null
        )
        invokeBridgeMethod.visitCode()
        // Start of method
        val l0 = Label()
        invokeBridgeMethod.visitLabel(l0)

        // Load 'this' reference
        invokeBridgeMethod.visitVarInsn(Opcodes.ALOAD, 0)

        // Invoke virtual method on 'this'
        invokeBridgeMethod.visitMethodInsn(
            Opcodes.INVOKEVIRTUAL,
            innerClazzName,
            "invoke",
            "()V",
            false
        )

        // Getstatic for kotlin.Unit.INSTANCE
        invokeBridgeMethod.visitFieldInsn(
            Opcodes.GETSTATIC,
            "kotlin/Unit",
            "INSTANCE",
            "Lkotlin/Unit;"
        )

        // Return kotlin.Unit.INSTANCE
        invokeBridgeMethod.visitInsn(Opcodes.ARETURN)
        invokeBridgeMethod.visitEnd()

        innerClassNode.methods.addAll(listOf(constructor, invokeMethod, invokeBridgeMethod))

        innerClass.accept(innerClassNode)

        innerClassNode.let {
            val cw = ClassWriter(ClassWriter.COMPUTE_FRAMES or ClassWriter.COMPUTE_MAXS)
            it.accept(cw)
            val innerClazzByteArray = cw.toByteArray()
//            println("inner clazz byte array = $innerClazzByteArray")
            val innerFilePath = "${TemplatePlugin.pathClassesWithAsm}/${innerClazzName}.class"
            println("innerFilePath = $innerFilePath")
            val saveInnerClazzResult = saveByteArrayToFile(
                innerClazzByteArray, innerFilePath
            )
            println("saveInnerClazzResult = $saveInnerClazzResult")
        }
        return innerClassNode
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