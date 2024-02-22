package com.hele.plugin_template

import com.android.build.api.instrumentation.FramesComputationMode
import com.android.build.api.instrumentation.InstrumentationScope
import com.android.build.api.variant.AndroidComponentsExtension
import com.hele.plugin_template.extension.TemplateExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.objectweb.asm.Opcodes

class TemplatePlugin : Plugin<Project> {
    companion object {
        var pluginExtension = TemplateExtension()
        var pathClassesWithAsm: String? = null
        fun getASMVersion(): Int {
            return when (pluginExtension.asmVersion) {
                "ASM6" -> Opcodes.ASM6
                "ASM7" -> Opcodes.ASM7
                "ASM8" -> Opcodes.ASM8
                "ASM9" -> Opcodes.ASM9
                else -> Opcodes.ASM7
            }
        }
    }

    override fun apply(project: Project) {
        // create extension
        pluginExtension =
            project.extensions.create("pluginExtension", TemplateExtension::class.java)

        // register asm transform class plugin - TemplateASMFactory
        val extension = project.extensions.getByType(AndroidComponentsExtension::class.java)
        extension.onVariants(extension.selector().all()) { variant ->
            variant.instrumentation.apply {
                transformClassesWith(
                    TemplateASMFactory::class.java,
                    InstrumentationScope.PROJECT
                ) {}
//                transformClassesWith(
//                    TemplateNodeASMFactory::class.java,
//                    InstrumentationScope.PROJECT
//                ) {}
                setAsmFramesComputationMode(FramesComputationMode.COMPUTE_FRAMES_FOR_INSTRUMENTED_METHODS)
            }

        }

        project.afterEvaluate {
            println("pluginExtension = $pluginExtension")
            it.tasks.firstOrNull {
                it.name.contains("ClassesWithAsm")
            }?.let {
                pathClassesWithAsm = it.outputs.files.firstOrNull {
                    it.path.endsWith("dirs", true)
                }?.path
                println("pathClassesWithAsm = $pathClassesWithAsm")
            } ?: kotlin.run {
                println("pathClassesWithAsm error..")

            }
        }

//        project.tasks.register("myCustomTask") {
//            it.doLast {
//                println("This is a custom task from MyCustomPlugin!")
//            }
//        }
    }
}