package com.ppwang.pphybridrequest.api

import com.ppwang.pphybridrequest.api.core.Cmd
import com.ppwang.pphybridrequest.bean.BannerItem
import com.ppwang.pprequest.core.PPParamSet


/**
 * @author: vitar
 * @date:   2020/11/6
 */
object MainHomeApi {

    /**
     * 请求首页豆腐块
     */
    fun createCmd50206(): PPParamSet.Param<*> {
        return PPParamSet.PhpParam<ArrayList<BannerItem>>(Cmd.CODE_50206)
    }

}