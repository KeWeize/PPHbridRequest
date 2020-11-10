package com.ppwang.pprequest.runnabl

import android.os.Handler
import android.os.Looper
import com.google.gson.Gson
import com.lzy.okgo.OkGo
import com.ppwang.pprequest.bean.PPResponse
import com.ppwang.pprequest.bean.PPResult
import com.ppwang.pprequest.callback.PPBaseOkGoCallback
import com.ppwang.pprequest.callback.PPJavaOkGoCallback
import com.ppwang.pprequest.callback.PPPhpOkGoCallback
import com.ppwang.pprequest.constact.PPApiUrlConfig
import com.ppwang.pprequest.core.PPParamSet
import com.ppwang.pprequest.exception.PPNetException
import com.ppwang.pprequest.utils.SignUtils
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit


/**
 * @author: vitar
 * @date:   2020/10/30
 *
 * 执行联合请求任务，根据 [PPParamSet] 对象遍历发起多个异步请求
 */
internal class PPRequestRunnable : Runnable {

    companion object {
        private val mGson = Gson()
    }

    /**
     * 联合请求信息集
     */
    private val mHtybridValue: PPParamSet

    /**
     * 当前联合请求任务返回数据封装对象
     */
    private val mResponse = PPResponse()

    /**
     * 同步多个请求线程的CountDownLatch对象
     */
    private val mLatch: CountDownLatch

    /**
     * 联合请求任务执行回调对象
     */
    private var mOnHtybridResponseListener: OnHtybridResponseListener? = null

    /**
     * 绑定了主线程Looper实例的Handler对象，用于将回调任务切换到主线程执行
     */
    private val mMainThreadHandler = Handler(Looper.getMainLooper())

    /**
     * 标记当前任务的状态
     */
    private var mStatus: STATUS = STATUS.RUNNING

    constructor(htybridValue: PPParamSet) {
        this.mHtybridValue = htybridValue
        mLatch = CountDownLatch(mHtybridValue.mParamList.size)
    }

    /**
     * 注入任务执行回调
     */
    internal fun setOnHtybridRequestListener(l: OnHtybridResponseListener?) {
        this.mOnHtybridResponseListener = l
    }

    override fun run() {
        mStatus = STATUS.RUNNING
        for (value in mHtybridValue.mParamList) {
            if (value is PPParamSet.JavaParam) {
                // 发起 Java 请求
                requestJavaApi(value)
            }
            if (value is PPParamSet.PhpParam) {
                // 发起 PHP 请求
                requestPhpApi(value)
            }
        }
        try {
            // 堵塞线程，直到所有请求线程执行完成（目前固定超时时间 10s，可考虑提供配置类动态配置请求任务的超时时间）
            mLatch.await(10 * 1000, TimeUnit.MILLISECONDS)
            if (mStatus == STATUS.COMPLETE) {
                // 正常请求执行完成情况下，切换到主线程执行回调逻辑
                mMainThreadHandler.post {
                    mOnHtybridResponseListener?.response(mResponse)
                }
            } else if (mStatus == STATUS.RUNNING) {
                mMainThreadHandler.post {
                    // 如果状态仍为运行中，则说明请求超时了
                    val response = PPResponse()
                    response.addNetException("-2", PPNetException(-2, "请求超时"))
                    mOnHtybridResponseListener?.response(response)
                }
            }
        } catch (e: InterruptedException) {
            mMainThreadHandler.post {
                // 如果状态仍为运行中，则说明请求超时了
                val response = PPResponse()
                response.addNetException("-2", PPNetException(-2, "请求超时"))
                mOnHtybridResponseListener?.response(response)
            }
        }
    }

    /**
     * 发起Java接口请求
     */
    private fun requestJavaApi(param: PPParamSet.JavaParam<*>) {
        val httpParams: HashMap<String, String> = param.mHttpParams
        // 当前接口请求执行回调
        val cellRequestListener = object : PPBaseOkGoCallback.OnCellRequestListener {
            override fun onSuccess(result: PPResult) {
                mResponse.addJavaResultData(param.getTag(), result)
                onLatchCountDownByApiCallback()
            }

            override fun onFail(exception: PPNetException) {
                mResponse.addNetException(param.getTag(), exception)
                onLatchCountDownByApiCallback()
            }
        }
        // 生成伪签名验证参数
        val signParams = generateFakeSignParams()
        // 构建请求api url
        val apiUrlSb = StringBuilder(PPApiUrlConfig.HOST_JAVA)
            .append(param.path)
        if (signParams.containsKey("parameters")) {
            if (!apiUrlSb.contains("?")) {
                apiUrlSb.append("?")
            } else {
                apiUrlSb.append("&")
            }
            apiUrlSb.append("parameters=").append(signParams["parameters"])
        }
        if (signParams.containsKey("sign")) {
            if (!apiUrlSb.contains("?")) {
                apiUrlSb.append("?")
            } else {
                apiUrlSb.append("&")
            }
            apiUrlSb.append("sign=").append(signParams["sign"])
        }
        val apiUrl = apiUrlSb.toString()
        when (param.method) {
            // 发起 GET 请求
            PPParamSet.Method.GET -> {
                if (param.mJsonBean != null) {
                    throw IllegalArgumentException("Java请求不允许在GET方法下设置JsonBody请求参数")
                }
                OkGo.get<String>(apiUrl)
                    .tag(this)
                    .params(httpParams)
                    .execute(PPJavaOkGoCallback(param, cellRequestListener))
            }
            // 发起 POST 请求
            PPParamSet.Method.POST -> {
                var requestJson = ""
                if (param.mJsonBean != null) {
                    // 如果使用 JsonBody 请求体，则直接将参数对象转换为 json
                    requestJson = param.mJsonBean!!.generateJson()
                } else {
                    // 使用普通参数集合，将普通参数集转为 json
                    val httpParams: HashMap<*, *> = param.mHttpParams
                    requestJson = mGson.toJson(listOf(httpParams))
                }
                OkGo.post<String>(apiUrl)
                    .tag(this)
                    .upJson(requestJson)
                    .execute(PPJavaOkGoCallback(param, cellRequestListener))
            }
        }
    }

    /**
     * 发起Php接口请求
     */
    private fun requestPhpApi(param: PPParamSet.PhpParam<*>) {
        if (!param.mHttpParams.containsKey("cmd")) {
            param.mHttpParams["cmd"] = param.cmd
        }
        // 生成请求参数 json 字符串
        var requestJson = ""
        if (param.mJsonBean != null) {
            // 如果使用 JsonBody 请求体，则直接将参数对象转换为 json
            requestJson = param.mJsonBean!!.generateJson()
        } else {
            // 使用普通参数集合，将普通参数集转为 json
            val httpParams: HashMap<String, *> = param.mHttpParams
            requestJson = mGson.toJson(listOf(httpParams))
        }
        // 当前接口请求执行回调
        val cellRequestListener = object : PPBaseOkGoCallback.OnCellRequestListener {
            override fun onSuccess(result: PPResult) {
                mResponse.addPhpResultData(param.getTag(), result)
                onLatchCountDownByApiCallback()
            }

            override fun onFail(exception: PPNetException) {
                mResponse.addNetException(param.getTag(), exception)
                onLatchCountDownByApiCallback()
            }
        }
        val url = PPApiUrlConfig.HOST_PHP + "index.php?c=goods"
        when (param.method) {
            // 执行 GET 请求
            PPParamSet.Method.GET -> {
                OkGo.get<String>(url)
                    .tag(this)
                    .params("json", requestJson)
                    .execute(PPPhpOkGoCallback(param, cellRequestListener))
            }
            PPParamSet.Method.POST -> {
                OkGo.post<String>(url)
                    .tag(this)
                    .params("json", requestJson)
                    .execute(PPPhpOkGoCallback(param, cellRequestListener))
            }
        }
    }

    /**
     * 生成签名校验
     */
    private fun generateFakeSignParams(): Map<String, String> {
        val signParams = TreeMap<Any, Any>()
        signParams["timeStream"] = System.currentTimeMillis().toString()
        val parameters = SignUtils.createParamters(signParams)
        val sign = SignUtils.createSign(signParams)
        return mapOf("parameters" to parameters, "sign" to sign)
    }

    /**
     * 单个请求回调成功，执行 CountDownLatch 自减逻辑
     */
    private fun onLatchCountDownByApiCallback() {
        if (mLatch.count == 1L) {
            // 正常请求回调结束，将状态修改为完成
            mStatus = STATUS.COMPLETE
        }
        mLatch.countDown()
    }

    /**
     * 取消当前的请求任务
     */
    internal fun cancel() {
        OkGo.getInstance().cancelTag(this)
        // 将当前状态修改为主动取消
        mStatus = STATUS.CANCEL
        while (mLatch.count > 0) {
            mLatch.countDown()
        }
    }

    /**
     * 多请求任务执行回调
     */
    internal interface OnHtybridResponseListener {
        fun response(response: PPResponse)
    }

    /**
     * 枚举类标记当前联合任务的执行状态
     */
    enum class STATUS {

        /**
         * 运行中
         */
        RUNNING,

        /**
         * 主动取消
         */
        CANCEL,

        /**
         * 通过各个请求回调正常执行完任务
         */
        COMPLETE
    }

}