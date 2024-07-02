package com.hele.ksp_template.annotation.processor

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.hele.annotation_template.AnnotationData
import com.hele.annotation_template.BaseAnnotationHandler
import com.hele.annotation_template.Greeting
import com.hele.annotation_template.GreetingAutoWired
import com.hele.annotation_template.IAnnotationHelper
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.writeTo

class GreetingProcessor(private val environment: SymbolProcessorEnvironment) : SymbolProcessor {
    @OptIn(KspExperimental::class)
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val logger = environment.logger
        logger.warn("GreetingProcessor process start")
        val annotatedClasses =
            resolver.getSymbolsWithAnnotation(Greeting::class.qualifiedName!!)
        val autoWiredList =
            resolver.getSymbolsWithAnnotation(GreetingAutoWired::class.qualifiedName!!)
        logger.warn("GreetingProcessor autoWiredList = ${autoWiredList.joinToString()}}")
        for (annotatedClazz in annotatedClasses) {
            if (annotatedClazz !is KSClassDeclaration) {
                continue
            }

            val clazzName = annotatedClazz.simpleName.asString()
            val packageName = annotatedClazz.packageName.asString()

            val greeting = annotatedClazz.getAnnotationsByType(Greeting::class).firstOrNull()

            val nullableAnnotationData = AnnotationData::class.asTypeName().copy(
                nullable = true
            )

            val autoWireds = autoWiredList.filter {
                it is KSPropertyDeclaration && it.parent == annotatedClazz
            }

            // 添加方法
            // fun sayHello(){println("Hello, World!)}
            val funSpec = FunSpec.builder("sayHello")
                .addModifiers(KModifier.PUBLIC)
                .returns(Unit::class)
                .addStatement("println(\"Hello, World!\")")
                .build()

            // 添加属性
            val defaultName = greeting?.name?.takeIf { it.isNotEmpty() } ?: "zzm"
            val propertySpec = PropertySpec.builder("name", String::class)
                .addModifiers(KModifier.PRIVATE)
                .initializer("\"$defaultName\"")
                .build()

            val defaultAge = greeting?.age?.takeIf { it != -1 } ?: 0

            val dataPropertySpec =
                PropertySpec.builder(
                    "annotationData",
                    nullableAnnotationData,
                    KModifier.PRIVATE
                )
                    .initializer("null").build()  // "AnnotationData(\"zzm\", 19)"

            // 实现抽象类的方法
            val handleFunSpec = FunSpec.builder("handle")
                .addModifiers(KModifier.PUBLIC, KModifier.OVERRIDE)
                .returns(Unit::class)
                .addStatement("println(\"handle something...\")")
                .build()

            // 实现接口方法
            val helpFunSpec = FunSpec.builder("help")
                .addModifiers(KModifier.PUBLIC, KModifier.OVERRIDE)
                .returns(Unit::class)
                .addStatement("println(\"help something...\")")
                .build()

            // 创建一个parseAnnotationData 方法
            val defaultParam = greeting?.name?.takeIf {
                it.isNotEmpty()
            } ?: "params default"

            val annotationDataStr = String.format(
                "AnnotationData(\"%s\", %d)",
                defaultParam,
                greeting?.age ?: -1
            )
            logger.warn(
                annotationDataStr
            )
            val parseAnnotationDataFunSpec = FunSpec.builder("parseAnnotationData")
                .addModifiers(KModifier.PRIVATE)
                .returns(
                    nullableAnnotationData
                )
                .addParameter(
                    ParameterSpec.builder("annoData", nullableAnnotationData)
                        .defaultValue(
                            String.format(
                                "AnnotationData(\"\"\"%s\"\"\", %d)",
                                defaultParam,
                                greeting?.age ?: -1
                            )
                        )
                        .build()
                )

                .addStatement(
                    """
                    AnnotationData("$defaultParam", age)
                    println("parseAnnotationData something...")
                    return annoData
                """.trimIndent()
                )
                .build()

            /**
             * companion object {
             *         private val name: String = "Hello, MainActivity"
             *         private val age: Int = 19
             *
             *         fun bind(activity: MainActivity) {
             *             activity.name = getIntent()
             *             activity.age = 19
             *         }
             *     }
             */
            val companionObject = TypeSpec.companionObjectBuilder()
                .addProperty(
                    PropertySpec.builder("name", String::class, KModifier.PRIVATE)
                        .initializer("\"${defaultName}\"")
                        .build()
                )
                .addProperty(
                    PropertySpec.builder("age", Int::class, KModifier.PRIVATE)
                        .initializer("$defaultAge")
                        .build()
                )
                .addFunction(FunSpec.builder("bind")
                    .addModifiers(KModifier.PUBLIC)
                    .returns(Unit::class)
                    .addParameter(
                        ParameterSpec.builder(
                            "activity", annotatedClazz.toClassName()
                        ).build()
                    )
                    .apply {
                        // 拿到 AutoWired
                        autoWireds.forEach {
                            val property = it as KSPropertyDeclaration
                            val propertyName = property.simpleName.getShortName()
                            val type = property.type.toTypeName()
                            logger.warn("propertyName = ${propertyName}, type = $type")
                            when (type) {
                                String::class.asTypeName(),
                                String::class.asTypeName().copy(
                                    nullable = true
                                ) -> {
//                                    addStatement("activity.${propertyName} = activity.intent.getStringExtra(\"${propertyName}\")")
                                    addStatement("activity.${propertyName} = $propertyName")
                                }

                                Int::class.asTypeName(),
                                Int::class.asTypeName().copy(
                                    nullable = true
                                ) -> {
                                    addStatement("activity.${propertyName} = $propertyName")
                                }

                                else -> {}
                            }
                        }
                    }
                    .build())

                .build()

            val typeSpec = TypeSpec.classBuilder("${clazzName}_generate")
                .primaryConstructor(
                    FunSpec.constructorBuilder()
                        .addParameter("name3", String::class)
                        .build()
                ).addProperty(
                    PropertySpec.builder("name3", String::class, KModifier.PRIVATE)
                        .initializer("name3")
                        .build()
                )
                .superclass(BaseAnnotationHandler::class)       // 添加super class
                .addSuperinterface(IAnnotationHelper::class)
//                .addProperty(propertySpec)
                .addProperty(dataPropertySpec)
                .addFunction(funSpec)
                .addFunction(handleFunSpec)
                .addFunction(helpFunSpec)
                .addFunction(parseAnnotationDataFunSpec)
                .addKdoc("auto generate class by Greeting processor, pls don't modify it.")
                .addType(companionObject)
                .build()

            val fileSpec = FileSpec.builder(packageName, "${clazzName}_generate")
                .addType(typeSpec)
                .build()

            fileSpec.writeTo(environment.codeGenerator, false, emptyList())
        }
        logger.warn("GreetingProcessor process end")

        return emptyList()
    }
}