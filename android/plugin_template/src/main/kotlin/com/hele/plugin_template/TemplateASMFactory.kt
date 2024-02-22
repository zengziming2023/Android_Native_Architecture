package com.hele.plugin_template

import com.android.build.api.instrumentation.AsmClassVisitorFactory
import com.android.build.api.instrumentation.ClassContext
import com.android.build.api.instrumentation.ClassData
import com.android.build.api.instrumentation.InstrumentationParameters
import com.hele.plugin_template.classvisitor.ClassVisitorManager
import org.objectweb.asm.ClassVisitor

abstract class TemplateASMFactory : AsmClassVisitorFactory<InstrumentationParameters.None> {

    override fun createClassVisitor(
        classContext: ClassContext, nextClassVisitor: ClassVisitor
    ): ClassVisitor {
        return ClassVisitorManager.createClassVisitor(nextClassVisitor)//TemplateClassVisitor(nextClassVisitor)
    }

    override fun isInstrumentable(classData: ClassData): Boolean {
        val extension = TemplatePlugin.pluginExtension
        val clazzName = classData.className
        val useInclude = extension.include.isNotEmpty()

        return when {
            !extension.enable -> false

            extension.include.any {
                clazzName.startsWith(it)
            } -> true

            extension.exclude.any {
                clazzName.startsWith(it)
            } -> false

            useInclude -> false

            else -> true // deal with the class file.
        }
    }

}