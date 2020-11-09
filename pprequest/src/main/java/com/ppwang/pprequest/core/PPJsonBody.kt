package com.ppwang.pprequest.core

import com.google.gson.Gson


/**
 * @author: vitar
 * @date:   2020/11/6
 */
abstract class PPJsonBody {

    /**
     * Php 请求需要额外添加 cmd 参数
     */
    internal var cmd: String? = null

    companion object {
        private val mGson = Gson()
    }

    /**
     * 将自身转换为json字符串
     */
    internal fun generateJson(): String = mGson.toJson(this)

}