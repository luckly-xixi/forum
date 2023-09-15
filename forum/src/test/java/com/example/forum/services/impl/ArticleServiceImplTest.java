package com.example.forum.services.impl;

import com.example.forum.model.Article;
import com.example.forum.services.IArticleService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class ArticleServiceImplTest {

    @Resource
    private IArticleService articleService;

    @Test
    @Transactional
    void create() {
        Article article = new Article();
        article.setUserId(1L); //xixi
        article.setBoardId(1L); //java版块
        article.setTitle("单元测试");
        article.setContent("测试内容");
        articleService.create(article);
        System.out.println("发贴成功");
    }
}