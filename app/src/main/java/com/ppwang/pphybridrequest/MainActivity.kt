package com.ppwang.pphybridrequest

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.ppwang.pphybridrequest.api.AdsEngineApi
import com.ppwang.pphybridrequest.api.core.Cmd
import com.ppwang.pphybridrequest.api.core.JavaPath
import com.ppwang.pphybridrequest.bean.BannerItem
import com.ppwang.pprequest.core.PPParamSet
import com.ppwang.pprequest.core.PPJsonBody
import com.ppwang.pprequest.core.PPRequest
import com.ppwang.pprequest.interfaces.IPPApiListener

class MainActivity : AppCompatActivity() {

    private val MAX_VALUE = 1
    private var count = MAX_VALUE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.tv_request).setOnClickListener {
            count = MAX_VALUE
            for (i in 1..count) {
                request(i)
            }
        }
    }

    private fun request(time: Int) {
        val params = createRequestParams()
        val request = PPRequest()
        val star = System.currentTimeMillis()
        request.excute(params,
            IPPApiListener {
                val end = System.currentTimeMillis()
                if (it.hasException()) {
                    for (key in it.getExceptionMap()!!.keys) {
                        val message = it.getExceptionMap()!![key]!!.getMessage()
                        Log.d("MainActivity", "第$time 次请求异常， tag:$key, message:$message")
                        break
                    }
                } else {
                    Log.d("MainActivity", "第$time 次请求，耗时: ${end - star}ms")
                }
                val list: List<BannerItem>? = it.getData(JavaPath.ADS_BANNER_BANNERLIST)
                val list2: List<BannerItem>? = it.getData(Cmd.CODE_50206)

//                Toast.makeText(
//                    this,
//                    "请求回调成功: 首页Banner： ${list?.size},  50206： ${list2?.size}",
//                    Toast.LENGTH_SHORT
//                ).show()

                count -= 1
                if (count == 0) {
                    Log.d("MainActivity", "所有请求执行结束");
                }
            }
        )

        val bean = BannerApiBean(5, 6)
        bean.setPointer(Pointer(50, 50))

    }

    /**
     * 生成联合请求对象
     */
    private fun createRequestParams(): PPParamSet {
        // 首页Banner数据
        val bannerParam = AdsEngineApi.createBannerList(0, 1)
        return PPParamSet(
            bannerParam, createParam8(), createParam50206(), createParam1012(),
            craeteParam1010(), createParam33000(), createParam21190(), createParam33(),
            createParam11806()
        )
    }

    /**
     * 请求参数
     */
    class BannerApiBean : PPJsonBody {

        private var page: Int

        private var pageSize: Int

        private var mPointer: Pointer? = null

        constructor(page: Int, pageSize: Int) : super() {
            this.page = page
            this.pageSize = pageSize
        }

        fun setPointer(pointer: Pointer) {
            this.mPointer = pointer
        }

    }

    class Pointer {

        private val map = mapOf("name" to "vitar", "company" to "批批网")

        private var pointer: Int
        private var start: Int

        constructor(pointer: Int, start: Int) {
            this.pointer = pointer
            this.start = start
        }
    }


    /**test**/
    /************************ 首页数据请求相关cmd start  */
    /**
     * 首页通透广告
     */
    private fun createParam8(): PPParamSet.PhpParam<*> {
        val value = PPParamSet.PhpParam<ArrayList<BannerItem>>("8")
        value.put("page", 1)
        return value
    }

    private fun createParam50206(): PPParamSet.PhpParam<*> {
        return PPParamSet.PhpParam<ArrayList<BannerItem>>("50206")
    }

    /**
     * 首页浮窗广告
     */
    private fun createParam1012(): PPParamSet.PhpParam<*> {
        return PPParamSet.PhpParam<ArrayList<BannerItem>>("1012")
    }

    private fun craeteParam1010(): PPParamSet.PhpParam<*> {
        return PPParamSet.PhpParam<ArrayList<BannerItem>>("1010")
    }

    private fun createParam33000(): PPParamSet.PhpParam<*> {
        val value = PPParamSet.PhpParam<ArrayList<BannerItem>>("33000")
        value.put("positionId", 1)
        return value
    }

    private fun createParam21190(): PPParamSet.PhpParam<*> {
        val value = PPParamSet.PhpParam<ArrayList<BannerItem>>("21190")
        value.put("order_by", 1)
        return value
    }

    /**
     * 获取首页背景图配置
     */
    private fun createParam33(): PPParamSet.PhpParam<*> {
        val value = PPParamSet.PhpParam<BannerItem>("33")
        return value
    }

    private fun createParam11806(): PPParamSet.PhpParam<*> {
        val value = PPParamSet.PhpParam<ArrayList<BannerItem>>("11806")
        return value
    }

}
