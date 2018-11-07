package com.hawk.data.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "message")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
// todo 没有确定前端表现形式，暂时不写私信
public class Message extends BaseEntity {

    /**
     * 发送方 id
     */
    private Long fromId;

    /**
     * 接收方 id
     */
    private Long toId;

    /**
     * 内容
     */
    private String content;

    /**
     * 是否已阅
     */
    private Boolean readState = false;

    /**
     * 会话 id
     */
    private String conversationId;

    /**
     * 站内信类型，1 为私信，2 为点赞信息，3 为评论信息
     */
    private Integer messageType;
}
