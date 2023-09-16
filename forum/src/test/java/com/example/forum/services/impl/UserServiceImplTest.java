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
}