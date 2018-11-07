package com.hawk.async.handler;

import com.hawk.async.EventModel;
import com.hawk.async.EventType;

import java.util.List;

public interface EventHandler {

    /**
     * 进行具体处理操作的方法
     *
     * @param eventModel
     */
    void doHandle(EventModel eventModel);

    /**
     * 返回该处理类支持的事件类型
     *
     * @return
     */
    List<EventType> getSupportEventTypes();
}
