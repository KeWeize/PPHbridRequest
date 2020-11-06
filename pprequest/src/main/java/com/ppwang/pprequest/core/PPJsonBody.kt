package com.ppwang.pprequest.core

import com.google.gson.Gson


/**
 * @author: vitar
 * @date:   2020/11/6
 */
abstract class PPJsonBody {

    companion object {
        private val mGson = Gson()
    }

    /**
     * 将自身转换为json字符串
     */
    fun generateJson(): String = mGson.toJson(this)

}