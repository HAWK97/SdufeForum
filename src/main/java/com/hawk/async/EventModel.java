package com.hawk.async;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
public class EventModel {

    /**
     * 事件类型，用 EventType 枚举类表示，这里指该事件属于点赞还是评论
     */
    @Getter
    private EventType eventType;

    /**
     * 触发该事件的用户 id，这里指进行点赞或评论操作的用户 id
     */
    @Getter
    private Long actorId;

    /**
     * 被点赞或评论的对象类型，1 为动态，2 为评论
     */
    @Getter
    private Integer entityType;

    /**
     * 被点赞或评论的对象 id
     */
    @Getter
    private Long entityId;

    /**
     * 被点赞或评论对象的拥有者 id，即将要收到私信的用户 id
     */
    @Getter
    private Long entityOwnerId;

    /**
     * 用于保存该事件的一些附加信息
     */
    private Map<String, String> exts = new HashMap<>();

    public EventModel setEventType(EventType eventType) {
        this.eventType = eventType;
        return this;
    }

    public EventModel setActorId(Long actorId) {
        this.actorId = actorId;
        return this;
    }

    public EventModel setEntityType(Integer entityType) {
        this.entityType = entityType;
        return this;
    }

    public EventModel setEntityId(Long entityId) {
        this.entityId = entityId;
        return this;
    }

    public EventModel setEntityOwnerId(Long entityOwnerId) {
        this.entityOwnerId = entityOwnerId;
        return this;
    }

    public Map<String, String> getExt() {
        return this.exts;
    }

    public EventModel setExt(String key, String value) {
        exts.put(key, value);
        return this;
    }
}
