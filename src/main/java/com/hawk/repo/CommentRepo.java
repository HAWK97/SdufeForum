package com.hawk.repo;

import com.hawk.data.entity.Comment;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepo extends BaseRepo<Comment, Long> {

    @Override
    @Query("select c from Comment c where c.id=?1")
    Comment getOne(Long id);

    List<Comment> findByNewsIdAndStatusGreaterThanEqualOrderByCreateTimeDesc(Long newsId, Integer status);
}
