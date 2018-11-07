package com.hawk.controller;

import com.hawk.annotation.LoginRequired;
import com.hawk.data.constant.MessageType;
import com.hawk.data.dto.ResponseMessage;
import com.hawk.handle.UserContextHolder;
import com.hawk.service.MessageService;
import com.hawk.util.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Api(tags = "站内信操作接口")
@RestController
@RequestMapping("/message")
public class MessageController {

    @Resource
    private MessageService messageService;

    @ApiOperation(value = "获取站内信未读情况", notes = "需要用户登录")
    @GetMapping("/messageStatus")
    @LoginRequired
    public ResponseMessage getMessageStatus() {
        return ResultUtil.success(messageService.getMessageStatus(UserContextHolder.get().getId()));
    }

    @ApiOperation(value = "获取站内信详情", notes = "传入站内信类型、页数与每页大小，需要用户登录。这里特别注意：请求该接口前先请求 /read 接口将未读站内信标为已读！")
    @GetMapping("/messageInfo")
    @LoginRequired
    public ResponseMessage getMessageInfo(
            @RequestParam("messageType") @ApiParam(name = "messageType", value = "站内信类型，2为点赞信息，3为评论信息（1为私信，还没有写...）") Integer messageType,
            @RequestParam(required = false, defaultValue = "1") @ApiParam(name = "index", value = "当前页数，从1开始，默认为1") int index,
            @RequestParam(required = false, defaultValue = "10") @ApiParam(name = "size", value = "每页大小，默认为10") int size
    ) {
        if (messageType == 2) {
            return ResultUtil.success(messageService.findByMessageType(MessageType.MESSAGE_LIKE, index, size));
        }
        return ResultUtil.success(messageService.findByMessageType(MessageType.MESSAGE_COMMIT, index, size));
    }

    @ApiOperation(value = "将未读站内信标为已读", notes = "传入站内信类型，需要用户登录。获取站内信详情前需要先请求该接口。请根据http://goeasy.io/resources/www/docs/goeasy-reference-0.1.18-cn.pdf动态获得站内信未读情况")
    @GetMapping("/read")
    @LoginRequired
    public ResponseMessage updateUnRead(
            @RequestParam("messageType") @ApiParam(name = "messageType", value = "站内信类型，2为点赞信息，3为评论信息（1为私信，还没有写...）") Integer messageType
    ) {
        if (messageType == 2) {
            messageService.updateUnRead(MessageType.MESSAGE_LIKE);
            return ResultUtil.success();
        }
        messageService.updateUnRead(MessageType.MESSAGE_COMMIT);
        return ResultUtil.success();
    }
}
