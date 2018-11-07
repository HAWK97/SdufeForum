package com.hawk.service;

import com.alibaba.fastjson.JSONObject;
import com.hawk.data.constant.ResultEnum;
import com.hawk.data.dto.ModifiedUser;
import com.hawk.data.dto.UserDTO;
import com.hawk.data.entity.Code;
import com.hawk.data.entity.User;
import com.hawk.data.vo.UserList;
import com.hawk.exception.MyException;
import com.hawk.handle.UserContextHolder;
import com.hawk.repo.UserRepo;
import com.hawk.util.PasswordUtil;
import com.hawk.util.RedisKeyUtil;
import com.hawk.util.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService extends BaseService<User, UserRepo> {

    @Autowired
    public UserService(UserRepo userRepo) {
        super(userRepo);
    }

    @Resource
    private CodeService codeService;

    @Resource
    private DTOService dtoService;

    @Resource
    private FollowService followService;

    @Resource
    private JedisAdapter jedisAdapter;

    @Override
    public User save(User user) {
        User saveUser = repo.save(user);
        String userKey = RedisKeyUtil.getUserKey(saveUser.getTicket());
        jedisAdapter.set(userKey, JSONObject.toJSONString(saveUser));
        return saveUser;
    }

    public User findByTicket(String ticket) {
        String userKey = RedisKeyUtil.getUserKey(ticket);
        String userJson = jedisAdapter.get(userKey);
        if (userJson == null) {
            User user = repo.findByTicket(ticket);
            jedisAdapter.set(userKey, JSONObject.toJSONString(ticket));
            return user;
        }
        return JSONObject.parseObject(userJson, User.class);
    }

    public User findByPhoneNumber(String phoneNumber) {
        return repo.findByPhoneNumber(phoneNumber);
    }

    public User register(User registerUser, String randomCode) {
        if (StringUtils.isEmpty(registerUser.getPhoneNumber())) {
            throw new MyException(ResultEnum.EMPTY_USERNAME);
        }
        if (StringUtils.isEmpty(registerUser.getPassword())) {
            throw new MyException(ResultEnum.EMPTY_PASSWORD);
        }
        if (null != findByPhoneNumber(registerUser.getPhoneNumber())) {
            throw new MyException(ResultEnum.REPEAT_REGISTER);
        }
        Code code = codeService.findByPhoneNumber(registerUser.getPhoneNumber());
        if (null == code || !code.getRandomCode().equals(randomCode)) {
            throw new MyException(ResultEnum.CODE_ERROR);
        }
        if (TimeUtil.plusMinutes(code.getUpdateTime(), 5).before(new Date())) {
            throw new MyException(ResultEnum.CODE_TIME_OUT);
        }
        User user = new User();
        user.setUserName("微享财大用户" + PasswordUtil.get10UUID());
        user.setPhoneNumber(registerUser.getPhoneNumber());
        user.setNickName(registerUser.getNickName());
        String salt = PasswordUtil.get5UUID();
        user.setPassword(PasswordUtil.getPassword(registerUser.getPassword(), salt));
        user.setSalt(salt);
        String ticket = PasswordUtil.get10UUID();
        user.setTicket(ticket);
        return save(user);
    }

    public User login(User loginUser) {
        if (StringUtils.isEmpty(loginUser.getPhoneNumber())) {
            throw new MyException(ResultEnum.EMPTY_USERNAME);
        }
        if (StringUtils.isEmpty(loginUser.getPassword())) {
            throw new MyException(ResultEnum.EMPTY_PASSWORD);
        }
        User user = findByPhoneNumber(loginUser.getPhoneNumber());
        if (null == user) {
            throw new MyException(ResultEnum.USER_NOT_EXIST);
        }
        if (!user.getPassword().equals(PasswordUtil.getPassword(loginUser.getPassword(), user.getSalt()))) {
            throw new MyException(ResultEnum.PASSWORD_ERROR);
        }
        return user;
    }

    public void updateAvatarUrl(String avatarUrl) {
        User user = UserContextHolder.get();
        user.setAvatarUrl(avatarUrl);
        save(user);
    }

    public void modifyInfo(ModifiedUser modifiedUser) {
        User user = UserContextHolder.get();
        String nickName = modifiedUser.getNickName();
        if (StringUtils.isEmpty(nickName)) {
            throw new MyException(ResultEnum.EMPTY_NICKNAME);
        }
        user.setNickName(nickName);
        user.setIntroduce(modifiedUser.getIntroduce());
        save(user);
    }

    public void modifyPhoneNumber(String phoneNumber, String randomCode) {
        User user = UserContextHolder.get();
        Code code = codeService.findByPhoneNumber(phoneNumber);
        if (null == code || !code.getRandomCode().equals(randomCode)) {
            throw new MyException(ResultEnum.CODE_ERROR);
        }
        if (TimeUtil.plusMinutes(code.getUpdateTime(), 5).before(new Date())) {
            throw new MyException(ResultEnum.CODE_TIME_OUT);
        }
        user.setPhoneNumber(phoneNumber);
        save(user);
    }

    public void modifyPassword(String oldPassword, String newPassword) {
        User user = UserContextHolder.get();
        if (StringUtils.isEmpty(newPassword)) {
            throw new MyException(ResultEnum.EMPTY_PASSWORD);
        }
        if (!user.getPassword().equals(PasswordUtil.getPassword(oldPassword, user.getSalt()))) {
            throw new MyException(ResultEnum.PASSWORD_ERROR);
        }
        user.setPassword(PasswordUtil.getPassword(newPassword, user.getSalt()));
        save(user);
    }

    public UserList getFollowList(Long userId, int index, int size) {
        Set<String> followSet = followService.getFollowSet(userId);
        List<Long> userIdList = followSet.stream().map(Long::valueOf).collect(Collectors.toList());
        List<User> userList = new ArrayList<>();
        for (Long id : userIdList) {
            User user = findOne(id);
            userList.add(user);
        }
        Integer totalCount = userList.size();
        List<UserDTO> userDTOList = userList.stream().map(user ->
                dtoService.getUserDTO(user)).skip((index - 1) * size).limit(size).collect(Collectors.toList());
        return new UserList(userDTOList, totalCount);
    }

    public UserList getFollowedList(Long userId, int index, int size) {
        Set<String> followedSet = followService.getFollowedSet(userId);
        List<Long> userIdList = followedSet.stream().map(Long::valueOf).collect(Collectors.toList());
        List<User> userList = new ArrayList<>();
        for (Long id : userIdList) {
            User user = findOne(id);
            userList.add(user);
        }
        Integer totalCount = userList.size();
        List<UserDTO> userDTOList = userList.stream().map(user ->
                dtoService.getUserDTO(user)).skip((index - 1) * size).limit(size).collect(Collectors.toList());
        return new UserList(userDTOList, totalCount);
    }
}
