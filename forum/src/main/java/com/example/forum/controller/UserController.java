package com.example.forum.controller;


import com.example.forum.common.AppResult;
import com.example.forum.common.ResultCode;
import com.example.forum.model.User;
import com.example.forum.services.IUserService;
import com.example.forum.utils.MD5Util;
import com.example.forum.utils.UUIDUtil;
import com.sun.istack.internal.NotNull;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(tags = "用户接口")
@Slf4j  //日志
@RestController //这是一个返回数据的Controller
@RequestMapping("/user") //路由映射
public class UserController {

    @Resource
    private IUserService userService;

    @ApiOperation("用户注册")
    @PostMapping("/register")
    public AppResult register(@ApiParam("用户名") @RequestParam("username") @NotNull String username,
                              @ApiParam("昵称") @RequestParam("nickname") @NotNull String nickname,
                              @ApiParam("密码") @RequestParam("password") @NotNull String password,
                              @ApiParam("重复密码") @RequestParam("passwordRepeat") @NotNull String passwordRepeat) {
//       1. 非空校验
//        if(StringUtil.isEmpty(username) ||
//        StringUtil.isEmpty(nickname) ||
//        StringUtil.isEmpty(password) ||
//        StringUtil.isEmpty(passwordRepeat)) {
//            return AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE);
//        }

    //2. 校验密码与重复密码是否相同
        if(!password.equals(passwordRepeat)) {
            log.warn(ResultCode.FAILED_TWO_PWD_NOT_SAME.toString());
            //返回错误信息
            return AppResult.failed(ResultCode.FAILED_TWO_PWD_NOT_SAME);
        }

    //3. 准备数据
        User user =new User();
        user.setUsername(username);
        user.setNickname(nickname);
        //处理密码
        //生成盐
        String salt = UUIDUtil.UUID_32();
        //生成密码的密文
        String encryptPassword = MD5Util.md5salt(password,salt);

        user.setPassword(encryptPassword);
        user.setSalt(salt);

        //4. 调用Service层
        userService.createNormalUser(user);
        //5. 返回成功
        return AppResult.success();
    }

}
