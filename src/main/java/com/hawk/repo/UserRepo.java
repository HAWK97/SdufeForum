package com.hawk.repo;

import com.hawk.data.entity.User;
import org.springframework.data.jpa.repository.Query;

public interface UserRepo extends BaseRepo<User, Long> {

    @Override
    @Query("select u from User u where u.id=?1")
    User getOne(Long id);

    User findByPhoneNumber(String phoneNumber);

    User findByTicket(String ticket);
}
