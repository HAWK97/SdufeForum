package com.hawk.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageStatus {

    /**
     * 全部未读数
     */
    private Integer allUnReadCount;

    /**
     * 点赞消息未读数
     */
    private Integer likeUnReadCount;

    /**
     * 评论消息未读数
     */
    private Integer CommentUnReadCount;
}
