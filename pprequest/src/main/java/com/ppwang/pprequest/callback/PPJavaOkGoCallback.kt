package com.ppwang.pprequest.callback

import com.ppwang.pprequest.bean.PPResult
import com.ppwang.pprequest.core.PPParamSet
import org.json.JSONObject


/**
 * @author: vitar
 * @date:   2020/11/2
 */
internal class PPJavaOkGoCallback : PPBaseOkGoCallback {

    companion object {

        /**
         * Java 接口规范 code 返回字段 key
         */
        private const val KEY_CODE = "code"

        /**
         * Java 接口规范 message 返回字段 key
         */
        private const val KEY_MESSAGE = "message"

        /**
         * Java 接口规范 data 返回字段 key
         */
        private const val KEY_DATA = "data"
    }

    constructor(
        param: PPParamSet.Param<*>,
        listener: OnCellRequestListener
    ) : super(param, listener)

    override fun parseData(response: String): PPResult {
        try {
            val jsonObject = JSONObject(response)
            val code: Int = jsonObject.getInt(KEY_CODE)
            var message = ""
            if (jsonObject.has(KEY_MESSAGE)) {
                message = jsonObject.getString(KEY_MESSAGE)
            }
            val result = PPResult(code, message)
            if (code == 200) {
                val dataJsonContent = jsonObject.getString(KEY_DATA)
                if (mRequestParam.type != null) {
                    // 使用 type 解析响应数据
                    result.data = mGson.fromJson(dataJsonContent, mRequestParam.type)
                }
            }
            return result
        } catch (e: Exception) {
            // 直接抛出异常，由于
            throw e
        }
    }

}