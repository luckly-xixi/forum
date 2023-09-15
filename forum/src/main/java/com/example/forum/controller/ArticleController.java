package com.example.forum.controller;


import com.example.forum.common.AppResult;
import com.example.forum.common.ResultCode;
import com.example.forum.config.AppConfig;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


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
}
