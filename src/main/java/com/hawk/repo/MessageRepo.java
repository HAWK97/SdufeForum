package com.hawk.repo;

import com.hawk.data.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(rollbackFor = Exception.class)
public interface MessageRepo extends BaseRepo<Message, Long> {

    Page<Message> findByToIdAndMessageType(Long toId, Integer messageType, Pageable pageable);

    List<Message> findByToIdAndReadStateAndMessageType(Long toId, Boolean readState, Integer messageType);

    @Modifying
    @Query("update Message m set m.readState=true where m.messageType=?1 and m.readState=false")
    void updateUnRead(Integer messageType);
}
