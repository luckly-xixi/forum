package com.example.forum.utils;


import org.apache.commons.codec.digest.DigestUtils;

/**
 *  用于加密的工具
 */

// 对字符串进行MD5加密
public class MD5Util {
    public static String md5(String str) {
        return DigestUtils.md5Hex(str);
    }

    //加密用户密码,str密码明文，salt扰动字符
    public static String md5salt (String str,String salt) {
        return  md5(md5(str) + salt);
    }
}
