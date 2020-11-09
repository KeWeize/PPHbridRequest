package com.ppwang.pprequest.callback

import android.text.TextUtils
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import com.ppwang.pprequest.bean.PPResult
import com.ppwang.pprequest.core.PPHtybridValue
import com.ppwang.pprequest.exception.PPNetException
import org.json.JSONException

/**
 * @author: vitar
 * @date:   2020/10/22
 */
internal abstract class PPBaseOkGoCallback : StringCallback {

    private val mRequestListener: OnCellRequestListener

    protected val mRequestParam: PPHtybridValue.Param<*>

    protected val mGson = Gson()

    constructor(param: PPHtybridValue.Param<*>, listener: OnCellRequestListener) : super() {
        this.mRequestParam = param
        this.mRequestListener = listener
    }

    override fun onSuccess(response: Response<String>?) {
        if (response != null && response.code() == 200) {
            val json: String? = response.body()
            if (!TextUtils.isEmpty(json)) {
                try {
                    val result = parseData(json!!)
                    mRequestListener.onSuccess(result)
                } catch (e: JsonParseException) {
                    if (TextUtils.isEmpty(e.message)) {
                        mRequestListener.onFail(PPNetException.PPJsonParseException())
                    } else {
                        mRequestListener.onFail(PPNetException.PPJsonParseException(e.message!!))
                    }
                }
            } else {
                mRequestListener.onFail(PPNetException.PPUnknowException())
            }
        } else {
            // 接口请求未知异常
            mRequestListener.onFail(PPNetException.PPUnknowException())
        }
    }

    /**
     * 网络层错误
     */
    override fun onError(response: Response<String>?) {
        super.onError(response)
        // 网络层接口异常
        val code = response?.code() ?: PPNetException.CODE_UNKNOW_EXCEPTION
        val message = if (!TextUtils.isEmpty(response?.message())) {
            response!!.message()
        } else {
            PPNetException.MESSAGE_DEFAULT_HTTP_EXCEPTION
        }
        when (response?.code()) {
            in 300 until 399 -> {
                // 网络请求重定向
                mRequestListener.onFail(PPNetException.PPHttpException(code, message))
            }
            in 400 until 499 -> {
                // 处理错误码 400+
                mRequestListener.onFail(PPNetException.PPHttpException(code, message))
            }
            in 500 until 599 -> {
                // 处理错误码 500+
                mRequestListener.onFail(PPNetException.PPHttpException(code, message))
            }
            else -> {
                // 未知异常
                mRequestListener.onFail(PPNetException.PPUnknowException(code, message))
            }
        }
    }

    /**
     * 解析有效的返回数据，由于不同系统（PHP、和Java）返回规范不统一，解析业务逻辑交由具体类去实现
     * 根据目前批批业务，实现有一下两种
     * [PPJavaOkGoCallback]、 [PPPhpOkGoCallback]
     */
    @Throws(JSONException::class)
    abstract fun parseData(response: String): PPResult

    /**
     * 单个网络请求执行回调接口
     */
    internal interface OnCellRequestListener {
        /**
         * 请求接口、并解析成功回调
         */
        fun onSuccess(result: PPResult)

        /**
         * 请求失败或解析失败回调
         */
        fun onFail(exception: PPNetException)
    }

}