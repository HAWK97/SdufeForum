package com.hawk.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    /**
     * 用户 id
     */
    private Long id;

    /**
     * 用户电话
     */
    private String phoneNumber;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 头像 url
     */
    private String avatarUrl;

    /**
     * 个人简介
     */
    private String introduce;

    /**
     * 关注数
     */
    private Long followCount;

    /**
     * 粉丝数
     */
    private Long followedCount;

    /**
     * 当前用户是否关注该用户
     */
    private boolean isCurrentFollow;

    /**
     * 发表动态总数
     */
    private Integer newsCount;
}
