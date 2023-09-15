package com.example.forum.services;

import com.example.forum.model.Board;

import java.util.List;

public interface IBoardService {

    /** 查询 num 条记录
     *
     */

    List<Board> selectByNum (Integer num);


    /**
     * 根据版块Id查询版块信息
     * @param id 版块Id
     * @return
     */
    Board selectById(Long id);
    /**
     * 更新板块中的 帖子数据
     * @param id 版块Id
     */
    void addOneArticleCountById(Long id);

}
