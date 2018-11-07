package com.hawk.data.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "code")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Code extends BaseEntity {

    /**
     * 用户电话
     */
    private String phoneNumber;

    /**
     * 随机生成的验证码
     */
    private String randomCode;
}
