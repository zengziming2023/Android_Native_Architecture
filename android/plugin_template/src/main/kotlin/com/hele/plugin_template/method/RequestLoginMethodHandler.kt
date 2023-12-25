package com.hele.plugin_template.method

import com.hele.plugin_template.base.TemplateAdviceAdapter
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.commons.AdviceAdapter

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
        desc: String?
    ) {
        super.onMethodEnter(visitor, access, name, desc)
        // 在方法入口插入代码
//        visitor.visitVarInsn(Opcodes.ALOAD, 0) // 加载当前对象
//        visitor.visitMethodInsn(
//            Opcodes.INVOKESTATIC,
//            "com/hele/base/utils/LoginUtilKt",
//            "requestLogin",
//            "(Lkotlin/jvm/functions/Function0;)V",
//            false
//        )

    }
}