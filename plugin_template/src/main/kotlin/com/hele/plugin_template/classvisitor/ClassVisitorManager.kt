package com.hele.plugin_template.classvisitor

import com.hele.plugin_template.classvisitor.sub.ReplaceClazzCV
import com.hele.plugin_template.classvisitor.sub.RequestLoginCV
import com.hele.plugin_template.classvisitor.sub.TraceMethodCV
import org.objectweb.asm.ClassVisitor

object ClassVisitorManager {

    private val classVisitorList: List<Class<out ClassVisitor>> by lazy {
        listOf(
            RequestLoginCV::class.java,
            ReplaceClazzCV::class.java,
            TraceMethodCV::class.java
        )
    }

    fun createClassVisitor(nextClassVisitor: ClassVisitor): ClassVisitor {
        var finalClassVisitor = nextClassVisitor
        classVisitorList.asReversed().forEach { cv ->
            kotlin.runCatching {
                // 拿到构造函数
                cv.getConstructor(ClassVisitor::class.java).newInstance(finalClassVisitor)
                // 通过构造函数来创建实例
            }.getOrNull()?.let {
//                println("createClassVisitor: $it")
                finalClassVisitor = it
            }
        }
//        println("final createClassVisitor: $finalClassVisitor")
        return finalClassVisitor
    }

}