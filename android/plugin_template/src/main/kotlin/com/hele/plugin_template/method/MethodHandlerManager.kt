package com.hele.plugin_template.method

import com.hele.plugin_template.base.TemplateAdviceAdapter
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.commons.AdviceAdapter
import kotlin.reflect.full.primaryConstructor

object MethodHandlerManager {

    private val methodHandlerMap by lazy {
        mapOf(
            TraceMethodHandler.MATCH_ANNOTATION to TraceMethodHandler::class,
            RequestLoginMethodHandler.MATCH_ANNOTATION to RequestLoginMethodHandler::class
        )
    }

    fun getMethodHandler(
        descriptor: String?,
        visible: Boolean,
        methodVisitor: TemplateAdviceAdapter,
        curMethodHandler: BaseMethodHandler?
    ): BaseMethodHandler? {
        return methodHandlerMap[descriptor]?.primaryConstructor?.call(
            methodVisitor,
            curMethodHandler
        )
            ?: curMethodHandler
    }

}