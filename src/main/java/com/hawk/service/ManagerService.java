package com.hawk.service;

import com.hawk.data.entity.Code;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

@Service
public class ManagerService {

    @Resource
    private UserService userService;

    @Resource
    private CodeService codeService;

    @Resource
    private SendSmsService sendSmsService;

    @Resource
    private QiniuService qiniuService;

    public void sendCode(String phoneNumber) {
        String randomCode = RandomStringUtils.randomNumeric(6);
        if (sendSmsService.sendSms(phoneNumber, randomCode)) {
            Code code = new Code();
            code.setPhoneNumber(phoneNumber);
            code.setRandomCode(randomCode);
            codeService.save(code);
        }
    }

    public void setAvatar(MultipartFile avatarImage) {
        String avatarUrl = qiniuService.upload(avatarImage);
        userService.updateAvatarUrl(avatarUrl);
    }
}
