package com.example.forum.services.impl;

import com.example.forum.model.User;
import com.example.forum.services.IUserService;
import com.example.forum.utils.MD5Util;
import com.example.forum.utils.UUIDUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


@SpringBootTest
class UserServiceImplTest {

    @Resource
    private IUserService userService;

    @Test
    void createNormalUser() {
        User user = new User();
        user.setUsername("Giao");
        user.setNickname("GIAOGIAO~");

        //定义一个原始密码
        String password = "123456";
        //生成盐
        String salt = UUIDUtil.UUID_32();
        //生产密码密文
        String ciphertext = MD5Util.md5salt(password,salt);
        //设置加密后的 密码
        user.setPassword(ciphertext);
        //设置盐
        user.setSalt(salt);
        //调用Service层方法
        userService.createNormalUser(user);
        System.out.println(user);
    }

    @Test
    void selectByUserName() {
//        User user = userService.selectByUserName("Giao");
        User user = userService.selectByUserName("Giao");
        System.out.println(user);
    }

    @Test
    void login() {
        User user = userService.login("Giao","123456");
//        User user = userService.login("Giao1","123456");
//        User user = userService.login("Giao","1234566");
        System.out.println(user);
    }

    @Test
    void selectById() {
        User user = userService.selectById(1L);
        System.out.println(user);
    }

    @Test
    @Transactional
    void addOneArticleCountById() {
        userService.addOneArticleCountById(1L);
        System.out.println("更新成功");
    }

    @Test
    @Transactional
    void subOneArticleCountById() {
//        userService.subOneArticleCountById(1L);
        userService.subOneArticleCountById(3L);
        System.out.println("更新成功");
    }

    @Test
    @Transactional
    void modifyInfo() {
        User user = new User();
        user.setId(3L); // 用户Id
//        user.setId(30L); // 用户Id
//        user.setUsername("Giao"); //登录名
        user.setUsername("zhangsan"); //登录名
        user.setNickname("zhangsan"); //昵称
//        user.setGender((byte) 0); //性别
        user.setGender((byte) 3); //性别
        user.setEmail("qwer@QQ.com"); //邮箱
        user.setPhoneNum("110"); //电话
        user.setRemark("法外狂徒"); //个人简介
        userService.modifyInfo(user);
    }

    @Test
    @Transactional
    void modifyPassword() {
        userService.modifyPassword(1L,"123456","110");
        System.out.println("更新成功");
    }
}