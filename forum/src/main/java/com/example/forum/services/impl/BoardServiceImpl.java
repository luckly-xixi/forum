package com.example.forum.services.impl;

import com.example.forum.common.AppResult;
import com.example.forum.common.ResultCode;
import com.example.forum.dao.BoardMapper;
import com.example.forum.exception.ApplicationException;
import com.example.forum.model.Board;
import com.example.forum.services.IBoardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
public class BoardServiceImpl implements IBoardService {
    @Resource
    private BoardMapper boardMapper;

    @Override
    public List<Board> selectByNum(Integer num) {
        //非空校验
        if(num <= 0) {
        //打印日志
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
        //抛异常
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        // 调用DAO查询数据库中的数据
        List<Board> result = boardMapper.selectByNum(num);
        //返回结果
        return result;
    }

    @Override
    public Board selectById(Long id) {
        //非空校验
        if(id == null || id <= 0) {
            log.warn(ResultCode.FAILED_BOARD_ARTICLE_COUNT.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_BOARD_ARTICLE_COUNT));
        }
        //调用DAO查询数据
        Board board = boardMapper.selectByPrimaryKey(id);
        return board;
    }

    @Override
    public void addOneArticleCountById(Long id) {
        //非空校验
        if(id == null || id <= 0) {
            log.warn(ResultCode.FAILED_BOARD_ARTICLE_COUNT.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_BOARD_ARTICLE_COUNT));
        }
        //查询对应的版块
        Board board = boardMapper.selectByPrimaryKey(id);
        if(board == null) {
            log.warn(ResultCode.ERROR_IS_NULL.toString() + ", user id = " + id);
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_IS_NULL));
        }
        //更新帖子数量
        Board updateBoard = new Board();
        updateBoard.setId(board.getId());
        updateBoard.setArticleCount(board.getArticleCount()+1);
        //调用DAO执行更新
        int row = boardMapper.updateByPrimaryKeySelective(updateBoard);
        //判断受影响的行数
        if(row != 1) {
            log.warn(ResultCode.FAILED.toString() + ", 受影响的行数不等于1.");
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED));
        }
    }
}
