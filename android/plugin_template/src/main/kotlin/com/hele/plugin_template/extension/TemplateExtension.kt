package com.hele.plugin_template.extension

open class TemplateExtension(
    var enable: Boolean = true,
    var exclude: MutableList<String> = mutableListOf(),
    var include: MutableList<String> = mutableListOf(),
    var asmVersion: String = "ASM7",
    var pathClassesWithAsm: String = ""
) {
    override fun toString(): String {
        return "TemplateExtension(enable=$enable, exclude=$exclude, include=$include, asmVersion='$asmVersion')"
    }
}
