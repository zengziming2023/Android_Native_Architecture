package com.hele.plugin_template.classvisitor.sub

import com.hele.plugin_template.classvisitor.base.BaseClassVisitor
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.FieldVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.commons.AdviceAdapter

class TraceMethodCV(nextClassVisitor: ClassVisitor) : BaseClassVisitor(nextClassVisitor) {

    override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor {
        return super.visitAnnotation(descriptor, visible)
    }

    override fun visitField(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        value: Any?
    ): FieldVisitor {
        return super.visitField(access, name, descriptor, signature, value)
    }

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        return super.visitMethod(access, name, descriptor, signature, exceptions).let { mv ->
            object : AdviceAdapter(api, mv, access, name, descriptor) {

                private val targetAnnotationDes = "Lcom/hele/base/annotation/TraceMethod;"
                private var match = false

                override fun visitAnnotation(
                    descriptor: String?,
                    visible: Boolean
                ): AnnotationVisitor {
                    if (targetAnnotationDes == descriptor) {
                        match = true
                    }
                    return super.visitAnnotation(descriptor, visible)
                }

                override fun onMethodEnter() {
                    super.onMethodEnter()
                    if (!match) return

                    // 插桩代码
                    visitFieldInsn(
                        GETSTATIC,
                        "com/hele/android_native_architecture/plugin/TraceMethodManager",
                        "INSTANCE",
                        "Lcom/hele/android_native_architecture/plugin/TraceMethodManager;"
                    )
                    visitMethodInsn(
                        INVOKEVIRTUAL,
                        "com/hele/android_native_architecture/plugin/TraceMethodManager",
                        "traceMethodStart",
                        "()V",
                        false
                    )
                }

                override fun onMethodExit(opcode: Int) {
                    super.onMethodExit(opcode)
                    if (!match) return
                    visitFieldInsn(
                        GETSTATIC,
                        "com/hele/android_native_architecture/plugin/TraceMethodManager",
                        "INSTANCE",
                        "Lcom/hele/android_native_architecture/plugin/TraceMethodManager;"
                    )
                    visitMethodInsn(
                        INVOKEVIRTUAL,
                        "com/hele/android_native_architecture/plugin/TraceMethodManager",
                        "traceMethodEnd",
                        "()V",
                        false
                    )
                }
            }
        }
    }


    override fun visitEnd() {
        super.visitEnd()
    }

}