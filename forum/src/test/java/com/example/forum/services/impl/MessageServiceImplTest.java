package com.example.forum.services.impl;

import com.example.forum.model.Message;
import com.example.forum.services.IMessageService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class MessageServiceImplTest {

    @Resource
    private IMessageService messageService;

    @Test
    @Transactional
    void create() {
        Message message = new Message();
        message.setPostUserId(2L);
        message.setReceiveUserId(1L);
//        message.setReceiveUserId(10L);
        message.setContent("测试站内信");
        messageService.create(message);
        System.out.println("测试成功");
    }

    @Test
    void selectUnreadCount() {
        Integer count = messageService.selectUnreadCount(1L);
        System.out.println("未读数量为 = " + count);
    }
}