package com.ppwang.pprequest.interfaces;

import com.ppwang.pprequest.bean.PPResponse;

/**
 * 联合请求下请求接口回调
 *
 * @author: vitar
 * @date: 2020/10/28
 */
public interface IPPApiListener {

    void onResponse(PPResponse response);

}
