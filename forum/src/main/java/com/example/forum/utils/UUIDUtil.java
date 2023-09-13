package com.example.forum.utils;


import java.util.UUID;

public class UUIDUtil {


    //生成一个标准的36位UUID
    public static String UUID_36() {
        return UUID.randomUUID().toString();
    }

    //生成一个32位的UUID
    public static String UUID_32() {
        return UUID.randomUUID().toString().replace("-","");
    }
}
