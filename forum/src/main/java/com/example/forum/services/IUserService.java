package com.example.forum.services;

import com.example.forum.model.User;

public interface IUserService {
    //  创建一个普通用户
    void createNormalUser(User user);


    //根据用户名查询用户
    User selectByUserName(String usernane);


    //用户登录
    User login(String username, String password);


    //根据ID查询用户的信息
    User selectById(Long id);

    /**
     * 版块中的帖子数量 + 1
     * @param id  用户Id
     */
    void addOneArticleCountById(Long id);


    /**
     * 版块中的帖子数量 - 1
     * @param id 版块Id
     */
    void subOneArticleCountById(Long id);

    /**
     * 修改个人信息
     * @param user 要更新的对象
     */
    void modifyInfo(User user);

    /**
     *  修改密码
     * @param id 用户Id
     * @param newPassword 新密码
     * @param oldPassword 老密码
     */
    void modifyPassword(Long id,String newPassword,String oldPassword);


}
