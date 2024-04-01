package com.hele.ksp_template.annotation.processor

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.hele.annotation_template.Greeting
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.writeTo

class GreetingProcessor(private val environment: SymbolProcessorEnvironment) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val logger = environment.logger
        logger.info("GreetingProcessor process start")
        val annotatedClasses =
            resolver.getSymbolsWithAnnotation(Greeting::class.qualifiedName!!)
        for (annotatedClazz in annotatedClasses) {
            if (annotatedClazz !is KSClassDeclaration) {
                continue
            }

            val clazzName = annotatedClazz.simpleName.asString()
            val packageName = annotatedClazz.packageName.asString()

            // 添加方法
            // fun sayHello(){println("Hello, World!)}
            val funSpec = FunSpec.builder("sayHello")
                .addModifiers(KModifier.PUBLIC)
                .returns(Unit::class)
                .addStatement("println(\"Hello, World!\")")
                .build()

            // 添加属性
            val propertySpec = PropertySpec.builder("name", String::class)
                .addModifiers(KModifier.PRIVATE)
                .initializer("\"zzm\"")
                .build()

            val typeSpec = TypeSpec.classBuilder("${clazzName}_generate")
                .addFunction(funSpec)
                .addProperty(propertySpec)
//                .superclass(Any::class)
//                .addSuperinterface(Any::class)
                .build()

            val fileSpec = FileSpec.builder(packageName, "${clazzName}_generate")
                .addType(typeSpec)
                .build()

            fileSpec.writeTo(environment.codeGenerator, false, emptyList())
        }
        logger.info("GreetingProcessor process end")

        return emptyList()
    }
}