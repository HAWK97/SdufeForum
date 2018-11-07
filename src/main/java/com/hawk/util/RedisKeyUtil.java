package com.hawk.util;

public class RedisKeyUtil {

    private static String SPLIT = ":";

    private static String BIZ_LIKE = "LIKE";

    private static String BIZ_DISLIKE = "DISLIKE";

    private static String BIZ_FOLLOW = "FOLLOWING";

    private static String BIZ_FOLLOWED = "FOLLOWED_BY";

    private static String BIZ_EVENT = "EVENT";

    private static String BIZ_USER = "USER";

    private static String BIZ_NEWS = "NEWS";

    private static String BIZ_COMMENT = "COMMENT";

    public static String getLikeKey(int entityType, Long entityId) {
        return BIZ_LIKE + SPLIT + String.valueOf(entityType) + SPLIT + String.valueOf(entityId);
    }

    public static String getDislikeKey(int entityType, Long entityId) {
        return BIZ_DISLIKE + SPLIT + String.valueOf(entityType) + SPLIT + String.valueOf(entityId);
    }

    public static String getFollowKey(Long userId) {
        return BIZ_FOLLOW + SPLIT + BIZ_USER + SPLIT + String.valueOf(userId);
    }

    public static String getFollowedKey(Long userId) {
        return BIZ_FOLLOWED + SPLIT + BIZ_USER + SPLIT + String.valueOf(userId);
    }

    public static String getEventQueueKey() {
        return BIZ_EVENT;
    }

    public static String getNewsKey(Long newsId) {
        return BIZ_NEWS + SPLIT + String.valueOf(newsId);
    }

    public static String getNewsListKey() {
        return BIZ_NEWS;
    }

    public static String getUserNewsKey(Long userId) {
        return BIZ_NEWS + SPLIT + BIZ_USER + SPLIT + String.valueOf(userId);
    }

    public static String getCommentKey(Long commentId) {
        return BIZ_COMMENT + SPLIT + String.valueOf(commentId);
    }

    public static String getCommentListKey(Long newsId) {
        return BIZ_NEWS + SPLIT + String.valueOf(newsId) + SPLIT + BIZ_COMMENT;
    }

    public static String getUserKey(String ticket) {
        return BIZ_USER + SPLIT + ticket;
    }

    public static String getFollowNewsKey(Long userId) {
        return BIZ_NEWS + SPLIT + BIZ_FOLLOW + SPLIT + String.valueOf(userId);
    }
}
