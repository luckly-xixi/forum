package com.example.forum.services.impl;

import com.example.forum.common.AppResult;
import com.example.forum.common.ResultCode;
import com.example.forum.dao.UserMapper;
import com.example.forum.exception.ApplicationException;
import com.example.forum.model.User;
import com.example.forum.services.IUserService;
import com.example.forum.utils.MD5Util;
import com.example.forum.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

@Slf4j
@Service
public class UserServiceImpl implements IUserService {

    @Resource
    private UserMapper userMapper;

    @Override
    public void createNormalUser(User user) {
        //1.非空校验
        if(user == null || StringUtil.isEmpty(user.getUsername())
                || StringUtil.isEmpty(user.getNickname())
                || StringUtil.isEmpty(user.getPassword())
                || StringUtil.isEmpty(user.getSalt())) {
            //打印日志
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            //抛出异常,统一抛出ApplicationException
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }

        //2.按用户名查询用户信息
        User existsUser = userMapper.selectByUserName(user.getUsername());
            //2.1 判断用户是否存在
        if(existsUser != null) {
            //打印日志
            log.info(ResultCode.FAILED_USER_EXISTS.toString());
            //抛出异常,统一抛出ApplicationException
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_USER_EXISTS));
        }

        //3. 新增用户,设置默认值
        user.setGender((byte) 2);
        user.setArticleCount(0);
        user.setIsAdmin((byte) 0);
        user.setState((byte) 0);
        user.setDeleteState((byte) 0);
        //当前日期
        Date date = new Date();
        user.setCreateTime(date);
        user.setUpdateTime(date);

        //写入数据库
        int row = userMapper.insertSelective(user);
        if(row != 1) {
            //打印日志
            log.info(ResultCode.FAILED_CREATE.toString());
            //抛出异常,统一抛出ApplicationException
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_CREATE));
        }
        //新增成功
        log.info("新增用户成功  username = " + user.getUsername() + " . ");
    }

    @Override
    public User selectByUserName(String usernane) {
        if(StringUtil.isEmpty(usernane)) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        return userMapper.selectByUserName(usernane);
    }

    @Override
    public User login(String username, String password) {
        //  1.非空校验
        if(StringUtil.isEmpty(username) || StringUtil.isEmpty(password)) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        //  2.按用户名查询用户信息
        User user = selectByUserName(username);
        //  3.对查询结果做非空校验
        if(user == null) {
            log.warn(ResultCode.FAILED_LOGIN.toString() + " , username = " + username);
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_LOGIN));
        };
        //  4.对密码做校验
        String encryptPassword = MD5Util.md5salt(password,user.getSalt());
        if(!encryptPassword.equals(user.getPassword())) {
            log.warn(ResultCode.FAILED_LOGIN.toString() + " 密码错误 username = " + username);
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_LOGIN));
        }
        log.info("登陆成功，username = " + username);
        return user;
    }

    @Override
    public User selectById(Long id) {
        //1. 非空校验
        if(id == null) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_LOGIN));
        }
        //2. 调用DAO查询数据库并获取对象
        User user = userMapper.selectByPrimaryKey(id);
        //3.返回结果
        return user;
    }
}
