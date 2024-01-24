package com.hele.plugin_template.method

import com.hele.plugin_template.base.TemplateAdviceAdapter
import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class RequestLoginMethodHandler(methodVisitor: TemplateAdviceAdapter, parent: BaseMethodHandler?) :
    BaseMethodHandler(methodVisitor, parent) {
    companion object {
        const val MATCH_ANNOTATION = "Lcom/hele/base/annotation/RequestLogin;"
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
        // 创建一个Label，用于在方法中标记插入位置
        val label = Label()
        // 在requestLogin调用之后插入Label
        methodVisitor.visitLabel(label)

        // 将当前方法的方法体包装在 lambda 中
        methodVisitor.visitLdcInsn(name)
        methodVisitor.visitLdcInsn("Lkotlin/jvm/functions/Function0;")
        methodVisitor.visitMethodInsn(
            Opcodes.INVOKESTATIC,
            "kotlin/jvm/internal/Lambda",
            "metafactory",
            "(Lkotlin/jvm/functions/Function0;Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
            false
        )
        methodVisitor.visitTypeInsn(Opcodes.CHECKCAST, "kotlin/jvm/functions/Function0")
        methodVisitor.visitVarInsn(Opcodes.ASTORE, 1)

        // 在方法开头插入代码，调用requestLogin函数
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 1)
        methodVisitor.visitMethodInsn(
            Opcodes.INVOKESTATIC,
            "com/hele/base/utils/LoginUtilKt", // 替换成requestLogin函数所在的包路径
            "requestLogin",
            "(Lkotlin/jvm/functions/Function0;)V",
            false
        )
    }
    override fun visiitCode(
        orgMethodVisitor: MethodVisitor,
        access: Int,
        name: String?,
        descriptor: String?
    ) {
        super.visiitCode(orgMethodVisitor, access, name, descriptor)



    }
}