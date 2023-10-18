package com.example.forum.services.impl;

import com.example.forum.common.AppResult;
import com.example.forum.common.ResultCode;
import com.example.forum.dao.UserMapper;
import com.example.forum.exception.ApplicationException;
import com.example.forum.model.User;
import com.example.forum.services.IUserService;
import com.example.forum.utils.MD5Util;
import com.example.forum.utils.StringUtil;
import com.example.forum.utils.UUIDUtil;
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
        if(existsUser != null) {
            log.info(ResultCode.FAILED_USER_EXISTS.toString());
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
            log.info(ResultCode.FAILED_CREATE.toString());
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

    @Override
    public void addOneArticleCountById(Long id) {
        //非空校验
        if(id == null || id <= 0) {
            log.warn(ResultCode.FAILED_BOARD_ARTICLE_COUNT.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_BOARD_ARTICLE_COUNT));
        }
        //查询用户信息
        User user = userMapper.selectByPrimaryKey(id);
        if(user == null) {
            log.warn(ResultCode.ERROR_IS_NULL.toString() + ", user id = " + id);
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_IS_NULL));
        }
        //更新用户的发帖数量
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setArticleCount(user.getArticleCount()+1);
        //调用DAO执行更新
        int row = userMapper.updateByPrimaryKeySelective(updateUser);
        //判断受影响的行数
        if(row != 1) {
            log.warn(ResultCode.FAILED.toString() + ", 受影响的行数不等于1.");
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED));
        }
    }

    @Override
    public void subOneArticleCountById(Long id) {
        //非空校验
        if(id == null || id <= 0) {
            log.warn(ResultCode.FAILED_BOARD_ARTICLE_COUNT.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_BOARD_ARTICLE_COUNT));
        }
        //查询用户信息
        User user = userMapper.selectByPrimaryKey(id);
        if(user == null) {
            log.warn(ResultCode.ERROR_IS_NULL.toString() + ", user id = " + id);
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_IS_NULL));
        }
        //更新用户的发帖数量
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setArticleCount(user.getArticleCount() - 1 );
        //判断减1之后是否小于0
        if(updateUser.getArticleCount() < 0) {
            updateUser.setArticleCount(0);
        }

        //调用DAO
        int row = userMapper.updateByPrimaryKeySelective(updateUser);
        if(row != 1) {
            log.warn(ResultCode.FAILED.toString() + ", 受影响的行数不等于1.");
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED));
        }
    }

    @Override
    public void modifyInfo(User user) {
        // 1.非空校验
        if(user == null || user.getId() == null || user.getId() <= 0) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        // 2.校验用户是否存在
        User existsUser = userMapper.selectByPrimaryKey(user.getId());
        if(existsUser == null) {
            log.warn(ResultCode.FAILED_USER_NOT_EXISTS.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_USER_NOT_EXISTS));
        }

        //3. 定义一个标志位
        boolean checkAttr = false; // false表示没有校验通过

        //4. 定义一个专门用来更新的对象，防止用户传入的User对象设置了其他的属性
        //当使用动态SQL进行更新的时候，覆盖了没有经过校验的字段
        User updateUser = new User();
        //5.设置用户Id
        updateUser.setId(user.getId());

        //6.对每一个参数进行校验并赋值
        //校验用户名
        if(!StringUtil.isEmpty(user.getUsername())
                && !user.getUsername().equals(existsUser.getUsername())) {
            //需要更新用户名（登录名）时，进行唯一性的校验
            User checkUser = userMapper.selectByUserName(user.getUsername());
            if(checkUser != null) {
                log.warn(ResultCode.FAILED_USER_EXISTS.toString());
                throw new ApplicationException(AppResult.failed(ResultCode.FAILED_USER_EXISTS));
            }
            //数据库中没有找到相对应的用户，表示可以修改用户名
            updateUser.setUsername(user.getUsername());
            //更新标志位
            checkAttr = true;
        }

        // 校验昵称
        if(!StringUtil.isEmpty(user.getNickname())
            && !user.getNickname().equals(existsUser.getNickname())) {
            //设置昵称
            updateUser.setNickname(user.getNickname());
            //更新标志位
            checkAttr = true;
        }

        //校验性别
        if(user.getGender() != null && user.getGender() != existsUser.getGender()) {
            //设置性别
            updateUser.setGender(user.getGender());
            //合法性校验
            if(updateUser.getGender() > 2 || updateUser.getGender() < 0) {
                updateUser.setGender((byte) 2);
            }
            //更新标志位
            checkAttr = true;
        }

        //校验Email
        if(!StringUtil.isEmpty(user.getEmail()) &&
        !user.getEmail().equals(existsUser.getEmail())) {
            //设置Email
            updateUser.setEmail(user.getEmail());
            //更新标志位
            checkAttr = true;
        }

        //校验电话号码
        if(!StringUtil.isEmpty(user.getPhoneNum()) &&
                !user.getPhoneNum().equals(existsUser.getPhoneNum())) {
            //设置电话号码
            updateUser.setPhoneNum(user.getPhoneNum());
            //更新标志位
            checkAttr = true;
        }

        //校验个人简介
        if(!StringUtil.isEmpty(user.getRemark()) &&
                !user.getRemark().equals(existsUser.getRemark())) {
            //设置个人简介
            updateUser.setRemark(user.getRemark());
            //更新标志位
            checkAttr = true;
        }

        //7.根据标志位来决定是否执行更新
        if(checkAttr == false) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }

        //8.调用DAO
        int row = userMapper.updateByPrimaryKeySelective(updateUser);
        if(row != 1) {
            log.warn(ResultCode.FAILED.toString() + ", 受影响的行数不等于1.");
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED));
        }
    }

    @Override
    public void modifyPassword(Long id, String newPassword, String oldPassword) {

        // 1.非空校验
        if(id == null || id <= 0 || StringUtil.isEmpty(newPassword) || StringUtil.isEmpty(oldPassword)) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        //查询用户信息
        User user = userMapper.selectByPrimaryKey(id);
        if(user == null || user.getDeleteState() == 1) {
            log.warn(ResultCode.FAILED_USER_NOT_EXISTS.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_USER_NOT_EXISTS));
        }

        //校验老密码是否正确
        //对老密码进行加密，获取密文
        String oldEncryptPassword = MD5Util.md5salt(oldPassword, user.getSalt());
        //与用户当前密码进行比较
        if(!oldEncryptPassword.equalsIgnoreCase(user.getPassword())) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }

        //生成新的盐值
        String salt = UUIDUtil.UUID_32();
        //生成新密码密文
        String encryptPassword = MD5Util.md5salt(newPassword, salt);

        //构造更新对象
        User updateUser = new User();
        updateUser.setId(user.getId());// 用户Id
        updateUser.setSalt(salt); //新生成的盐
        updateUser.setPassword(encryptPassword); //新密码密文
        Date date = new Date();
        updateUser.setUpdateTime(date);  //更新时间

        //调用DAO
        int row = userMapper.updateByPrimaryKeySelective(updateUser);
        if(row != 1) {
            log.warn(ResultCode.FAILED.toString() + ", 受影响的行数不等于1.");
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED));
        }
    }
}
