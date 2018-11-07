package com.hawk.controller;

import com.hawk.annotation.LoginRequired;
import com.hawk.data.dto.ResponseMessage;
import com.hawk.data.entity.Comment;
import com.hawk.data.entity.News;
import com.hawk.service.CommentService;
import com.hawk.service.NewsService;
import com.hawk.service.QiniuService;
import com.hawk.util.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Api(tags = "动态操作接口")
@RestController
@RequestMapping("/news")
public class NewsController {

    @Resource
    private NewsService newsService;

    @Resource
    private CommentService commentService;

    @Resource
    private QiniuService qiniuService;

    @ApiOperation(value = "获得图片上传凭证")
    @GetMapping("/token")
    public String getToken() {
        return "{\"uptoken\": \"" + qiniuService.getUpToken() + "\"}";
    }

    @ApiOperation(value = "发布动态",notes = "传入动态内容与动态图片链接，需要用户登录")
    @PostMapping("/addNews")
    @LoginRequired
    public ResponseMessage addNews(
            @RequestBody @ApiParam(name = "news", value = "新增动态，包括动态内容与动态图片链接") News news
    ) {
        newsService.save(news);
        return ResultUtil.success();
    }

    @ApiOperation(value = "发表评论", notes = "传入评论内容与被评论的动态id，需要用户登录")
    @PostMapping("/addComment")
    @LoginRequired
    public ResponseMessage addComment(
            @RequestBody @ApiParam(name = "comment", value = "新增评论，包括评论内容与被评论的动态id")Comment comment) {
        commentService.save(comment);
        return ResultUtil.success();
    }

    @ApiOperation(value = "获取动态列表", notes = "需要传入页数与每页大小")
    @GetMapping("/newsList")
    public ResponseMessage getNewsList(
            @RequestParam(required = false, defaultValue = "1") @ApiParam(name = "index", value = "当前页数，从1开始，默认为1") int index,
            @RequestParam(required = false, defaultValue = "10") @ApiParam(name = "size", value = "每页大小，默认为10") int size
    ) {
        return ResultUtil.success(newsService.getNewsList(index, size));
    }

    @ApiOperation(value = "根据用户id获取动态列表", notes = "需要传入用户id，页数与每页大小")
    @GetMapping("/{userId}/newsList")
    public ResponseMessage getNewsListByUserId(
            @PathVariable("userId") @ApiParam(name = "userId", value = "用户id") Long userId,
            @RequestParam(required = false, defaultValue = "1") @ApiParam(name = "index", value = "当前页数，从1开始，默认为1") int index,
            @RequestParam(required = false, defaultValue = "10") @ApiParam(name = "size", value = "每页大小，默认为10") int size
    ) {
        return ResultUtil.success(newsService.getNewsListByUserId(userId, index, size));
    }

    @ApiOperation(value = "获取当前用户关注动态列表", notes = "传入页数与每页大小，需要用户登录")
    @GetMapping("/followNewsList")
    @LoginRequired
    public ResponseMessage getFollowNewsList(
            @RequestParam(required = false, defaultValue = "1") @ApiParam(name = "index", value = "当前页数，从1开始，默认为1") int index,
            @RequestParam(required = false, defaultValue = "10") @ApiParam(name = "size", value = "每页大小，默认为10") int size
    ) {
        return ResultUtil.success(newsService.getFollowNewsList(index, size));
    }

    @ApiOperation(value = "根据动态id获取评论", notes = "需要传入动态id")
    @GetMapping("/commentList")
    public ResponseMessage getCommentList(
            @RequestParam("newsId") @ApiParam(name = "newsId", value = "动态id") Long newsId
    ) {
        return ResultUtil.success(commentService.getCommentDTOList(newsId));
    }

    @ApiOperation(value = "删除动态或评论", notes = "传入删除类型与动态（或评论）id，需要用户登录")
    @LoginRequired
    @DeleteMapping("/delete")
    public ResponseMessage deleteEntity(
            @RequestParam("deleteType") @ApiParam(name = "deleteType", value = "删除类型，传入1为动态，传入2为评论") int deleteType,
            @RequestParam("deleteId") @ApiParam(name = "deleteId", value = "动态或评论id") Long deleteId
    ) {
        if (deleteType == 1) {
            newsService.deleteById(deleteId);
            return ResultUtil.success();
        }
        commentService.deleteById(deleteId);
        return ResultUtil.success();
    }

    @ApiOperation(value = "获取关注动态数量", notes = "需要用户登录")
    @LoginRequired
    @GetMapping("/followNewsCount")
    public String getFollowNewsCount() {
        return newsService.getFollowNewsCount();
    }

    @ApiOperation(value = "查看关注动态后将关注动态数量清零", notes = "需要用户登录")
    @LoginRequired
    @GetMapping("/updateFollowNewsCount")
    public ResponseMessage updateFollowNewsCount() {
        newsService.updateFollowNewsCount();
        return ResultUtil.success();
    }
}
