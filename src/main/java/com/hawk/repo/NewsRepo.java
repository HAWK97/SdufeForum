package com.hawk.repo;

import com.hawk.data.entity.News;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NewsRepo extends BaseRepo<News, Long> {

    @Override
    @Query("select n from News n where n.id=?1")
    News getOne(Long id);

    List<News> findByStatusGreaterThanEqualOrderByCreateTimeDesc(Integer status);

    List<News> findByUserIdAndStatusGreaterThanEqualOrderByCreateTimeDesc(Long userId, Integer status);
}
