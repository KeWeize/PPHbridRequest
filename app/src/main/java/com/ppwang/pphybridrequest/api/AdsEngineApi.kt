package com.ppwang.pphybridrequest.api

import com.ppwang.pphybridrequest.api.core.JavaPath
import com.ppwang.pphybridrequest.bean.BannerItem
import com.ppwang.pprequest.core.PPParamSet


/**
 * 广告系统 api
 *
 * @author: vitar
 * @date:   2020/11/6
 */
object AdsEngineApi {

    /**
     * 请求广告
     */
    fun createBannerList(releaseType: Int, page: Int): PPParamSet.Param<*> {
        val param = PPParamSet.JavaParam<ArrayList<BannerItem>>(JavaPath.ADS_BANNER_BANNERLIST)
        param.put("releaseType", releaseType)
        param.put("page", page)
        return param;
    }

}