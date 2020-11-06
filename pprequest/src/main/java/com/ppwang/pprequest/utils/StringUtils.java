package com.ppwang.pprequest.utils;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

public class StringUtils {

    /**
     * 十进制转十六进制对应表
     */
    private static final char HEX_DIGITS[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    /**
     * 字节转十六进制
     *
     * @param bytes
     * @return
     */
    public static String toHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++) {
            sb.append(HEX_DIGITS[(bytes[i] & 0xf0) >>> 4]);
            sb.append(HEX_DIGITS[bytes[i] & 0x0f]);
        }
        return sb.toString();
    }

    /**
     * MD5编码，大写，使用默认charset(UTF-8)
     */
    public static String md5(String str) {
        return md5(str.getBytes());
    }

    /**
     * MD5编码，小写，使用默认charset(UTF-8)
     */
    public static String md5Lcase(String str) {
        String output = md5(str.getBytes());
        return output == null ? null : output.toLowerCase(Locale.getDefault());
    }

    /**
     * MD5编码，大写，使用特定charset
     *
     * @param str
     * @param charsetName
     * @return
     */
    public static String md5(String str, String charsetName) {
        try {
            return md5(str.getBytes(charsetName));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * MD5编码，小写，使用特定charset
     *
     * @param str
     * @param charsetName
     * @return
     */
    public static String md5Lcase(String str, String charsetName) {
        String output = md5(str, charsetName);
        return output == null ? null : output.toLowerCase(Locale.getDefault());
    }

    /**
     * MD5编码，大写
     *
     * @param bytes
     * @return
     */
    public static String md5(byte[] bytes) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(bytes);
            return toHexString(digest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * MD5编码，小写
     *
     * @param bytes
     * @return
     */
    public static String md5Lcase(byte[] bytes) {
        String output = md5(bytes);
        return output == null ? null : output.toLowerCase(Locale.getDefault());
    }

    /**
     * base64编码
     */
    public static String base64Encode(String text) {
        String result = "";
        try {
            if (text != null) {
                byte[] textByte = text.getBytes("UTF-8");
                result = Base64.encodeToString(textByte, Base64.NO_WRAP);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

}
