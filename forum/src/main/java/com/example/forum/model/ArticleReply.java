package com.example.forum.model;

import lombok.Data;

import java.util.Date;


@Data
public class ArticleReply {
    //编号
    private Long id;

    //帖子ID,关联Article
    private Long articleId;

    //回复的用户的编号
    private Long postUserId;

    //楼中楼
    private Long replyId;

    //楼中楼
    private Long replyUserId;

    // 回复的正文
    private String content;

    //  点赞
    private Integer likeCount;

    //  状态 0 正常  1 禁用
    private Byte state;

    //  状态 0 正常  1 删除
    private Byte deleteState;

    //创建时间
    private Date createTime;

    //更新时间
    private Date updateTime;

    //  关联对象 - 回复的发布者
    private User user;

}