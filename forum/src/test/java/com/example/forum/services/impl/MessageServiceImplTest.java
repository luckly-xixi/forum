package com.example.forum.services.impl;

import com.example.forum.model.Message;
import com.example.forum.services.IMessageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class MessageServiceImplTest {

    @Resource
    private IMessageService messageService;
    @Resource
    private ObjectMapper objectMapper;

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

    @Test
    void selectByReceiveUserId() throws JsonProcessingException {
        List<Message> messages = messageService.selectByReceiveUserId(1L);
        System.out.println(objectMapper.writeValueAsString(messages));
        messages = messageService.selectByReceiveUserId(2L);
        System.out.println(objectMapper.writeValueAsString(messages));
        messages = messageService.selectByReceiveUserId(20L);
        System.out.println(objectMapper.writeValueAsString(messages));
    }

    @Test
    @Transactional
    void updateStateById() {
//        messageService.updateStateById(1L, (byte) 1);
//        messageService.updateStateById(1L, (byte) 2);
//        messageService.updateStateById(1L, (byte) 3);
        messageService.updateStateById(1L, (byte) 0);
        System.out.println("更新成功");
    }

    @Test
    void selectById() throws JsonProcessingException {
        Message message = messageService.selectById(1L);
        System.out.println(objectMapper.writeValueAsString(message));
    }

    @Test
    void reply() {
        Message message = new Message();
        message.setPostUserId(1L);
        message.setReceiveUserId(2L);
        message.setContent("回复测试");
        //调用Service
        messageService.reply(3L,message);
        System.out.println("测试成功");
    }
}