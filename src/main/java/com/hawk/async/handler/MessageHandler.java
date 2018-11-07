package com.hawk.async.handler;

import com.alibaba.fastjson.JSONObject;
import com.hawk.async.EventModel;
import com.hawk.async.EventType;
import com.hawk.data.constant.EntityType;
import com.hawk.data.constant.MessageType;
import com.hawk.data.entity.Comment;
import com.hawk.data.entity.Message;
import com.hawk.data.entity.News;
import com.hawk.service.CommentService;
import com.hawk.service.MessageService;
import com.hawk.service.NewsService;
import com.hawk.service.SocketHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

@Service
public class MessageHandler implements EventHandler {

    @Resource
    private MessageService messageService;

    @Resource
    private NewsService newsService;

    @Resource
    private CommentService commentService;

    @Resource
    private SocketHandler socketHandler;

    @Override
    public void doHandle(EventModel eventModel) {
        Message message = new Message();
        message.setFromId(eventModel.getActorId());
        message.setToId(eventModel.getEntityOwnerId());
        // 该事件为点赞事件
        if (eventModel.getEventType().equals(EventType.LIKE)) {
            // 点赞对象为动态
            if (eventModel.getEntityType() == EntityType.ENTITY_NEWS) {
                News news = newsService.findOne(eventModel.getEntityId());
                // 在私信中，动态只展示前两个字
                message.setContent("赞了您的动态“" + news.getContent().substring(3, 5) + " ...”");
            } else {
                // 点赞对象为评论
                Comment comment = commentService.findOne(eventModel.getEntityId());
                // 在私信中，评论展示全部内容
                message.setContent("赞了您的评论“" + comment.getContent() + "”");
            }
            message.setMessageType(MessageType.MESSAGE_LIKE);
        } else {
            // 该事件为评论事件
            News news = newsService.findOne(eventModel.getEntityId());
            // 在私信中，动态只展示前两个字
            message.setContent("评论了您的动态“" + news.getContent().substring(3, 5) + " ...”："
                    + eventModel.getExt().get("content"));
            message.setMessageType(MessageType.MESSAGE_COMMIT);
        }
        messageService.save(message);
        String messageStatus = JSONObject.toJSONString(messageService.getMessageStatus(eventModel.getEntityOwnerId()));
        // 使用 WebSocket 推送私信
        socketHandler.sendMessageToUser(eventModel.getEntityOwnerId(), new TextMessage(messageStatus));
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LIKE, EventType.COMMENT);
    }
}
