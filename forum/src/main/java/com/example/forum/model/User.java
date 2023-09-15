package com.example.forum.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

//作用在响应的类上，对返回响应数据的说明
@ApiModel("用户信息")

@Data
public class User {
    private Long id;

    //作用在类的属性上，对属性的说明
    @ApiModelProperty("用户名")
    private String username;

    @JsonIgnore //不参与Json序列化
    private String password;

    @ApiModelProperty("昵称")
    private String nickname;

    @ApiModelProperty("电话号码")
    private String phoneNum;

    @ApiModelProperty("邮箱")
    private String email;

    @ApiModelProperty("性别")
    private Byte gender;

    @JsonIgnore //不参与Json序列化
    private String salt;

    @ApiModelProperty("头像地址")
    private String avatarUrl;

    @ApiModelProperty("发帖数量")
    private Integer articleCount;

    @ApiModelProperty("是否管理员")
    private Byte isAdmin;

    @ApiModelProperty("个人简介")
    private String remark;

    @ApiModelProperty("用户状态")
    private Byte state;

    @JsonIgnore //不参与Json序列化
    private Byte deleteState;

    @ApiModelProperty("注册日期")
    private Date createTime;

    private Date updateTime;

}