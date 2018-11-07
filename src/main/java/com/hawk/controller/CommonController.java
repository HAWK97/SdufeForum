package com.hawk.controller;

import com.hawk.annotation.LoginRequired;
import com.hawk.data.constant.EntityType;
import com.hawk.data.dto.ResponseMessage;
import com.hawk.service.FollowService;
import com.hawk.service.LikeService;
import com.hawk.util.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Api(tags = "通用操作接口")
@RestController
@RequestMapping("/news")
public class CommonController {

    @Resource
    private LikeService likeService;

    @Resource
    private FollowService followService;

    @ApiOperation(value = "点赞动态或评论", notes = "传入点赞类型与动态（或评论）id，需要用户登录")
    @GetMapping("/like")
    @LoginRequired
    public ResponseMessage likeEntity(
            @RequestParam("likeType") @ApiParam(name = "likeType", value = "点赞类型，传入1为动态，传入2为评论") int likeType,
            @RequestParam("likeId") @ApiParam(name = "likeId", value = "动态或评论id") Long likeId
    ) {
        if (likeType == 1) {
            return ResultUtil.success(likeService.like(EntityType.ENTITY_NEWS, likeId));
        }
        return ResultUtil.success(likeService.like(EntityType.ENTITY_COMMENT, likeId));
    }

    @ApiOperation(value = "取消对动态或评论的点赞", notes = "传入取消点赞类型与动态（或评论）id，需要用户登录")
    @GetMapping("/dislike")
    @LoginRequired
    public ResponseMessage dislikeEntity(
            @RequestParam("dislikeType") @ApiParam(name = "dislikeType", value = "取消点赞类型，传入1为动态，传入2为评论") int dislikeType,
            @RequestParam("dislikeId") @ApiParam(name = "dislikeId", value = "动态或评论id") Long dislikeId
    ) {
        if (dislikeType == 1) {
            return ResultUtil.success(likeService.dislike(EntityType.ENTITY_NEWS, dislikeId));
        }
        return ResultUtil.success(likeService.dislike(EntityType.ENTITY_COMMENT, dislikeId));
    }

    @ApiOperation(value = "关注用户", notes = "传入关注用户的id，需要用户登录")
    @GetMapping("/follow")
    @LoginRequired
    public ResponseMessage follow(
            @RequestParam("userId") @ApiParam(name = "userId", value = "关注用户的id") Long userId
    ) {
        followService.follow(userId);
        return ResultUtil.success();
    }

    @ApiOperation(value = "取消关注用户", notes = "传入取消关注用户的id，需要用户登录")
    @GetMapping("/unFollow")
    @LoginRequired
    public ResponseMessage unFollow(
            @RequestParam("userId") @ApiParam(name = "userId", value = "取消关注用户的id") Long userId
    ) {
        followService.unFollow(userId);
        return ResultUtil.success();
    }
}
