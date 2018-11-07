package com.hawk.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {

    /**
     * 评论 id
     */
    private Long id;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 发表该评论的用户 id
     */
    private Long userId;

    /**
     * 发表该评论的用户昵称
     */
    private String nickName;

    /**
     * 发表该评论的用户头像链接
     */
    private String avatarUrl;

    /**
     * 点赞数
     */
    private long likeCount;

    /**
     * 当前用户是否点赞了该评论
     */
    private boolean isCurrentLike;

    /**
     * 当前用户是否发表了该评论
     */
    private boolean isCurrentCreate;

    /**
     * 评论创建时间
     */
    private String createTime;
}
