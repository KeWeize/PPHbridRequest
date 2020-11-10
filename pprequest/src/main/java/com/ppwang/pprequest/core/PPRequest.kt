package com.ppwang.pprequest.core

import com.ppwang.pprequest.bean.PPResponse
import com.ppwang.pprequest.interfaces.IPPApiListener
import com.ppwang.pprequest.runnabl.PPRequestRunnable
import java.util.*
import java.util.concurrent.SynchronousQueue
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger


/**
 * @author: vitar
 * @date:   2020/10/28
 */
class PPRequest {

    companion object {

        /**
         * 线程池核心线程数量
         */
        private const val CORE_POOL_SIZE = 2

        /**
         * 最大线程数
         */
        private const val MAXIMUM_POOL_SIZE = Int.MAX_VALUE

        /**
         * 普通线程保活时间（秒）
         */
        private const val KEEP_ALIVE_SECONDS = 5L

        /**
         * 网络请求共用线程池
         */
        private val mThreadPoolExecutor: ThreadPoolExecutor = ThreadPoolExecutor(
            CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_SECONDS,
            TimeUnit.SECONDS, SynchronousQueue<Runnable>(), object : ThreadFactory {
                val mCount = AtomicInteger(1)
                override fun newThread(r: Runnable): Thread {
                    return Thread(r, "PPRequestTask #" + mCount.getAndIncrement())
                }
            }
        )
    }

    private val mTask: LinkedList<PPRequestRunnable> = LinkedList()

    /**
     * 执行请求逻辑
     */
    fun excute(params: PPParamSet, listener: IPPApiListener) {

        val task = PPRequestRunnable(params)
        task.setOnHtybridRequestListener(object :
            PPRequestRunnable.OnHtybridResponseListener {
            override fun response(response: PPResponse) {
                listener.onResponse(response)
                mTask.remove(task)
            }
        })
        mThreadPoolExecutor.execute(task)
        mTask.add(task)
    }

    /**
     * 取消请求任务
     */
    fun cancel() {
        for (task in mTask) {
            // 遍历取消正在请求中的任务
            task.cancel()
            mThreadPoolExecutor.remove(task)
        }
    }

}