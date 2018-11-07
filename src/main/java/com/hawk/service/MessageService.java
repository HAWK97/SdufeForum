package com.hawk.service;

import com.hawk.data.constant.MessageType;
import com.hawk.data.dto.MessageDTO;
import com.hawk.data.vo.MessageList;
import com.hawk.data.dto.MessageStatus;
import com.hawk.data.entity.Message;
import com.hawk.handle.UserContextHolder;
import com.hawk.repo.MessageRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageService extends BaseService<Message, MessageRepo> {

    @Autowired
    public MessageService(MessageRepo messageRepo) {
        super(messageRepo);
    }

    @Resource
    private DTOService dtoService;

    public MessageStatus getMessageStatus(Long userId) {
        MessageStatus messageStatus = new MessageStatus();
        Integer likeUnReadCount = repo.findByToIdAndReadStateAndMessageType(userId, false, MessageType.MESSAGE_LIKE).size();
        Integer commentUnReadCount = repo.findByToIdAndReadStateAndMessageType(userId, false, MessageType.MESSAGE_COMMIT).size();
        messageStatus.setAllUnReadCount(likeUnReadCount + commentUnReadCount);
        messageStatus.setLikeUnReadCount(likeUnReadCount);
        messageStatus.setCommentUnReadCount(commentUnReadCount);
        return messageStatus;
    }

    public MessageList findByMessageType(Integer messageType, int index, int size) {
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        Pageable pageable = PageRequest.of(index - 1, size, sort);
        Page<Message> messageList = repo.findByToIdAndMessageType(UserContextHolder.get().getId(), messageType, pageable);
        Long totalCount = messageList.getTotalElements();
        List<MessageDTO> messageDTOList = messageList.stream().map(message -> dtoService.getMessageDTO(message)).collect(Collectors.toList());
        return new MessageList(messageDTOList, totalCount);
    }

    public void updateUnRead(Integer messageType) {
        repo.updateUnRead(messageType);
    }
}
