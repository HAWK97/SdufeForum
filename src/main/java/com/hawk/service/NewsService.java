package com.hawk.service;

import com.alibaba.fastjson.JSONObject;
import com.hawk.async.EventModel;
import com.hawk.async.EventProducer;
import com.hawk.async.EventType;
import com.hawk.data.constant.ResultEnum;
import com.hawk.data.dto.CommentDTO;
import com.hawk.data.dto.NewsAndComment;
import com.hawk.data.vo.NewsAndCommentList;
import com.hawk.data.dto.NewsDTO;
import com.hawk.data.entity.News;
import com.hawk.exception.MyException;
import com.hawk.handle.UserContextHolder;
import com.hawk.repo.NewsRepo;
import com.hawk.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class NewsService extends BaseService<News, NewsRepo> {

    @Autowired
    public NewsService(NewsRepo newsRepo) {
        super(newsRepo);
    }

    @Resource
    private CommentService commentService;

    @Resource
    private DTOService dtoService;

    @Resource
    private FollowService followService;

    @Resource
    private JedisAdapter jedisAdapter;

    @Resource
    private EventProducer eventProducer;

    @Override
    public News findOne(Long newsId) {
        String newsKey = RedisKeyUtil.getNewsKey(newsId);
        String newsJson = jedisAdapter.get(newsKey);
        if (newsJson == null) {
            News news = repo.getOne(newsId);
            jedisAdapter.set(newsKey, JSONObject.toJSONString(news));
            return news;
        }
        return JSONObject.parseObject(newsJson, News.class);
    }

    @Override
    public News save(News addedNews) {
        News news = new News();
        news.setContent(addedNews.getContent());
        news.setUserId(UserContextHolder.get().getId());
        News saveNews = repo.save(news);
        String newsKey = RedisKeyUtil.getNewsKey(saveNews.getId());
        jedisAdapter.set(newsKey, JSONObject.toJSONString(saveNews));
        String newsListKey = RedisKeyUtil.getNewsListKey();
        String newsId = String.valueOf(saveNews.getId());
        jedisAdapter.lpush(newsListKey, newsId);
        String userNewsListKey = RedisKeyUtil.getUserNewsKey(saveNews.getUserId());
        jedisAdapter.lpush(userNewsListKey, newsId);
        // 当用户发布动态时将动态事件放入消息队列以通知其粉丝
        eventProducer.fireEvent(new EventModel().setEventType(EventType.NEWS)
                .setActorId(UserContextHolder.get().getId()));
        return saveNews;
    }

    @Override
    public void deleteById(Long id) {
        News news = findOne(id);
        if (!news.getUserId().equals(UserContextHolder.get().getId())) {
            throw new MyException(ResultEnum.DELETE_ERROR);
        }
        String newsId = String.valueOf(id);
        String newsListKey = RedisKeyUtil.getNewsListKey();
        jedisAdapter.lrem(newsListKey, newsId);
        String newsKey = RedisKeyUtil.getNewsKey(id);
        jedisAdapter.del(newsKey);
        String userNewsListKey = RedisKeyUtil.getUserNewsKey(news.getUserId());
        jedisAdapter.lrem(userNewsListKey, newsId);
        repo.deleteById(id);
    }

    private List<News> findAll() {
        String newsListKey = RedisKeyUtil.getNewsListKey();
        if (jedisAdapter.llen(newsListKey) == 0) {
            List<News> newsList = repo.findByStatusGreaterThanEqualOrderByCreateTimeDesc(0);
            for (News news : newsList) {
                jedisAdapter.rpush(newsListKey, String.valueOf(news.getId()));
            }
            return newsList;
        }
        return jedisAdapter.lrange(newsListKey).stream().map(Long::valueOf)
                .map(newsId -> findOne(newsId)).collect(Collectors.toList());
    }

    public List<News> findByUserId(Long userId) {
        String userNewsListKey = RedisKeyUtil.getUserNewsKey(userId);
        if (jedisAdapter.llen(userNewsListKey) == 0) {
            List<News> newsList = repo.findByUserIdAndStatusGreaterThanEqualOrderByCreateTimeDesc(userId, 0);
            for (News news : newsList) {
                jedisAdapter.rpush(userNewsListKey, String.valueOf(news.getId()));
            }
            return newsList;
        }
        return jedisAdapter.lrange(userNewsListKey).stream().map(Long::valueOf)
                .map(newsId -> findOne(newsId)).collect(Collectors.toList());
    }

    public NewsAndCommentList getNewsList(int index, int size) {
        List<News> allNewsList = findAll();
        List<News> newsList = allNewsList.stream().skip((index - 1) * size).limit(size).collect(Collectors.toList());
        long totalCount = allNewsList.size();
        List<NewsDTO> newsDTOList = newsList.stream().map(news ->
                dtoService.getNewsDTO(news)).collect(Collectors.toList());
        List<NewsAndComment> newsAndCommentList = new ArrayList<>();
        for (NewsDTO newsDTO : newsDTOList) {
            List<CommentDTO> commentDTOList = commentService.getCommentDTOList(newsDTO.getId());
            NewsAndComment newsAndComment = new NewsAndComment(newsDTO, commentDTOList);
            newsAndCommentList.add(newsAndComment);
        }
        return new NewsAndCommentList(newsAndCommentList, totalCount);
    }

    public NewsAndCommentList getNewsListByUserId(Long userId, int index, int size) {
        List<News> allNewsList = findByUserId(userId);
        List<News> newsList = allNewsList.stream().skip((index - 1) * size).limit(size).collect(Collectors.toList());
        long totalCount = allNewsList.size();
        List<NewsDTO> newsDTOList = newsList.stream().map(news ->
                dtoService.getNewsDTO(news)).collect(Collectors.toList());
        List<NewsAndComment> newsAndCommentList = new ArrayList<>();
        for (NewsDTO newsDTO : newsDTOList) {
            List<CommentDTO> commentDTOList = commentService.getCommentDTOList(newsDTO.getId());
            NewsAndComment newsAndComment = new NewsAndComment(newsDTO, commentDTOList);
            newsAndCommentList.add(newsAndComment);
        }
        return new NewsAndCommentList(newsAndCommentList, totalCount);
    }

    public NewsAndCommentList getFollowNewsList(int index, int size) {
        Set<String> followSet = followService.getFollowSet(UserContextHolder.get().getId());
        List<Long> userIdList = followSet.stream().map(Long::valueOf).collect(Collectors.toList());
        List<News> allNewsList = findAll().stream()
                .filter(news -> userIdList.contains(news.getUserId())).collect(Collectors.toList());
        List<News> newsList = allNewsList.stream().skip((index - 1) * size).limit(size).collect(Collectors.toList());
        long totalCount = allNewsList.size();
        List<NewsDTO> newsDTOList = newsList.stream().map(news -> dtoService.getNewsDTO(news)).collect(Collectors.toList());
        List<NewsAndComment> newsAndCommentList = new ArrayList<>();
        for (NewsDTO newsDTO : newsDTOList) {
            List<CommentDTO> commentDTOList = commentService.getCommentDTOList(newsDTO.getId());
            NewsAndComment newsAndComment = new NewsAndComment(newsDTO, commentDTOList);
            newsAndCommentList.add(newsAndComment);
        }
        return new NewsAndCommentList(newsAndCommentList, totalCount);
    }

    public String getFollowNewsCount() {
        String followNewsKey = RedisKeyUtil.getFollowNewsKey(UserContextHolder.get().getId());
        String followNewsCount = jedisAdapter.get(followNewsKey);
        if (followNewsCount == null) {
            return "{\"followNewsCount\": 0}";
        }
        return "{\"followNewsCount\": " + followNewsCount + "}";
    }

    public void updateFollowNewsCount() {
        String followNewsKey = RedisKeyUtil.getFollowNewsKey(UserContextHolder.get().getId());
        jedisAdapter.del(followNewsKey);
    }
}
