package com.hawk.async;

import com.alibaba.fastjson.JSONObject;
import com.hawk.service.JedisAdapter;
import com.hawk.util.RedisKeyUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class EventProducer {

    @Resource
    private JedisAdapter jedisAdapter;

    public void fireEvent(EventModel eventModel) {
        String eventModelJson = JSONObject.toJSONString(eventModel);
        String eventQueueKey = RedisKeyUtil.getEventQueueKey();
        jedisAdapter.lpush(eventQueueKey, eventModelJson);
    }
}
