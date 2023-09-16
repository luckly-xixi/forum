package com.example.forum.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;


@Data
public class Article {
    private Long id;

    private Long boardId;

    private Long userId;

    private String title;

    // 访问数
    private Integer visitCount;

    // 回复数
    private Integer replyCount;

    // 点赞数
    private Integer likeCount;

    private Byte state;

    private Byte deleteState;

    private Date createTime;

    private Date updateTime;

    private String content;

    @ApiModelProperty("是否是作者")
    private boolean isOwn = false;

    //关联的对象 - 作者
    private User user;

    //关联的对象 - 版块
    private Board board;
}