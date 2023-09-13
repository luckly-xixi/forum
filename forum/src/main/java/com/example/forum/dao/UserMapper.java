package com.example.forum.dao;

import com.example.forum.model.User;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface UserMapper {
    int insert(User row);

    int insertSelective(User row);

    User selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(User row);

    int updateByPrimaryKey(User row);
}