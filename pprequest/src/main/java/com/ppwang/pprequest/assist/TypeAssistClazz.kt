package com.ppwang.pprequest.assist

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type


/**
 * @author: vitar
 * @date:   2020/11/10
 */
internal abstract class TypeAssistClazz<T> : ParameterizedType {

    override fun getRawType(): Type {
        return getActualTypeArguments()[0]
    }

    override fun getOwnerType(): Type? {
        return null
    }

    override fun getActualTypeArguments(): Array<Type> {
        val clz: Class<*> = this.javaClass
        val superclass =
            clz.genericSuperclass //getGenericSuperclass()获得带有泛型的父类

        if (superclass is Class<*>) {
            throw RuntimeException("Missing type parameter.")
        }
        val parameterized =
            superclass as ParameterizedType?
        return parameterized!!.actualTypeArguments
    }


}