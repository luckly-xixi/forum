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

    @Override
    public List<Article> selectByUserId(Long userId) {
        //非空校验
        if(userId == null || userId<=0) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        //校验用户是否存在
        User user = userService.selectById(userId);
        if(user == null) {
            log.warn(ResultCode.FAILED_USER_NOT_EXISTS.toString() + " , user id = " + userId);
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_USER_NOT_EXISTS));
        }
        //调用DAO
        List<Article> articles = articleMapper.selectByUserId(userId);

        return articles;
    }

    @Override
    public Article selectById(Long id) {
        if(id == null || id <= 0) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        //调用DAO
        Article article = articleMapper.selectByPrimaryKey(id);
        return article;
    }

    @Override
    public Article selectDetailById(Long id) {
        //非空校验
        if(id == null || id <= 0) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        //调用DAO
        Article article = articleMapper.selectDetailById(id);
        if(article == null) {
            log.warn(ResultCode.FAILED_ARTICLE_NOT_EXISTS.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_ARTICLE_NOT_EXISTS));
        }
        //更新帖子访问数
        Article updateArticle = new Article();
        updateArticle.setId(article.getId());
        updateArticle.setVisitCount(article.getVisitCount()+1);
        //保存到数据库
        int row = articleMapper.updateByPrimaryKeySelective(updateArticle);
        if(row != 1) {
            log.warn(ResultCode.ERROR_SERVICES.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_SERVICES));
        }
        //返回帖子详情
        article.setVisitCount(article.getVisitCount()+1);
        return article;
    }

    @Override
    public void modify(Long id, String title, String content) {
        //非空校验
        if(id == null || id <= 0
        || StringUtil.isEmpty(title)
        || StringUtil.isEmpty(content)) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        //构建要更新的帖子对象
        Article updateArticle = new Article();
        updateArticle.setId(id); //Id
        updateArticle.setTitle(title); //标题
        updateArticle.setContent(content); //正文
        updateArticle.setUpdateTime(new Date()); //更新时间
        //调用DAO
        int row = articleMapper.updateByPrimaryKeySelective(updateArticle);
        if(row != 1) {
            log.warn(ResultCode.ERROR_SERVICES.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_SERVICES));
        }
    }

    @Override
    public void thumbsUpById(Long id) {
        //非空校验
        if(id == null || id <= 0) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        //获取帖子详情
        Article article = articleMapper.selectByPrimaryKey(id);
        //帖子不存在
        if(article == null || article.getDeleteState() ==1) {
            log.warn(ResultCode.FAILED_ARTICLE_NOT_EXISTS.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_ARTICLE_NOT_EXISTS));
        }
        //帖子异常
        if(article.getState() ==1) {
            log.warn(ResultCode.FAILED_ARTICLE_BANNED.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_ARTICLE_BANNED));
        }

        //构造要更新的对象
        Article updateArticle = new Article();
        updateArticle.setId(article.getId());
        updateArticle.setLikeCount(article.getLikeCount() + 1);
        updateArticle.setUpdateTime(new Date());

        //调用DAO
        int row = articleMapper.updateByPrimaryKeySelective(updateArticle);
        if(row != 1) {
            log.warn(ResultCode.ERROR_SERVICES.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_SERVICES));
        }
    }

    @Override
    public void deleteById(Long id) {
        //非空校验
        if(id == null || id <= 0) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        //  根据Id查询帖子信息
        Article article = articleMapper.selectByPrimaryKey(id);
        if(article == null || article.getDeleteState() == 1) {
            log.warn(ResultCode.FAILED_BOARD_NOT_EXISTS.toString() + ", article = " + id);
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_BOARD_NOT_EXISTS));
        }
        //构造更新对象
        Article updateArticle = new Article();
        updateArticle.setId(article.getId());
        updateArticle.setDeleteState((byte) 1);
        //调用DAO
        int row = articleMapper.updateByPrimaryKeySelective(updateArticle);
        if(row != 1) {
            log.warn(ResultCode.ERROR_SERVICES.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_SERVICES));
        }
        //更新版块中帖子的数量
        boardService.subOneArticleCountById(article.getBoardId());
        //更新用户发帖数
        userService.subOneArticleCountById(article.getUserId());

        log.info("删除帖子成功, article id = " + article.getId() + ", user id = " + article.getUserId());
    }


    @Override
    public void addOneReplyCountById(Long id) {
        //非空校验
        if(id == null || id <= 0) {
            log.warn(ResultCode.FAILED_PARAMS_VALIDATE.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_PARAMS_VALIDATE));
        }
        //    获取帖子记录
        Article article = articleMapper.selectByPrimaryKey(id);
        if(article == null || article.getDeleteState() == 1) {
            log.warn(ResultCode.FAILED_BOARD_NOT_EXISTS.toString() + ", article = " + id);
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_BOARD_NOT_EXISTS));
        }
        //   帖子异常  ,  封帖
        if(article.getState() == 1 ) {
            log.warn(ResultCode.FAILED_ARTICLE_BANNED.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.FAILED_ARTICLE_BANNED));
        }
        //构造更新对象
        Article updateArticle = new Article();
        updateArticle.setId(article.getId());
        //帖子回复数 + 1
        updateArticle.setReplyCount(article.getReplyCount() + 1);
        updateArticle.setUpdateTime(new Date());
        //执行更新
        int row = articleMapper.updateByPrimaryKeySelective(updateArticle);
        if(row != 1) {
            log.warn(ResultCode.ERROR_SERVICES.toString());
            throw new ApplicationException(AppResult.failed(ResultCode.ERROR_SERVICES));
        }
    }



}
