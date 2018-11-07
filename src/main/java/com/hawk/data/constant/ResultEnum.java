package com.hawk.data.constant;

/**
 * 用于描述响应结果的枚举
 *
 * @author wangshuguang
 * @since 2018/03/13
 */
public enum ResultEnum {

    SYSTEM_ERROR(-99, "运行时异常"),

    NEED_LOGIN(-1, "未登录"),

    REPEAT_REGISTER(-2, "该用户已注册"),

    USER_NOT_EXIST(-3, "不存在该用户"),

    PASSWORD_ERROR(-4, "密码错误"),

    EMPTY_USERNAME(-5, "用户名为空"),

    EMPTY_PASSWORD(-6, "密码为空"),

    EMPTY_NICKNAME(-7, "昵称为空"),

    SEND_SMS_ERROR(-8, "短信验证码发送失败"),

    CODE_ERROR(-9, "验证码错误"),

    CODE_TIME_OUT(-10, "验证码已失效"),

    IMAGE_FORMAT_ERROR(-11, "图片格式错误"),

    IMAGE_FAILED_UPLOAD(-12, "图片上传失败"),

    IMAGE_NUMBER_ERROR(-13, "动态上传图片不得超过9张"),

    DELETE_ERROR(-14, "当前用户无权限删除"),

    SUCCESS(0, "success");

    private Integer code;

    private String msg;

    ResultEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
