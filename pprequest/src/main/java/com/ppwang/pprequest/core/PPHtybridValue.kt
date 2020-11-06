package com.ppwang.pprequest.core

import com.ppwang.pprequest.core.PPHtybridValue.*
import java.lang.reflect.Type

/**
 * @author: vitar
 * @date:   2020/10/30
 *
 * 联合请求信息集，内部包含多个 [Param] 请求信息，根据目前业务需求具体实现有以下：
 * 1. Java 接口请求 [JavaParam]
 * 2. PHP 接口请求 [PhpParam]
 */
class PPHtybridValue {

    internal val mParamList: ArrayList<Param> = ArrayList(4)

    constructor(vararg params: Param) {
        mParamList.addAll(params)
    }

    /**
     * 添加请求信息
     */
    fun addParam(param: Param) {
        if (mParamList.contains(param)) {
            return
        }
        mParamList.add(param)
    }

    /**
     * 包装单个请求信息
     */
    abstract class Param {

        /**
         * 请求方法，不覆盖默认GET请求
         */
        internal var method = Method.GET

        /**
         * data 解析类型
         */
        internal var clazz: Class<*>? = null

        /**
         * data 解析类型，通常用于 List 携带泛型数据
         */
        internal var type: Type? = null

        /**
         * 网络请求参数
         */
        internal val mHttpParams: HashMap<String, String> = HashMap()

        constructor(clazz: Class<*>?) {
            this.clazz = clazz
        }

        constructor(type: Type?) {
            this.type = type
        }

        /**
         * 由子类具体实现，需要返回对应的标签字段，作为返回数据的 key 值
         */
        internal abstract fun getTag(): String

        /**
         * 注入请求方法，目前支持 GET、POST。后续看业务需要新增
         */
        fun method(method: Method): Param {
            this.method = method
            return this
        }

        fun put(paramMap: Map<String, String>): Param {
            mHttpParams.putAll(paramMap)
            return this
        }

        fun put(key: String, value: String): Param {
            mHttpParams[key] = value
            return this
        }

        fun put(key: String, value: Int): Param {
            mHttpParams[key] = value.toString()
            return this
        }

        fun put(key: String, value: Long): Param {
            mHttpParams[key] = value.toString()
            return this
        }

        fun put(key: String, value: Float): Param {
            mHttpParams[key] = value.toString()
            return this
        }

        fun put(key: String, value: Double): Param {
            mHttpParams[key] = value.toString()
            return this
        }

        fun put(key: String, value: Boolean): Param {
            mHttpParams[key] = value.toString()
            return this
        }

        fun put(key: String, value: Char): Param {
            mHttpParams[key] = value.toString()
            return this
        }
    }

    /**
     * Java 接口请求信息类
     */
    class JavaParam : Param {

        /**
         * 请求路径，同时也作为Java接口请求的Tag使用
         */
        internal val path: String

        constructor(path: String, clazz: Class<*>?) : super(clazz) {
            this.path = path
        }

        constructor(path: String, type: Type?) : super(type) {
            this.path = path
        }

        override fun getTag() = path

    }

    /**
     * PHP 接口请求信息类
     */
    class PhpParam : Param {

        /**
         * php 请求 cmd，同时也作为 phpt 接口请求的Tag使用，用于缓存返回数据
         */
        internal val cmd: String

        constructor(cmd: String, clazz: Class<*>?) : super(clazz) {
            this.cmd = cmd
        }

        constructor(cmd: String, type: Type?) : super(type) {
            this.cmd = cmd
        }

        override fun getTag(): String = cmd
    }

    /**
     * 网络请求方法
     */
    enum class Method {
        GET, POST
    }

}