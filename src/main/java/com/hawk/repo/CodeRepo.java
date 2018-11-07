package com.hawk.repo;

import com.hawk.data.entity.Code;

public interface CodeRepo extends BaseRepo<Code, Long> {

    Code findByPhoneNumber(String phoneNumber);
}
