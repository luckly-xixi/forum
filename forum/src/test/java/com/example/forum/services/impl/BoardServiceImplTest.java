package com.example.forum.services.impl;

import com.example.forum.model.Board;
import com.example.forum.services.IBoardService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import java.util.List;


@SpringBootTest
class BoardServiceImplTest {

    @Resource
    private IBoardService boardService;

    @Test
    void selectByNum() {
//        List<Board> boards = boardService.selectByNum(5);
        List<Board> boards = boardService.selectByNum(1);
        System.out.println(boards);
    }
}