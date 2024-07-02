package com.hele.plugin_template.extension

import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.tree.LocalVariableNode
import org.objectweb.asm.tree.MethodNode

const val REPLACE_METHOD_SUFFIX = "\$replace"
fun MethodNode.getReplaceMethodName(): String {
    return "${name}$REPLACE_METHOD_SUFFIX"
}

fun MethodNode.isStatic(): Boolean {
    return (access and ACC_STATIC) != 0
}

fun LocalVariableNode.getLocalVarName(): String {
    return if (name == "this") {
        "outerClazz"
    } else name
}

fun LocalVariableNode.getLoadType(): Int {
    // Load the variable onto the stack based on its type
    return when (desc[0]) {
        'I', 'Z', 'S', 'B', 'C' -> ILOAD
        'J' -> LLOAD
        'F' -> FLOAD
        'D' -> DLOAD
        else -> ALOAD
    }
}