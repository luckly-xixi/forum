package com.example.forum.services;

import com.example.forum.model.ArticleReply;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface IArticleReplyService {

    /**
     * 新增帖子回复
     * @param articleReply
     */
    @Transactional
    void create(ArticleReply articleReply);


    List<ArticleReply> selectByArticleId(Long articleId);
}
