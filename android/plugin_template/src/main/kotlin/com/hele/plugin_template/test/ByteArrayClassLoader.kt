package com.hele.plugin_template.test

import java.util.HashMap

class ByteArrayClassLoader : ClassLoader() {
    private val classes = HashMap<String, ByteArray>()

    fun defineClass(name: String, bytecode: ByteArray): Class<*> {
        classes[name] = bytecode
        return super.defineClass(name, bytecode, 0, bytecode.size)
    }

    override fun findClass(name: String): Class<*>? {
        val bytecode = classes[name]
        return if (bytecode != null) {
            defineClass(name, bytecode)
        } else {
            super.findClass(name)
        }
    }
}
