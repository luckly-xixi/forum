package com.example.forum.controller;


import com.example.forum.common.AppResult;
import com.example.forum.exception.ApplicationException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;



@Api(tags = "测试类的相关接口")
@RestController
@RequestMapping("/test")
public class TestController {


    @ApiOperation("测试接口1，显示字符串")
    @GetMapping("/hello")
    public String hello() {
        return "Hello forum";
    }


    @ApiOperation("测试接口2，显示异常信息")
    @GetMapping("/exception")
    public AppResult testException() throws Exception {
        throw new Exception("这是一个Exception....");
    }

    @ApiOperation("测试接口3，显示自定义异常信息")
    @GetMapping("/appException")
    public AppResult testApplicationException() {
        throw new ApplicationException("这是一个ApplicationException...");
    }



    @ApiOperation("测试接口4，按接入的姓名显示你好信息")
    @PostMapping("/helloByName")
    public String helloByName(@ApiParam(value = "姓名") @RequestParam("name") String name) {
        return "hello" + name;
    }
}
