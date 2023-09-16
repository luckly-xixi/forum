package com.example.forum.services.impl;

import com.example.forum.common.AppResult;
import com.example.forum.common.ResultCode;
import com.example.forum.dao.ArticleMapper;
import com.example.forum.exception.ApplicationException;
import com.example.forum.model.Article;
import com.example.forum.model.Board;
import com.example.forum.model.User;
import com.example.forum.services.IArticleService;
import com.example.forum.services.IBoardService;
import com.example.forum.services.IUserService;
import com.example.forum.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class ArticleServiceImpl implements IArticleService {

    // 注意循环引用的问题 A引用B，B同时也引用A
    @Resource
    private ArticleMapper articleMapper;
    //用户和版块的操作
    @Resource
    private IUserService userService;
    @Resource
    private IBoardService boardService;

    @Override
    public void create(Article article) {
        //非空校验
        if(article == null || article.getUserId() == null || article.getBoardId() == null
        || StringUtil.isEmpty(article.getTitle())
        || StringUtil.isEmpty(article.getContent())) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        //设置默认值
        article.setVisitCount(0);  //访问数
        article.setReplyCount(0);  //回复数
        article.setLikeCount(0);  //点赞数
        article.setDeleteState((byte) 0);
        article.setState((byte) 0);
        Date date = new Date();
        article.setCreateTime(date);
        article.setUpdateTime(date);
        //写入数据库
        int articleRow = articleMapper.insertSelective(article);
        if(articleRow <= 0) {
            log.warn(ResultCode.FAILED_CREATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_CREATE));
        }

        //获取用户发贴数
        User user = userService.selectById(article.getUserId()); //获取用户数
        //未找到指定用户信息
        if (user == null) {
            log.warn(ResultCode.FAILED_CREATE.toString() + "发帖失败， user id =" + article.getUserId());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_CREATE));
        }
        //更新用户发贴数
        userService.addOneArticleCountById(user.getId());

        //获取版块信息
        Board board = boardService.selectById(article.getBoardId());
        //是否在数据库再有对应的版块
        if(board == null) {
            log.warn(ResultCode.FAILED_CREATE.toString() + "发帖失败， board id =" + article.getBoardId());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_CREATE));
        }
        //更新版块中的帖子数量
        boardService.addOneArticleCountById(board.getId());

        //打印日志
        log.info(ResultCode.SUCCESS.toString() + ", user id =" + article.getUserId()
         + ", board id = " + article.getBoardId() + ", article id = " + article.getId() +
                "发贴成功");
    }

    @Override
    public List<Article> selectAll() {
        //调用DAO
        List<Article> articles = articleMapper.selectAll();
        //返回结果
        return articles;
    }

    @Override
    public List<Article> selectAllByBoardId(Long boardId) {
        //非空校验
        if(boardId == null || boardId<=0) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        //校验版块是否存在
        Board board = boardService.selectById(boardId);
        if(board == null) {
            log.warn(ResultCode.FAILED_BOARD_NOT_EXISTS.toString() + " , board id = " + boardId);
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_BOARD_NOT_EXISTS));
        }
        //调用DAO查询
        List<Article> articles = articleMapper.selectAllByBoardId(boardId);
        return articles;
    }
}
