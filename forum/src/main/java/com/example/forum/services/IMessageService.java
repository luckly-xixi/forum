package com.example.forum.services;

import com.example.forum.model.Message;

public interface IMessageService {

    /**
     *  发送站内信
     * @param message 站内信对象
     */
    void create(Message message);

    /**
     * 根据用户Id查询该用户未读数量
     * @param receiveUserId 用户Id
     * @return 户未读数量
     */
    Integer selectUnreadCount(Long receiveUserId);
}
