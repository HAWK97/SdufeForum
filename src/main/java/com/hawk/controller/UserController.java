package com.hawk.controller;

import com.hawk.annotation.LoginRequired;
import com.hawk.data.constant.ResultEnum;
import com.hawk.data.dto.ModifiedUser;
import com.hawk.data.dto.ResponseMessage;
import com.hawk.data.entity.User;
import com.hawk.service.DTOService;
import com.hawk.service.ManagerService;
import com.hawk.service.UserService;
import com.hawk.util.DigestUtil;
import com.hawk.util.ResultUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Api(tags = "用户操作接口")
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private ManagerService managerService;

    @Resource
    private DTOService dtoService;

    @ApiOperation(value = "用户注册", notes = "需要传入用户名、密码、是否保持登录状态和验证码")
    @PostMapping("/register")
    public ResponseMessage register(
            @RequestBody @ApiParam(name = "registerUser", value = "注册用户，包括用户名、密码") User registerUser,
            @RequestParam(value = "rememberMe", defaultValue = "false")
            @ApiParam(name = "rememberMe", value = "是否保持登录状态") boolean rememberMe,
            @RequestParam(value = "randomCode") @ApiParam(name = "randomCode", value = "收到的验证码") String randomCode,
            HttpServletRequest request, HttpServletResponse response
    ) {
        User user = userService.register(registerUser, randomCode);
        return addCookie(user, rememberMe, request, response);
    }

    @ApiOperation(value = "用户登录", notes = "需要传入用户名、密码以及是否保持登录状态")
    @PostMapping("/login")
    public ResponseMessage login(
            @RequestBody @ApiParam(name = "loginUser", value = "登录用户，包括用户名、密码") User loginUser,
            @RequestParam(value = "rememberMe", defaultValue = "false")
            @ApiParam(name = "rememberMe", value = "是否保持登录状态") boolean rememberMe,
            HttpServletRequest request, HttpServletResponse response
    ) {
        User user = userService.login(loginUser);
        return addCookie(user, rememberMe, request, response);
    }

    private ResponseMessage addCookie(User user, boolean rememberMe, HttpServletRequest request, HttpServletResponse response) {
        Cookie cookie = new Cookie("ticket", DigestUtil.Encrypt(user.getTicket()));
        cookie.setPath("/");
        if (rememberMe) {
            // 设置Cookie失效时间为7天
            cookie.setMaxAge(3600 * 24 * 7);
        }
        response.addCookie(cookie);
        request.getSession().setAttribute("user", user);
        return ResultUtil.success();
    }

    @ApiOperation(value = "用户登出", notes = "需要用户登录")
    @GetMapping(value = "/logout")
    @LoginRequired
    public ResponseMessage logout(HttpServletRequest request, HttpServletResponse response) {
        Cookie cookie = new Cookie("ticket", null);
        cookie.setPath("/");
        // 设置Cookie无效
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        request.getSession().removeAttribute("user");
        return ResultUtil.success();
    }

    @ApiOperation(value = "获取用户信息", notes = "需要传入用户id，若不传入id，则为获取当前用户信息")
    @GetMapping(value = "/info")
    public ResponseMessage getUser(
            @RequestParam(required = false, defaultValue = "0") @ApiParam(name = "userId", value = "用户id，若不传入id，则为获取当前用户信息") Long userId,
            HttpServletRequest request
    ) {
        if (userId == 0) {
            if (request.getSession().getAttribute("user") == null) {
                return ResultUtil.error(ResultEnum.NEED_LOGIN);
            }
            return ResultUtil.success(dtoService.getUserDTO((User) request.getSession().getAttribute("user")));
        }
        return ResultUtil.success(dtoService.getUserDTO(userService.findOne(userId)));
    }

    @ApiOperation(value = "发送短信验证码", notes = "需要传入用户手机号")
    @GetMapping("/code")
    public ResponseMessage sendCode(
            @RequestParam(value = "phoneNumber") @ApiParam(name = "phoneNumber", value = "用户手机号") String phoneNumber) {
        managerService.sendCode(phoneNumber);
        return ResultUtil.success();
    }

    @ApiOperation(value = "修改用户头像、昵称与个人简介", notes = "传入修改后的头像图片、用户昵称与个人简介，需要用户登录")
    @PostMapping("/modifyInfo")
    @LoginRequired
    public ResponseMessage modifyInfo(
            @ApiParam(name = "modifiedUser", value = "修改后的用户，包括昵称、个人简介") ModifiedUser modifiedUser,
            @RequestParam(name = "avatarImage", required = false)
            @ApiParam(name = "avatarImage", value = "用户头像图片，可选格式为：bmp, jpg, jpeg, png, gif") MultipartFile avatarImage
    ) {
        if (null != avatarImage) {
            managerService.setAvatar(avatarImage);
        }
        userService.modifyInfo(modifiedUser);
        return ResultUtil.success();
    }

    @ApiOperation(value = "修改用户手机号", notes = "需要向修改后的手机号发送验证码并在url中传入验证码（类似于注册），" +
            "在请求体中传入修改后的手机号")
    @PostMapping("/phoneNumber")
    @LoginRequired
    public ResponseMessage modifyPhoneNumber(
            @RequestBody @ApiParam(name = "params", value = "修改后的手机号，形式为{\"phoneNumber\": 手机号}") Map<String, String> params,
            @RequestParam(value = "randomCode") @ApiParam(name = "randomCode", value = "收到的验证码") String randomCode
    ) {
        String phoneNumber = params.get("phoneNumber");
        userService.modifyPhoneNumber(phoneNumber, randomCode);
        return ResultUtil.success();
    }

    @ApiOperation(value = "修改用户密码", notes = "在请求体中传入旧密码和修改后的密码")
    @PostMapping("/password")
    @LoginRequired
    public ResponseMessage modifyPassword(
            @RequestBody @ApiParam(name = "params", value = "旧密码和修改后的密码，" +
                    "形式为{\"oldPassword\": 旧密码, \"newPassword\": 修改后的密码}") Map<String, String> params
    ) {
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");
        userService.modifyPassword(oldPassword, newPassword);
        return ResultUtil.success();
    }

    @ApiOperation(value = "获取关注列表", notes = "需要传入用户id，显示页数与每页大小")
    @GetMapping(value = "/{userId}/followList")
    public ResponseMessage getFollowList(
            @PathVariable("userId") @ApiParam(name = "userId", value = "用户id") Long userId,
            @RequestParam(required = false, defaultValue = "1") @ApiParam(name = "index", value = "当前页数，从1开始，默认为1") int index,
            @RequestParam(required = false, defaultValue = "10") @ApiParam(name = "size", value = "每页大小，默认为10") int size
    ) {
        return ResultUtil.success(userService.getFollowList(userId, index, size));
    }

    @ApiOperation(value = "获取粉丝列表", notes = "需要传入用户id，显示页数与每页大小")
    @GetMapping(value = "/{userId}/followedList")
    public ResponseMessage getFollowedList(
            @PathVariable("userId") @ApiParam(name = "userId", value = "用户id") Long userId,
            @RequestParam(required = false, defaultValue = "1") @ApiParam(name = "index", value = "当前页数，从1开始，默认为1") int index,
            @RequestParam(required = false, defaultValue = "10") @ApiParam(name = "size", value = "每页大小，默认为10") int size
    ) {
        return ResultUtil.success(userService.getFollowedList(userId, index, size));
    }
}
