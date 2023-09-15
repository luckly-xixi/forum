package com.example.forum.services;

import com.example.forum.model.Board;

import java.util.List;

public interface IBoardService {

    /** 查询 num 条记录
     *
     */

    List<Board> selectByNum (Integer num);

}
