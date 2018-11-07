package com.hawk.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewsDTO {

    /**
     * 动态 id
     */
    private Long id;

    /**
     * 动态内容
     */
    private String content;

    /**
     * 发布该动态的用户 id
     */
    private Long userId;

    /**
     * 发布该动态的用户昵称
     */
    private String nickName;

    /**
     * 发布该动态的用户头像链接
     */
    private String avatarUrl;

    /**
     * 评论数
     */
    private int commentCount;

    /**
     * 点赞数
     */
    private long likeCount;

    /**
     * 当前用户是否点赞了该动态
     */
    private boolean isCurrentLike;

    /**
     * 当前用户是否发布了该动态
     */
    private boolean isCurrentCreate;

    /**
     * 动态创建时间
     */
    private String createTime;
}
