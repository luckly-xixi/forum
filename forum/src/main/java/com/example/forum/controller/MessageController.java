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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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
}
