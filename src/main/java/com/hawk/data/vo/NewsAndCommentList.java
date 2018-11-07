package com.hawk.data.vo;

import com.hawk.data.dto.NewsAndComment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewsAndCommentList {

    private List<NewsAndComment> newsAndCommentList;

    private Long totalCount;
}
