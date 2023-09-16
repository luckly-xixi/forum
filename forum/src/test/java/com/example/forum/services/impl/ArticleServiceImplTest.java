package com.example.forum.services.impl;

import com.example.forum.model.Article;
import com.example.forum.services.IArticleService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class ArticleServiceImplTest {

    @Resource
    private IArticleService articleService;
    @Resource
    private ObjectMapper objectMapper;

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

    @Test
    void selectAll() throws JsonProcessingException {
        //调用Service层
        List<Article> articles = articleService.selectAll();
        //转换成字符串
        System.out.println(objectMapper.writeValueAsString(articles));
    }

    @Test
    void selectAllByBoardId() throws JsonProcessingException {
//        List<Article> articles = articleService.selectAllByBoardId(1L);
        List<Article> articles = articleService.selectAllByBoardId(3L);
//        List<Article> articles = articleService.selectAllByBoardId(10L);
        System.out.println(objectMapper.writeValueAsString(articles));
    }
}