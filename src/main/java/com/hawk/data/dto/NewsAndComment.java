package com.hawk.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewsAndComment {

    private NewsDTO newsDTO;

    private List<CommentDTO> commentDTOList;
}
