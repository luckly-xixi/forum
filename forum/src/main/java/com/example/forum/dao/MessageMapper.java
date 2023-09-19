package com.example.forum.dao;

import com.example.forum.model.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface MessageMapper {
    int insert(Message row);

    int insertSelective(Message row);

    Message selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Message row);

    int updateByPrimaryKey(Message row);

    /**
     * 根据用户Id查询该用户未读数量
     * @param receiveUserId 用户Id
     * @return 户未读数量
     */
    Integer selectUnreadCount(@Param("receiveUserId") Long receiveUserId);

    /**
     * 根据接收者用户Id查询所有站内信
     * @param receiveUserId  接收者用户Id
     * @return List<Message>
     */
    List<Message> selectByReceiveUserId(@Param("receiveUserId") Long receiveUserId);
}