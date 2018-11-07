package com.hawk.async;

import com.alibaba.fastjson.JSONObject;
import com.hawk.async.handler.EventHandler;
import com.hawk.service.JedisAdapter;
import com.hawk.util.RedisKeyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class EventConsumer implements InitializingBean, ApplicationContextAware {

    // 针对每个事件，都有一组 Handler 进行处理，使用该成员变量存储它们的对应关系
    private Map<EventType, List<EventHandler>> config = new HashMap<>();

    private ApplicationContext applicationContext;

    @Resource
    private JedisAdapter jedisAdapter;

    /**
     * 初始化 bean 时的操作
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        // 获得所有 EventHandler 接口的实现类
        Map<String, EventHandler> beans = applicationContext.getBeansOfType(EventHandler.class);
        // 遍历这些实现类，根据它们的 getSupportEventTypes() 方法为 config 成员变量赋值
        for (Map.Entry<String, EventHandler> entry : beans.entrySet()) {
            List<EventType> eventTypeList = entry.getValue().getSupportEventTypes();
            for (EventType eventType : eventTypeList) {
                if (!config.containsKey(eventType)) {
                    config.put(eventType, new ArrayList<>());
                }
                config.get(eventType).add(entry.getValue());
            }
        }

        // 另起一个线程，用于将事件对象从 Redis 链表中弹出并路由到不同的 Handler 进行处理
        new Thread(() -> {
            while (true) {
                String eventQueueKey = RedisKeyUtil.getEventQueueKey();
                // 阻塞时间设为 0，表示若链表为空将一直阻塞下去
                List<String> events = jedisAdapter.brpop(0, eventQueueKey);
                for (String event : events) {
                    if (event.equals(eventQueueKey)) {
                        continue;
                    }
                    EventModel eventModel = JSONObject.parseObject(event, EventModel.class);
                    if (!config.containsKey(eventModel.getEventType())) {
                        log.error("事件无法识别！");
                        continue;
                    }
                    for (EventHandler eventHandler : config.get(eventModel.getEventType())) {
                        eventHandler.doHandle(eventModel);
                    }
                }
            }
        }).start();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
