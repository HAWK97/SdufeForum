package com.hawk.service;

import com.alibaba.fastjson.JSONObject;
import com.hawk.async.EventModel;
import com.hawk.async.EventProducer;
import com.hawk.async.EventType;
import com.hawk.data.constant.EntityType;
import com.hawk.data.constant.ResultEnum;
import com.hawk.data.dto.CommentDTO;
import com.hawk.data.entity.Comment;
import com.hawk.data.entity.News;
import com.hawk.exception.MyException;
import com.hawk.handle.UserContextHolder;
import com.hawk.repo.CommentRepo;
import com.hawk.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService extends BaseService<Comment, CommentRepo> {

    @Autowired
    public CommentService(CommentRepo commentRepo) {
        super(commentRepo);
    }

    @Resource
    private NewsService newsService;

    @Resource
    private DTOService dtoService;

    @Resource
    private JedisAdapter jedisAdapter;

    @Resource
    private EventProducer eventProducer;

    @Override
    public Comment findOne(Long commentId) {
        String commentKey = RedisKeyUtil.getCommentKey(commentId);
        String commentJson = jedisAdapter.get(commentKey);
        if (commentJson == null) {
            Comment comment = repo.getOne(commentId);
            jedisAdapter.set(commentKey, JSONObject.toJSONString(comment));
            return comment;
        }
        return JSONObject.parseObject(commentJson, Comment.class);
    }

    @Override
    public Comment save(Comment addedComment) {
        Comment comment = new Comment();
        comment.setContent(addedComment.getContent());
        comment.setNewsId(addedComment.getNewsId());
        Long userId = UserContextHolder.get().getId();
        comment.setUserId(userId);
        Comment saveComment = repo.save(comment);
        News news = newsService.findOne(addedComment.getNewsId());
        // 只有评论人不是动态发布人时才将评论事件放入消息队列发送站内信
        // 也就是说自己评论自己发布的动态是不会发送站内信的
        if (!userId.equals(news.getUserId())) {
            eventProducer.fireEvent(new EventModel().setEventType(EventType.COMMENT)
                    .setActorId(userId).setEntityType(EntityType.ENTITY_NEWS)
                    .setEntityId(addedComment.getNewsId()).setEntityOwnerId(news.getUserId())
                    .setExt("content", addedComment.getContent()));
        }
        String commentKey = RedisKeyUtil.getCommentKey(saveComment.getId());
        jedisAdapter.set(commentKey, JSONObject.toJSONString(saveComment));
        String commentListKey = RedisKeyUtil.getCommentListKey(saveComment.getNewsId());
        jedisAdapter.lpush(commentListKey, String.valueOf(saveComment.getId()));
        return saveComment;
    }

    @Override
    public void deleteById(Long id) {
        Comment comment = findOne(id);
        if (!comment.getUserId().equals(UserContextHolder.get().getId())) {
            throw new MyException(ResultEnum.DELETE_ERROR);
        }
        String commentKey = RedisKeyUtil.getCommentKey(id);
        jedisAdapter.del(commentKey);
        String commentListKey = RedisKeyUtil.getCommentListKey(comment.getNewsId());
        jedisAdapter.lrem(commentListKey, String.valueOf(id));
        repo.deleteById(id);
    }

    public List<Comment> findByNewsId(Long newsId) {
        String commentListKey = RedisKeyUtil.getCommentListKey(newsId);
        if (jedisAdapter.llen(commentListKey) == 0) {
            List<Comment> commentList = repo.findByNewsIdAndStatusGreaterThanEqualOrderByCreateTimeDesc(newsId, 0);
            for (Comment comment : commentList) {
                jedisAdapter.rpush(commentListKey, String.valueOf(comment.getId()));
            }
            return commentList;
        }
        return jedisAdapter.lrange(commentListKey).stream().map(Long::valueOf)
                .map(commentId -> findOne(commentId)).collect(Collectors.toList());
    }

    public List<CommentDTO> getCommentDTOList(Long newsId) {
        return findByNewsId(newsId).stream().map(comment -> dtoService.getCommentDTO(comment))
                .sorted(Comparator.comparing(CommentDTO::getLikeCount).reversed()).collect(Collectors.toList());
    }
}
