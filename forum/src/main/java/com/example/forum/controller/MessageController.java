package com.example.forum.controller;


import com.example.forum.common.AppResult;
import com.example.forum.common.ResultCode;
import com.example.forum.config.AppConfig;
import com.example.forum.model.Message;
import com.example.forum.model.User;
import com.example.forum.services.IMessageService;
import com.example.forum.services.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@Api(tags = "站内信接口")
@Slf4j
@RequestMapping("/message")
public class MessageController {


    @Resource
    private IMessageService messageService;
    @Resource
    private IUserService userService;

        @ApiOperation("发送站内信")
        @PostMapping("/send")
        public AppResult send(HttpServletRequest request,
                              @ApiParam("接收者Id") @RequestParam("receiveUserId") @NonNull Long receiveUserId,
                              @ApiParam("内容") @RequestParam("content") @NonNull String content) {
            HttpSession session = request.getSession(false);
            User user = (User) session.getAttribute(AppConfig.USER_SESSION);
            //1.当前用户状态
            if(user.getState() == 1) {
                return AppResult.failed(ResultCode.FAILED_USER_BANNED);
            }

            //2.不能给自己发站内信
            if(user.getId() == receiveUserId) {
                return AppResult.failed("不能给自己发送站内信");
            }

            //3.校验接收者是否存在
            User receiveUser = userService.selectById(receiveUserId);
            if(receiveUser == null || receiveUser.getDeleteState() == 1) {
                return AppResult.failed("接收者状态异常");
            }

            //4.封装对象
            Message message =new Message();
            message.setPostUserId(user.getId()); //发送者Id
            message.setReceiveUserId(receiveUserId); // 接收者Id
            message.setContent(content); // 站内信内容

            //5.调用DAO
            messageService.create(message);
            //6.返回结果
            return AppResult.success("发送成功");
        }


        @ApiOperation("获取未读数")
        @GetMapping("/getUnreadCount")
        public AppResult<Integer> getUnreadCount(HttpServletRequest request) {
            //1.获取当前登录的用户
            HttpSession session = request.getSession(false);
            User user = (User) session.getAttribute(AppConfig.USER_SESSION);
            //2.调用Service
            Integer count = messageService.selectUnreadCount(user.getId());//当亲登录用户的id就是当前接收者id\
            return AppResult.success(count);
        }

        @ApiOperation("查询用户的所有站内信")
        @GetMapping("/getAll")
        public AppResult<List<Message>> getAll (HttpServletRequest request) {
            //1.获取当前登录用户
            HttpSession session = request.getSession(false);
            User user = (User) session.getAttribute(AppConfig.USER_SESSION);
            //2.调用Service
            List<Message> messages = messageService.selectByReceiveUserId(user.getId());
            //3.返回结果
            return AppResult.success(messages);
        }

        @ApiOperation("更新为已读")
        @PostMapping("/markRead")
    public AppResult markRead (HttpServletRequest request, @ApiParam("站内信Id") @RequestParam("id") @NonNull Long id) {
            //1.根据ID查询站内信
            Message message = messageService.selectById(id);

            //2.站内信是否存在
            if(message == null || message.getDeleteState() == 1) {
             return AppResult.failed(ResultCode.FAILED_MESSAGE_NOT_EXISTS);
            }

            //3.站内信是不是自己的
            HttpSession session = request.getSession(false);
            User user = (User) session.getAttribute(AppConfig.USER_SESSION);
            if(user.getId() != message.getReceiveUserId()) {
                return AppResult.failed(ResultCode.FAILED_FORBIDDEN);
            }
            //调用Service，把状态更新为已读
            messageService.updateStateById(id, (byte) 1);

            return AppResult.success();
        }


        @ApiOperation("回复站内信")
        @PostMapping("/reply")
        public AppResult reply(HttpServletRequest request,
                               @ApiParam("要回复的站内信Id") @RequestParam("repliedId") @NonNull Long repliedId,
                               @ApiParam("站内信的内容") @RequestParam("content") @NonNull String content) {

            //校验当前登录用户的状态
            HttpSession session = request.getSession(false);
            User user = (User) session.getAttribute(AppConfig.USER_SESSION);
            if(user.getState() == 1) {
                return AppResult.failed(ResultCode.FAILED_USER_BANNED);
            }

            //校验要回复的站内信状态
            Message existsMessage = messageService.selectById(repliedId);
            if(existsMessage == null || existsMessage.getDeleteState() == 1) {
                return AppResult.failed(ResultCode.FAILED_MESSAGE_NOT_EXISTS);
            }

            //不能给自己发站内信
            if(user.getId() == existsMessage.getPostUserId()) {
            return AppResult.failed("不能回复自己的站内信");
            }

            //构造对象
            Message message = new Message();
            message.setPostUserId(user.getId()); // 发送者
            message.setReceiveUserId(existsMessage.getPostUserId()); // 接收者
            message.setContent(content);
            //调用Service
            messageService.reply(repliedId,message);

            return AppResult.success();
        }
}
