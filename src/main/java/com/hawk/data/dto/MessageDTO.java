package com.hawk.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {

    /**
     * 消息内容
     */
    private String content;

    /**
     * 发送该消息的用户 id
     */
    private Long userId;

    /**
     * 发送该消息的用户昵称
     */
    private String nickName;

    /**
     * 发送该消息的用户头像链接
     */
    private String avatarUrl;

    /**
     * 消息创建时间
     */
    private String createTime;
}
