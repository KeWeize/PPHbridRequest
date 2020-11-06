package com.ppwang.pprequest.callback

import com.ppwang.pprequest.bean.PPResult
import com.ppwang.pprequest.core.PPHtybridValue
import com.ppwang.pprequest.exception.PPNetException
import org.json.JSONObject


/**
 * @author: vitar
 * @date:   2020/11/2
 */
internal class PPPhpOkGoCallback : PPBaseOkGoCallback {

    companion object {

        /**
         * PHP 接口规范 code 返回字段 key
         */
        private const val KEY_CODE = "statusCode"

        /**
         * PHP 接口规范 message 返回字段 key
         */
        private const val KEY_MESSAGE = "errorMsg"

        /**
         * PHP 接口规范 data 返回字段 key
         */
        private const val KEY_DATA = "data"
    }

    constructor(param: PPHtybridValue.Param, listener: OnCellRequestListener) : super(
        param,
        listener
    )

    override fun parseData(response: String): PPResult {
        try {
            val cmd = mRequestParam.getTag()
            var jsonObject = JSONObject(response)
            if (jsonObject.has(cmd)) {
                jsonObject = jsonObject.getJSONObject(cmd)
                val code: Int = jsonObject.getInt(KEY_CODE)
                var message = ""
                if (jsonObject.has(KEY_MESSAGE)) {
                    message = jsonObject.getString(KEY_MESSAGE)
                }
                val result = PPResult(code, message)
                if (code == 0) {
                    val dataJsonContent = jsonObject.getString(KEY_DATA)
                    if (mRequestParam.clazz != null) {
                        // 使用 class 解析响应数据
                        result.data = mGson.fromJson(dataJsonContent, mRequestParam.clazz)
                    } else if (mRequestParam.type != null) {
                        // 使用 type 解析响应数据
                        result.data = mGson.fromJson(dataJsonContent, mRequestParam.type)
                    }
                }
                return result
            }
            return PPResult(
                PPNetException.CODE_UNKNOW_EXCEPTION,
                PPNetException.MESSAGE_UNKNOW_EXCEPTION
            )
        } catch (e: Exception) {
            // 直接抛出异常，由于
            throw e
        }
    }


}