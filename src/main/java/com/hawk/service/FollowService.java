package com.hawk.service;

import com.hawk.handle.UserContextHolder;
import com.hawk.util.RedisKeyUtil;
import com.hawk.util.TimeUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Set;

@Service
public class FollowService {

    @Resource
    private JedisAdapter jedisAdapter;

    public boolean getCurrentFollowStatus(Long userId) {
        if (null == UserContextHolder.get()) {
            return false;
        }
        String followKey = RedisKeyUtil.getFollowKey(UserContextHolder.get().getId());
        return jedisAdapter.zismember(followKey, String.valueOf(userId));
    }

    public long getFollowCount(Long userId) {
        String followKey = RedisKeyUtil.getFollowKey(userId);
        return jedisAdapter.zcard(followKey);
    }

    public long getFollowedCount(Long userId) {
        String followedKey = RedisKeyUtil.getFollowedKey(userId);
        return jedisAdapter.zcard(followedKey);
    }

    public void follow(Long userId) {
        String followKey = RedisKeyUtil.getFollowKey(UserContextHolder.get().getId());
        String followedKey = RedisKeyUtil.getFollowedKey(userId);
        Double timeStamp = TimeUtil.dateToDouble(new Date());
        jedisAdapter.zadd(followKey, timeStamp, String.valueOf(userId));
        jedisAdapter.zadd(followedKey, timeStamp, String.valueOf(UserContextHolder.get().getId()));
    }

    public void unFollow(Long userId) {
        String followKey = RedisKeyUtil.getFollowKey(UserContextHolder.get().getId());
        String followedKey = RedisKeyUtil.getFollowedKey(userId);
        jedisAdapter.zrem(followKey, String.valueOf(userId));
        jedisAdapter.zrem(followedKey, String.valueOf(UserContextHolder.get().getId()));
    }

    public Set<String> getFollowSet(Long userId) {
        String followKey = RedisKeyUtil.getFollowKey(userId);
        return jedisAdapter.zrevrange(followKey);
    }

    public Set<String> getFollowedSet(Long userId) {
        String followedKey = RedisKeyUtil.getFollowedKey(userId);
        return jedisAdapter.zrevrange(followedKey);
    }
}
