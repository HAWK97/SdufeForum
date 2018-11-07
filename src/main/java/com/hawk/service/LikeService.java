package com.hawk.service;

import com.hawk.async.EventModel;
import com.hawk.async.EventProducer;
import com.hawk.async.EventType;
import com.hawk.data.constant.EntityType;
import com.hawk.data.entity.Comment;
import com.hawk.data.entity.News;
import com.hawk.handle.UserContextHolder;
import com.hawk.util.RedisKeyUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class LikeService {

    @Resource
    private NewsService newsService;

    @Resource
    private CommentService commentService;

    @Resource
    private JedisAdapter jedisAdapter;

    @Resource
    private EventProducer eventProducer;

    public boolean getCurrentLikeStatus(int entityType, Long entityId) {
        if (null == UserContextHolder.get()) {
            return false;
        }
        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
        return jedisAdapter.sismember(likeKey, String.valueOf(UserContextHolder.get().getId()));
    }

    public long getEntityLikeStatus(int entityType, Long entityId) {
        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
        return jedisAdapter.scard(likeKey);
    }

    public long like(int entityType, Long entityId) {
        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
        String dislikeKey = RedisKeyUtil.getDislikeKey(entityType, entityId);
        Long userId = UserContextHolder.get().getId();
        jedisAdapter.sadd(likeKey, String.valueOf(userId));
        // 如果首次点赞，需要给被点赞用户发消息
        if (!jedisAdapter.sismember(dislikeKey, String.valueOf(userId))) {
            // 构造点赞事件对象并调用 EventProducer 的 fireEvent() 方法将该事件对象插入到消息队列中
            // 点赞对象为动态
            if (entityType == EntityType.ENTITY_NEWS) {
                News news = newsService.findOne(entityId);
                if (!userId.equals(news.getUserId())) {
                    eventProducer.fireEvent(new EventModel().setEventType(EventType.LIKE)
                            .setActorId(userId).setEntityType(EntityType.ENTITY_NEWS)
                            .setEntityId(entityId).setEntityOwnerId(news.getUserId())
                    );
                }
            } else {
                // 点赞对象为评论
                Comment comment = commentService.findOne(entityId);
                if (!userId.equals(comment.getUserId())) {
                    eventProducer.fireEvent(new EventModel().setEventType(EventType.LIKE)
                            .setActorId(userId).setEntityType(EntityType.ENTITY_COMMENT)
                            .setEntityId(entityId).setEntityOwnerId(comment.getUserId()));
                }
            }
        } else {
            jedisAdapter.srem(dislikeKey, String.valueOf(userId));
        }
        return jedisAdapter.scard(likeKey);
    }

    public long dislike(int entityType, Long entityId) {
        String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
        String dislikeKey = RedisKeyUtil.getDislikeKey(entityType, entityId);
        jedisAdapter.srem(likeKey, String.valueOf(UserContextHolder.get().getId()));
        jedisAdapter.sadd(dislikeKey, String.valueOf(UserContextHolder.get().getId()));
        return jedisAdapter.scard(likeKey);
    }
}
