package com.hele.base.utils

import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty0
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.staticFunctions
import kotlin.reflect.full.staticProperties
import kotlin.reflect.full.superclasses

object ReflectUtil {
    fun <T> callStaticMethod(clazz: KClass<*>, methodName: String, args: Array<Any>? = null): T? {
        return kotlin.runCatching {
            val staticMethod = findStaticFunction(clazz, methodName) ?: kotlin.run {
                var method: KFunction<*>? = null
                for (superClazz in clazz.superclasses) {
                    method = findStaticFunction(superClazz, methodName)
                    if (method != null) {
                        break
                    }
                }
                method
            }
            staticMethod?.call(args) as? T
        }.getOrNull()
    }

    fun <T> callMethod(obj: Any, methodName: String, args: Array<Any>? = null): T? {
        return kotlin.runCatching {
            // memberFunctions return all non static functions of this and all super clazz.
            obj::class.memberFunctions.firstOrNull {
                it.name == methodName
            }?.call(obj, args) as? T
        }.getOrNull()
    }

    fun <T> getStaticField(clazz: KClass<*>, fieldName: String): T? {
        return kotlin.runCatching {
            val staticField = findStaticField(clazz, fieldName) ?: kotlin.run {
                var field: KProperty0<*>? = null
                for (superClazz in clazz.superclasses) {
                    field = findStaticField(superClazz, fieldName)
                    if (field != null) {
                        break
                    }
                }
                field
            }

            staticField?.getter?.call()?.let { it as T }
        }.getOrNull()
    }

    fun <T> getField(obj: Any, fieldName: String): T? {
        return kotlin.runCatching {
            obj::class.memberProperties.firstOrNull {
                it.name == fieldName
            }?.getter?.call(obj) as? T
        }.getOrNull()
    }

    fun <T> setStaticField(clazz: KClass<*>, fieldName: String, value: T) {
        kotlin.runCatching {
            val stateField = findStaticField(clazz, fieldName) ?: kotlin.run {
                var field: KProperty0<*>? = null
                for (superClazz in clazz.superclasses) {
                    field = findStaticField(superClazz, fieldName)
                    if (field != null) break
                }
                field
            }
            stateField?.getter?.call(value)
        }.getOrNull()
    }

    fun <T> setField(obj: Any, fieldName: String, value: T) {
        kotlin.runCatching {
            obj::class.memberProperties.firstOrNull {
                it.name == fieldName
            }?.call(obj, value)
        }.getOrNull()
    }

    private fun findStaticField(
        clazz: KClass<*>, fieldName: String
    ) = clazz.staticProperties.firstOrNull { it.name == fieldName }


    /**
     * clazz.staticFunctions: return all static functions of this clazz
     */
    private fun findStaticFunction(
        clazz: KClass<*>, methodName: String
    ) = clazz.staticFunctions.firstOrNull {
        it.name == methodName
    }
}