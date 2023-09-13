package com.example.forum.controller;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController //表示返回的结果是数据
@RequestMapping("/test") //定义一级映射路径
public class TestController {

    @RequestMapping("/hello")
    public String hello() {
        return "Hello forum";
    }
}
