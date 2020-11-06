package com.ppwang.pprequest.bean

import android.text.TextUtils
import com.ppwang.pprequest.exception.PPNetException


/**
 * @author: vitar
 * @date:   2020/10/30
 */
class PPResponse {

    /**
     * 用于存储联合请求响应数据
     */
    private var mResponseData: HashMap<String, Any>? = null

    /**
     * 缓存当次联合请求中存在的错误信息
     */
    private var mExceptionData: HashMap<String, PPNetException>? = null

    /**
     * 添加Java接口返回数据
     */
    internal fun addJavaResultData(tag: String, result: PPResult) {
        synchronized(this) {
            if (result.code != 200) {
                // 接口业务异常
                addNetException(tag, PPNetException.PPApiException(result.code, result.message))
            } else if (result.data != null) {
                addData(tag, result.data)
            }
        }
    }

    /**
     * 添加Java接口返回数据
     */
    internal fun addPhpResultData(tag: String, result: PPResult) {
        synchronized(this) {
            if (result.code != 0 && result.code != 200) {
                // 接口业务异常
                addNetException(tag, PPNetException.PPApiException(result.code, result.message))
            }
            if (result.code == 0 && result.data != null) {
                addData(tag, result.data)
            }
        }
    }

    /**
     * 缓存接口请求异常信息
     */
    internal fun addNetException(tag: String, exception: PPNetException?) {
        synchronized(this) {
            if (!TextUtils.isEmpty(tag) && exception != null) {
                if (mExceptionData == null) {
                    mExceptionData = hashMapOf(tag to exception)
                } else {
                    mExceptionData!![tag] = exception
                }
            }
        }
    }

    /**
     * 将联合请求结果放入当前对象中
     */
    private fun addData(key: String, value: Any?) {
        if (value == null) {
            return
        }
        if (mResponseData == null) {
            mResponseData = HashMap(4)
        }
        mResponseData!![key] = value
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getData(tag: String): T? {
        if (mResponseData == null || !mResponseData!!.containsKey(tag)) {
            return null
        }
        var data: T? = null
        try {
            data = mResponseData!![tag] as T?
        } catch (ignore: ClassCastException) {
        }
        return data
    }

    /**
     * 联合请求是否存在异常
     */
    fun hasException(): Boolean = mExceptionData != null && mExceptionData!!.size > 0

}