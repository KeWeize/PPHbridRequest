package com.ppwang.pprequest.interfaces;

import com.ppwang.pprequest.bean.PPResponse;

/**
 * @author: vitar
 * @date: 2020/11/6
 */
public interface IPPApiListener {

    void onResponse(PPResponse response);

}
