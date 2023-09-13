package com.example.forum.controller;


import com.example.forum.common.AppResult;
import com.example.forum.exception.ApplicationException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;


//作⽤在Controller上，对控制器类的说明，tags="说明该类的作⽤，可以在前台界⾯上看到的注解"
@Api(tags = "测试类的相关接口")


@RestController //表示返回的结果是数据
@RequestMapping("/test") //定义一级映射路径
public class TestController {

    //作用在具体方法上，对Api接口的说明
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


    //@ApiParam:作⽤在⽅法中的每⼀个参数上，对参数的属性进⾏说明
    @ApiOperation("测试接口4，按接入的姓名显示你好信息")
    @PostMapping("/helloByName")
    public String helloByName(@ApiParam(value = "姓名") @RequestParam("name") String name) {
        return "hello" + name;
    }
}
