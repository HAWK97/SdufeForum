package com.hawk.data.vo;

import com.hawk.data.dto.MessageDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageList {

    private List<MessageDTO> messageDTOList;

    private Long totalCount;
}
