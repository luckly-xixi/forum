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
import java.util.List;

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
    public Message selectById(Long id) {
        //非空校验
        if(id == null || id <= 0 ) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        //调用DAO
        Message message = messageMapper.selectByPrimaryKey(id);

        return message;
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

    @Override
    public List<Message> selectByReceiveUserId(Long receiveUserId) {
        //非空校验
        if(receiveUserId == null || receiveUserId <= 0) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        List<Message> messages = messageMapper.selectByReceiveUserId(receiveUserId);
        return messages;
    }

    @Override
    public void updateStateById(Long id, Byte state) {
        //非空校验
        if(id == null || id <= 0 || state <0 || state > 2) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        //构造更新对象
        Message updateMessage = new Message();
        updateMessage.setId(id);
        updateMessage.setState(state);
        Date date = new Date();
        updateMessage.setUpdateTime(date);
        //调用DAO
        int row = messageMapper.updateByPrimaryKeySelective(updateMessage);
        if(row != 1) {
            log.warn(ResultCode.ERROR_SERVICES.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_SERVICES));
        }
    }

    @Override
    public void reply(Long repliedId, Message message) {
        //非空校验
        if(repliedId == null || repliedId <= 0) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        //校验repliedId对应的站内信状态
        Message existsMessage = messageMapper.selectByPrimaryKey(repliedId);
        if(existsMessage == null || existsMessage.getDeleteState() == 1) {
            log.warn(ResultCode.FAILED_MESSAGE_NOT_EXISTS.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_MESSAGE_NOT_EXISTS));
        }
        // 更新状态为已回复
        updateStateById(repliedId, (byte) 2);
        //回复内容写入数据库
        create(message);
    }


}
