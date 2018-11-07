package com.hawk.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.Date;

@Data
@MappedSuperclass
public abstract class BaseEntity implements Serializable {

    /**
     * 自增 id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(hidden = true)
    private Long id;

    /**
     * 创建时间
     */
    @CreationTimestamp
    @ApiModelProperty(hidden=true)
    private Date createTime;

    /**
     * 修改时间
     */
    @JsonIgnore
    @UpdateTimestamp
    private Date updateTime;

    /**
     * 状态：-1 为删除，0 为正常，默认为 0
     */
    @JsonIgnore
    private Integer status = 0;
}
