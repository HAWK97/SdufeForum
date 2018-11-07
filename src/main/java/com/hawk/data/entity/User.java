package com.hawk.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "user")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel("用户对象")
public class User extends BaseEntity {

    /**
     * 用户名根据 UUID 自动生成
     */
    @JsonIgnore
    private String userName;

    /**
     * 用户电话（作为用户名）
     */
    @ApiModelProperty("用户电话（作为用户名）")
    private String phoneNumber;

    /**
     * 昵称
     */
    @ApiModelProperty("昵称")
    private String nickName;

    /**
     * 密码
     */
    @ApiModelProperty("密码")
    private String password;

    /**
     * 头像 url，提供默认头像
     */
    @ApiModelProperty(hidden=true)
    private String avatarUrl = "http://p44lruo4o.bkt.clouddn.com/pencil-treeg-concept-logo-vector7.jpg";

    /**
     * 个人简介
     */
    @ApiModelProperty("个人简介")
    private String introduce = "暂无简介";

    /**
     * 用于密码加密的盐
     */
    @JsonIgnore
    private String salt;

    /**
     * 存放在 Cookie 中的用户登录凭证
     */
    @JsonIgnore
    private String ticket;
}
