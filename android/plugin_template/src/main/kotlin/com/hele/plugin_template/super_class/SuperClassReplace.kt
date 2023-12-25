package com.hele.plugin_template.super_class

/**
 * tool for replace superClazzA to clazzB
 */
object SuperClassReplace {

    private val replaceClassMap by lazy {
        mapOf(
            "java/lang/Thread" to "com/hele/android_native_architecture/plugin/BaseThread"
        )
    }

    fun replaceSuperClass(curClazzName: String?, oldSuperClazz: String?): String? {
        return oldSuperClazz?.let {
            replaceClassMap[it]?.takeIf {
                it != curClazzName
            }?.apply {
                println("replace super clazz: $oldSuperClazz to $this")
            } ?: it
        }
    }
}