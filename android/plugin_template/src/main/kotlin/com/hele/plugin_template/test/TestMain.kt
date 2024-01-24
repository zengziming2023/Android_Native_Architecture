package com.hele.plugin_template.test

import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import org.objectweb.asm.util.CheckClassAdapter
import org.objectweb.asm.util.Textifier
import org.objectweb.asm.util.TraceClassVisitor
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

//fun wrapMethodWithLambda(inputClass: ByteArray): ByteArray {
//    val cr = ClassReader(inputClass)
//    val cw = ClassWriter(cr, ClassWriter.COMPUTE_MAXS or ClassWriter.COMPUTE_FRAMES)
//    val cv = LambdaWrapperClassVisitor(Opcodes.ASM9, cw)
//    cr.accept(cv, 0)
//
//    return cw.toByteArray()
//}

fun wrap(inputClazz: ByteArray, path: String?): ByteArray {
    val cr = ClassReader(inputClazz)
    val cw = ClassWriter(cr, ClassWriter.COMPUTE_FRAMES) // ClassWriter.COMPUTE_MAXS or
    val checkClass = CheckClassAdapter(cw)
    val traceClass = TraceClassVisitor(checkClass, object : Textifier(Opcodes.ASM9) {
        override fun visitClassEnd() {
            super.visitClassEnd()
            getText().forEach {
                println(it)
            }
        }
    }, null)

    val testClass = TestNodeClassVisitor(Opcodes.ASM9, traceClass, path) // TestClassVisitor

    cr.accept(testClass, 0)

    return cw.toByteArray()
}

fun main() {
//    val clazzLoader = MyClass::class.java.classLoader
//    println("MyClass clazzLoader = $clazzLoader")
    val myClazz = MyClass::class.java
    val curByteArray = myClazz.getResourceAsStream("MyClass.class")!!.readBytes()
    println("curByteArray = $curByteArray")

    val path = getCurrentClassPath(myClazz)
    println("my clazz path = $path")

//    val myClassBytes = wrapMethodWithLambda(
//        curByteArray
//    )
    val myClassBytes = wrap(curByteArray, path)
    println("myClassBytes = $myClassBytes")

    val myClazzPath = "$path/${MyClass::class.simpleName}.class"
    println("myClazzPath = $myClazzPath")
    val success =
        saveByteArrayToFile(
            myClassBytes,
            myClazzPath
        )
    // "/Users/ziming.zeng/Documents/asm/${
    //                MyClass::class.qualifiedName?.replace(
    //                    ".",
    //                    "/"
    //                )
    //            }.class"
    println("asm result: $success")

    val innerClazz = Class.forName("com.hele.plugin_template.test.MyClass\$InnermyMethod")
//    innerClazz.getDeclaredMethod("invoke", innerClazz)
    println("innerClazz = $innerClazz")
}

fun saveByteArrayToFile(byteArray: ByteArray, filePath: String): Boolean {
    return try {
        val file = File(filePath)

        // 创建文件的父目录（如果不存在）
        file.parentFile?.mkdirs()

        // 使用 FileOutputStream 将 ByteArray 写入文件
        val fos = FileOutputStream(file)
        fos.write(byteArray)
        fos.close()

        true // 保存成功
    } catch (e: IOException) {
        e.printStackTrace()
        false // 保存失败
    }
}

// 获取当前类的绝对路径；
fun getCurrentClassPath(claz: Class<*>): String? {
    val f = File(claz.getResource("").path)
    return f.path
}