package com.example.forum.services.impl;

import com.example.forum.common.AppResult;
import com.example.forum.common.ResultCode;
import com.example.forum.dao.MessageMapper;
import com.example.forum.exception.ApplicationException;
import com.example.forum.model.Message;
import com.example.forum.model.User;
import com.example.forum.services.IMessageService;
import com.example.forum.services.IUserService;
import com.example.forum.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

@Slf4j
@Service
public class MessageServiceImpl implements IMessageService {

    @Resource
    private MessageMapper messageMapper;
    @Resource
    private IUserService userService;


    @Override
    public void create(Message message) {
        //非空校验
        if(message == null
                || message.getPostUserId() == null
                || message.getReceiveUserId() == null
                || StringUtil.isEmpty(message.getContent())) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        //校验接收者是否存在
        User user = userService.selectById(message.getReceiveUserId());
        if(user == null || user.getDeleteState() == 1) {
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }


        //设置默认值
        message.setState((byte) 0);
        message.setDeleteState((byte) 0);
        //设置创建于更新时间
        Date date = new Date();
        message.setCreateTime(date);
        message.setUpdateTime(date);

        //调用DAO
        int row = messageMapper.insertSelective(message);
        if(row != 1) {
            log.warn(ResultCode.ERROR_SERVICES.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_SERVICES));
        }
    }

    @Override
    public Integer selectUnreadCount(Long receiveUserId) {
        //非空校验
        if(receiveUserId == null || receiveUserId <= 0) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        //直接调用DAO
        Integer count = messageMapper.selectUnreadCount(receiveUserId);
        //正常查询不可能出现null
        if(count == null) {
            log.warn(ResultCode.ERROR_SERVICES.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_SERVICES));
        }

        return count;
    }


}
