package com.example.forum.controller;


import com.example.forum.common.AppResult;
import com.example.forum.common.ResultCode;
import com.example.forum.config.AppConfig;
import com.example.forum.model.User;
import com.example.forum.services.IUserService;
import com.example.forum.utils.MD5Util;
import com.example.forum.utils.StringUtil;
import com.example.forum.utils.UUIDUtil;
import com.sun.istack.internal.NotNull;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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




    @ApiOperation("用户登录")
    @PostMapping("/login")
    public AppResult login(HttpServletRequest request,
                           @ApiParam("用户名") @RequestParam("username") @NotNull String username,
                           @ApiParam("密码") @RequestParam("password") @NotNull String password) {
//        1. 调用Service中的登陆方法
            User user = userService.login(username,password);
            if(user == null) {
                log.warn(ResultCode.FAILED_LOGIN.toString());
                return AppResult.failed(ResultCode.FAILED_LOGIN);
            }
//        2.如果登录成功把User对象设置到Session作用域中
            HttpSession session = request.getSession(true);
            session.setAttribute(AppConfig.USER_SESSION,user);
//        3.返回结果
        return AppResult.success();
    }


    @ApiOperation("获取用户信息")
    @GetMapping("/info")
    public AppResult<User> getUserInfo(HttpServletRequest request,
                                       @ApiParam("id") @RequestParam(value = "id",required = false)Long id) {
        //定义全局变量
        User user = null;
        //根据ID值判断User对象的获取方式
        if(id == null) {
            //如果ID为空，从Session中获取当前登录用户的信息
            HttpSession session = request.getSession(false);


//            //判断当前session和用户信息是否有效
//            if(session == null || session.getAttribute(AppConfig.USER_SESSION) == null) {
//                //用户未登录返回错误信息
//                return AppResult.failed(ResultCode.FAILED_FORBIDDEN);
//            }
//            配置了拦截器

            //从session中获取当前登录用户信息
                user = (User) session.getAttribute(AppConfig.USER_SESSION);
        } else {
            //如果ID不为空，从数据库中按ID查询用户信息
            user = userService.selectById(id);
        }

        //返回参数的校验
        if(user == null) {
            return AppResult.failed(ResultCode.FAILED_USER_NOT_EXISTS);
        }
        return AppResult.success(user);
    }


    @ApiOperation("退出登录")
    @GetMapping("/logout")
    public AppResult logout(HttpServletRequest request) {
        //读取session对象
        HttpSession session = request.getSession(false);
        //判断session是否有效
        if(session != null) {
            //打印日志
            log.info("退出成功");
         //表示用户在登录状态，直接销毁session
         session.invalidate();
        }

        return AppResult.success("退出成功");
    }


    /**
     * 修改个人信息
     * @param username 用户名
     * @param nickname 昵称
     * @param gender 性别
     * @param email 邮箱
     * @param phoneNum 电话
     * @param remark  个人简介
     * @return
     */
    @ApiOperation("修改个人信息")
    @PostMapping("/modifyInfo")
    public AppResult modifyInfo(HttpServletRequest request,
                                @ApiParam("用户名") @RequestParam(value = "username",required = false) String username,
                                @ApiParam("昵称") @RequestParam(value = "nickname",required = false)  String nickname,
                                @ApiParam("性别") @RequestParam(value = "gender",required = false)  Byte gender,
                                @ApiParam("邮箱") @RequestParam(value = "email",required = false)  String email,
                                @ApiParam("电话") @RequestParam(value = "phoneNum",required = false)  String phoneNum,
                                @ApiParam("个人简介") @RequestParam(value = "remark",required = false)  String remark) {
        //1.接收参数
        //2.对参数做非空校验(全部都为空，则返回错误描述)
        if(StringUtil.isEmpty(username)
                && StringUtil.isEmpty(nickname)
                && StringUtil.isEmpty(email)
                && StringUtil.isEmpty(phoneNum)
                && StringUtil.isEmpty(remark)
                && gender == null) {
            return AppResult.failed("请输入要修改的内容");
        }

        //获取session
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(AppConfig.USER_SESSION);

        //3.封装对象
        User updateUser = new User();
        updateUser.setId(user.getId()); //用户Id
        updateUser.setUsername(username); //用户名
        updateUser.setNickname(nickname); //昵称
        updateUser.setGender(gender); //性别
        updateUser.setEmail(email); //邮箱
        updateUser.setPhoneNum(phoneNum); //电话
        updateUser.setRemark(remark); //个人简介

        //4.调用Service中的方法
        userService.modifyInfo(updateUser);
        //5. 查询最新的用户信息
        user = userService.selectById(user.getId());
        //6. 把最新的用户信息设置到session中
        session.getAttribute(AppConfig.USER_SESSION);
        //7.返回结果
        return AppResult.success(user);
    }


    @ApiOperation("修改密码")
    @PostMapping("/modifyPwd")
    public AppResult modifyPassword(HttpServletRequest request,
                                    @ApiParam("原密码") @RequestParam("oldPassword") @NonNull String oldPassword,
                                    @ApiParam("新密码") @RequestParam("newPassword") @NonNull String newPassword,
                                    @ApiParam("确认密码") @RequestParam("passwordRepeat") @NonNull String passwordRepeat) {
        //1.校验新密码和确认密码是否相同
        if(!newPassword.equals(passwordRepeat)) {
            return AppResult.failed(ResultCode.FAILED_TWO_PWD_NOT_SAME);
        }
        //2.获取当前登录的用户信息
        HttpSession session = request.getSession(false);
        User user = (User) session.getAttribute(AppConfig.USER_SESSION);
        //3.调用service
        userService.modifyPassword(user.getId(),newPassword,oldPassword);

        //4，销毁session
        if(session != null) {
            session.invalidate();
        }

        return AppResult.success();
    }

}
