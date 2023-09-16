package com.example.forum.controller;


import com.example.forum.common.AppResult;
import com.example.forum.common.ResultCode;
import com.example.forum.config.AppConfig;
import com.example.forum.exception.ApplicationException;
import com.example.forum.model.Article;
import com.example.forum.model.Board;
import com.example.forum.model.User;
import com.example.forum.services.IArticleService;
import com.example.forum.services.IBoardService;
import com.sun.istack.internal.NotNull;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;


@Api(tags = "文章接口")
@Slf4j
@RestController
@RequestMapping("/article")
public class ArticleController {

    @Resource
    private IArticleService articleService;
    @Resource
    private IBoardService boardService;
    /**
     * 发布新贴子
     * @param boardId 版块Id
     * @param title 文章标题
     * @param content 文章内容
     * @return
     */
        @ApiOperation("发布新帖")
        @PostMapping("/create")
        public AppResult create (HttpServletRequest request,
                                 @ApiParam("版块Id") @RequestParam("boardId") @NotNull Long boardId,
                                 @ApiParam("文章标题") @RequestParam("title") @NotNull String title,
                                 @ApiParam("文章内容") @RequestParam("content") @NotNull String content) {
            //校验用户禁言
            HttpSession session = request.getSession(false);
            User user = (User) session.getAttribute(AppConfig.USER_SESSION);
            if(user.getState() == 1) {
                return AppResult.failed(ResultCode.FAILED_USER_BANNED);
            }
            //版块校验
            Board board = boardService.selectById(boardId.longValue());
            if(board == null) {
                log.warn(ResultCode.FAILED_BOARD_BANNED.toString());
                return AppResult.failed(ResultCode.FAILED_BOARD_BANNED);
            }
            // 封装文章对象
            Article article = new Article();
            article.setTitle(title);  //标题
            article.setContent(content);  // 正文
            article.setBoardId(boardId);  // 版块Id
            article.setUserId(user.getId());  // 作者Id
            //调用Service
            articleService.create(article);
            //响应
            return AppResult.success();
        }


        @ApiOperation("获取帖子列表")
        @GetMapping("/getAllByBoardId")
        public AppResult<List<Article>> getAllByBoardId(
                @ApiParam("版块Id") @RequestParam(value = "boardId",required = false) Long boardId) {
            //  定义返回结果集
            List<Article> articles;
            if(boardId == null) {
                //查询所有
                articles = articleService.selectAll();
            } else {
                articles = articleService.selectAllByBoardId(boardId);
            }

            //结果是否为空
            if(articles == null) {
                // 如果集合为空，那么创建一个空集合
                articles = new ArrayList<>();
            }
            //响应结果
            return AppResult.success(articles);
        }


        @ApiOperation("根据帖子Id获取详情")
        @GetMapping("/details")
        public AppResult<Article> getDetails (HttpServletRequest request,
                @ApiParam("帖子Id") @RequestParam("id") @NotNull Long id){
            //从session中获取当前登录用户
            HttpSession session = request.getSession(false);
            User user = (User) session.getAttribute(AppConfig.USER_SESSION);

            // 调用Service，获取帖子详情
            Article article = articleService.selectDetailById(id);
            //校验返回参数
            if(article == null) {
                throw new ApplicationException(AppResult.failed(ResultCode.FAILED_ARTICLE_NOT_EXISTS));
            }
            //判断当前用户是否为作者
            if(user.getId() == article.getUserId()) {
             //标识为作者
             article.setOwn(true);
            }

            return AppResult.success(article);
        }



        @ApiOperation("修改帖子")
        @PostMapping("/modify")
        public AppResult modify(HttpServletRequest request,
                                @ApiParam("帖子Id") @RequestParam("id") @NotNull Long id,
                                @ApiParam("帖子标题") @RequestParam("title") @NotNull String title,
                                @ApiParam("帖子正文") @RequestParam("content") @NotNull String content) {
            //获取当前登录用户
            HttpSession session = request.getSession(false);
            User user = (User) session.getAttribute(AppConfig.USER_SESSION);
            //校验用户状态
            if(user.getState() == 1) {
             return AppResult.failed(ResultCode.FAILED_USER_BANNED);
            }
            //查询帖子详情
            Article article = articleService.selectById(id);
            if(article == null) {
             return AppResult.failed(ResultCode.FAILED_ARTICLE_NOT_EXISTS);
            }
            //判断用户是不是作者
            if(user.getId() != article.getUserId()) {
                return AppResult.failed(ResultCode.FAILED_FORBIDDEN);
            }
            //判断帖子状态 - 已归档
            if(article.getState() == 1 || article.getDeleteState() == 1) {
                return AppResult.failed(ResultCode.FAILED_ARTICLE_BANNED);
            }


            //调用Service
            articleService.modify(id,title,content);
            //打印日志
            log.info("帖子更新成功。 Article id = " + id + "User id = " + user.getId() + " .");

            return AppResult.success();
        }


        @ApiOperation("点赞")
        @PostMapping("/thumbsUp")
        public AppResult thumbsUp(HttpServletRequest request,
                                  @ApiParam("帖子Id") @RequestParam("id") @NotNull Long id) {
            //获取用户状态
            HttpSession session = request.getSession(false);
            User user = (User) session.getAttribute(AppConfig.USER_SESSION);
            //判断用户是否被禁言
            if(user.getState() == 1) {
                return AppResult.failed(ResultCode.FAILED_USER_BANNED);
            }

            //调用Service
            articleService.thumbsUpById(id);
            return AppResult.success();
        }


        @ApiOperation("删除帖子")
        @PostMapping("/delete")
        private AppResult deleteById(HttpServletRequest request,
                                     @ApiParam("帖子Id") @RequestParam("id") @NotNull Long id) {
            // 校验用户状态
            HttpSession session = request.getSession(false);
            User user = (User) session.getAttribute(AppConfig.USER_SESSION);
            if(user.getState() == 1){
                return AppResult.failed(ResultCode.FAILED_USER_BANNED);
            }
            //查询帖子详情
            Article article = articleService.selectById(id);
            // 校验帖子
            if(article == null || article.getDeleteState() == 1) {
                return AppResult.failed(ResultCode.FAILED_ARTICLE_NOT_EXISTS);
            }
            //校验当前登录用户是不是作者
            if(user.getId() != article.getUserId()) {
             return AppResult.failed(ResultCode.FAILED_FORBIDDEN);
            }
            //调用Service
            articleService.deleteById(id);

            return AppResult.success();
        }
}
