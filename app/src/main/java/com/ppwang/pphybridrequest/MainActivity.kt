package com.ppwang.pphybridrequest

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ppwang.pphybridrequest.api.AdsEngineApi
import com.ppwang.pphybridrequest.api.MainHomeApi
import com.ppwang.pphybridrequest.api.core.Cmd
import com.ppwang.pphybridrequest.api.core.JavaPath
import com.ppwang.pphybridrequest.bean.BannerItem
import com.ppwang.pprequest.core.PPHtybridValue
import com.ppwang.pprequest.core.PPRequest
import com.ppwang.pprequest.interfaces.IPPApiListener

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.tv_request).setOnClickListener {
            request()
        }
    }

    private fun request() {
        val params = createRequestParams()
        val request = PPRequest()
        request.excute(params,
            IPPApiListener {
                if (it.hasException()) {
                    Toast.makeText(this, "请求异常", Toast.LENGTH_SHORT).show()
                }
                val list: List<BannerItem>? = it.getData(JavaPath.ADS_BANNER_BANNERLIST)
                val list2: List<BannerItem>? = it.getData(Cmd.CODE_50206)

                Toast.makeText(
                    this,
                    "请求回调成功: 首页Banner： ${list?.size},  50206： ${list2?.size}",
                    Toast.LENGTH_SHORT
                ).show()

            })
    }

    /**
     * 生成联合请求对象
     */
    private fun createRequestParams(): PPHtybridValue {
        // 首页Banner数据
        val bannerParam = AdsEngineApi.createBannerList(0, 1)
        // 豆腐块
        val phpParam = MainHomeApi.createCmd50206()
        return PPHtybridValue(bannerParam, phpParam)
    }

}
