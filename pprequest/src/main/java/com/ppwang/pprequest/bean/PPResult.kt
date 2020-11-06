package com.ppwang.pprequest.bean

/**
 * 接口请求返回对象
 */
internal class PPResult {

    var code: Int = -1

    var message: String = ""

    var data: Any? = null

    constructor(code: Int, message: String) : this(code, message, null)

    constructor(code: Int, message: String, data: Any?) {
        this.code = code
        this.message = message
        this.data = data
    }

}