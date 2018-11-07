package com.hawk.async.handler;

import com.hawk.async.EventModel;
import com.hawk.async.EventType;
import com.hawk.service.FollowService;
import com.hawk.service.JedisAdapter;
import com.hawk.service.SocketHandler;
import com.hawk.util.RedisKeyUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class NewsHandler implements EventHandler {

    @Resource
    private FollowService followService;

    @Resource
    private JedisAdapter jedisAdapter;

    @Resource
    private SocketHandler socketHandler;

    @Override
    public void doHandle(EventModel eventModel) {
        Set<String> followedSet = followService.getFollowedSet(eventModel.getActorId());
        List<Long> userIdList = followedSet.stream().map(Long::valueOf).collect(Collectors.toList());
        for (Long userId : userIdList) {
            String followNewsKey = RedisKeyUtil.getFollowNewsKey(userId);
            jedisAdapter.incr(followNewsKey);
            String followNewsCount = "{\"followNewsCount\": " + jedisAdapter.get(followNewsKey) + "}";
            // 使用 WebSocket 推送关注动态
            socketHandler.sendMessageToUser(userId, new TextMessage(followNewsCount));
        }
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.NEWS);
    }
}
