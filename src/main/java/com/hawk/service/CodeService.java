package com.hawk.service;

import com.hawk.data.entity.Code;
import com.hawk.repo.CodeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CodeService extends BaseService<Code, CodeRepo> {

    @Autowired
    public CodeService(CodeRepo codeRepo) {
        super(codeRepo);
    }

    @Override
    public Code save(Code code) {
        Code oldCode = findByPhoneNumber(code.getPhoneNumber());
        if (null != oldCode) {
            oldCode.setRandomCode(code.getRandomCode());
            oldCode.setUpdateTime(code.getCreateTime());
            return repo.save(oldCode);
        }
        return repo.save(code);
    }

    public Code findByPhoneNumber(String phoneNumber) {
        return repo.findByPhoneNumber(phoneNumber);
    }
}
