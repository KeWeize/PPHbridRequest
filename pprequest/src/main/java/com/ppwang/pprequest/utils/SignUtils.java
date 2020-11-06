package com.ppwang.pprequest.utils;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

/**
 * ================================================
 * 版权所有: 广州市批来批往信息科技有限公司，并保留所有权利。
 * 网站地址: https://www.ppwang.com
 * ================================================
 * <p>
 * 创建者: 何潮 <hechao5909@dingtalk.com>
 * 日期: 2020/6/3 14:39
 */
public class SignUtils {

    private static final String ApiKey = "51d8bd7959cafcf4bd92a8db8dd15b34";

    /**
     * 生成参数字符串
     * @param packageParams 参数 (按参数名称a-z排序,遇到空值的参数不参加签名)
     * @return
     */
    public static String createParamters(SortedMap<Object, Object> packageParams) {
        StringBuffer sb = new StringBuffer();
        Set es = packageParams.entrySet();
        Iterator it = es.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            if (entry.getKey() == null || entry.getValue() == null) {
                continue;
            }
            String k = entry.getKey().toString();
            String v = entry.getValue().toString();
            if (null != v && !"".equals(v) && !"sign".equals(k) && !"key".equals(k)) {//遇到空值的参数不参加签名
                sb.append(k + "=" + v + "&");
            }
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length()-1);
        }
        return sb.toString();
    }

    /**
     * 生成签名
     * @param packageParams 参数 (按参数名称a-z排序,遇到空值的参数不参加签名)
     * @return
     */
    public static String createSign(SortedMap<Object, Object> packageParams) {
        StringBuffer sb = new StringBuffer();
        sb.append(SignUtils.createParamters(packageParams));
        sb.append("&key=" + ApiKey);
        String encodedText = sb.toString();
        return StringUtils.md5Lcase(encodedText);
    }

    /**
     * 检验签名是否正确
     * @param packageParams 参数
     * @return boolean 校验是否成功
     */
    public static boolean checkSign(SortedMap<Object, Object> packageParams) {

        String mysign = createSign(packageParams).toString();//生成签名
        String tenpaySign = packageParams.get("sign").toString();//接口签名
        return tenpaySign.equals(mysign);//判断生成签名和接口中签名是否一致
    }

}
