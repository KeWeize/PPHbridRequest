package com.ppwang.pprequest.exception


/**
 * @author: vitar
 * @date:   2020/11/2
 */
open class PPNetException {

    companion object {

        /**
         * 未知错误
         */
        const val CODE_UNKNOW_EXCEPTION = -1
        const val MESSAGE_UNKNOW_EXCEPTION = "未知错误"

        /**
         * Json 解析异常
         */
        const val CODE_JSON_PARSE_EXCEPTION = -10002
        const val MESSAGE_JSON_PARSE_EXCEPTION = "Json解析错误"

        /**
         * 网络层错误默认文案
         */
        const val MESSAGE_DEFAULT_HTTP_EXCEPTION = "网络层错误"

    }

    private val code: Int
    private val message: String

    constructor(code: Int, message: String) {
        this.code = code
        this.message = message
    }

    fun getCode() = code

    fun getMessage() = message

    /**
     * Json 解析异常
     */
    class PPJsonParseException : PPNetException {

        constructor() : this(MESSAGE_JSON_PARSE_EXCEPTION)

        constructor(message: String) : super(CODE_JSON_PARSE_EXCEPTION, message)
    }

    /**
     * 未知错误
     */
    class PPUnknowException : PPNetException {

        constructor() : this(CODE_UNKNOW_EXCEPTION, MESSAGE_UNKNOW_EXCEPTION)

        constructor(code: Int, message: String) : super(code, message)
    }

    /**
     * 网络请求错误
     * 300..399 : 网络重定向错误
     * 400..499 ：网络请求失败
     * 500..599 ：服务器错误
     */
    class PPHttpException(code: Int, message: String) : PPNetException(code, message)

    /**
     * 批批接口业务异常
     */
    class PPApiException(code: Int, message: String) : PPNetException(code, message)

}