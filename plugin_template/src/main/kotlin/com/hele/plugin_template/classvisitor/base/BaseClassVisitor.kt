package com.hele.plugin_template.classvisitor.base

import com.hele.plugin_template.TemplatePlugin
import org.objectweb.asm.ClassVisitor

abstract class BaseClassVisitor(nextClassVisitor: ClassVisitor) :
    ClassVisitor(TemplatePlugin.getASMVersion(), nextClassVisitor) {

    protected var curClazzName: String? = null

    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        curClazzName = name
        super.visit(version, access, name, signature, superName, interfaces)
    }
}