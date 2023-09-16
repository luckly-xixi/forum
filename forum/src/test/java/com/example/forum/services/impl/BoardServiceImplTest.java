package com.example.forum.services.impl;

import com.example.forum.model.Board;
import com.example.forum.services.IBoardService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import java.util.List;


@SpringBootTest
class BoardServiceImplTest {

    @Resource
    private IBoardService boardService;

    @Resource
    private ObjectMapper objectMapper;

    @Test
    void selectByNum() {
//        List<Board> boards = boardService.selectByNum(5);
        List<Board> boards = boardService.selectByNum(1);
        System.out.println(boards);
    }

    @Test
    @Transactional //更新成功后回滚操作
    void addOneArticleCount() {
        boardService.addOneArticleCountById(1L);
        System.out.println("更新成功");
    }

    @Test
    void selectById() throws JsonProcessingException {
//        Board board = boardService.selectById(1L);
//        Board board = boardService.selectById(2L);
        Board board = boardService.selectById(10L);
        System.out.println(objectMapper.writeValueAsString(board));
    }
}