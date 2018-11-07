package com.hawk.service;

import com.hawk.data.constant.EntityType;
import com.hawk.data.dto.CommentDTO;
import com.hawk.data.dto.MessageDTO;
import com.hawk.data.dto.NewsDTO;
import com.hawk.data.dto.UserDTO;
import com.hawk.data.entity.Comment;
import com.hawk.data.entity.Message;
import com.hawk.data.entity.News;
import com.hawk.data.entity.User;
import com.hawk.handle.UserContextHolder;
import com.hawk.util.TimeUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class DTOService {

    @Resource
    private UserService userService;

    @Resource
    private NewsService newsService;

    @Resource
    private CommentService commentService;

    @Resource
    private LikeService likeService;

    @Resource
    private FollowService followService;

    public NewsDTO getNewsDTO(News news) {
        NewsDTO newsDTO = new NewsDTO();
        newsDTO.setId(news.getId());
        newsDTO.setContent(news.getContent());
        newsDTO.setUserId(news.getUserId());
        User user = userService.findOne(news.getUserId());
        newsDTO.setNickName(user.getNickName());
        newsDTO.setAvatarUrl(user.getAvatarUrl());
        newsDTO.setCommentCount(commentService.findByNewsId(news.getId()).size());
        newsDTO.setLikeCount(likeService.getEntityLikeStatus(EntityType.ENTITY_NEWS, news.getId()));
        User currentUser = UserContextHolder.get();
        if (currentUser == null || !news.getUserId().equals(currentUser.getId())) {
            newsDTO.setCurrentCreate(false);
        } else {
            newsDTO.setCurrentCreate(true);
        }
        newsDTO.setCurrentLike(likeService.getCurrentLikeStatus(EntityType.ENTITY_NEWS, news.getId()));
        newsDTO.setCreateTime(TimeUtil.dateToString(news.getCreateTime()));
        return newsDTO;
    }

    public CommentDTO getCommentDTO(Comment comment) {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setId(comment.getId());
        commentDTO.setContent(comment.getContent());
        commentDTO.setUserId(comment.getUserId());
        User user = userService.findOne(comment.getUserId());
        commentDTO.setNickName(user.getNickName());
        commentDTO.setAvatarUrl(user.getAvatarUrl());
        commentDTO.setLikeCount(likeService.getEntityLikeStatus(EntityType.ENTITY_COMMENT, comment.getId()));
        commentDTO.setCurrentLike(likeService.getCurrentLikeStatus(EntityType.ENTITY_COMMENT, comment.getId()));
        User currentUser = UserContextHolder.get();
        if (currentUser == null || !comment.getUserId().equals(currentUser.getId())) {
            commentDTO.setCurrentCreate(false);
        } else {
            commentDTO.setCurrentCreate(true);
        }
        commentDTO.setCreateTime(TimeUtil.dateToString(comment.getCreateTime()));
        return commentDTO;
    }

    public UserDTO getUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setPhoneNumber(user.getPhoneNumber());
        userDTO.setNickName(user.getNickName());
        userDTO.setAvatarUrl(user.getAvatarUrl());
        userDTO.setIntroduce(user.getIntroduce());
        userDTO.setFollowCount(followService.getFollowCount(user.getId()));
        userDTO.setFollowedCount(followService.getFollowedCount(user.getId()));
        userDTO.setCurrentFollow(followService.getCurrentFollowStatus(user.getId()));
        userDTO.setNewsCount(newsService.findByUserId(user.getId()).size());
        return userDTO;
    }

    public MessageDTO getMessageDTO(Message message) {
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setContent(message.getContent());
        messageDTO.setUserId(message.getFromId());
        User user = userService.findOne(message.getFromId());
        messageDTO.setNickName(user.getNickName());
        messageDTO.setAvatarUrl(user.getAvatarUrl());
        messageDTO.setCreateTime(TimeUtil.dateToString(message.getCreateTime()));
        return messageDTO;
    }
}
