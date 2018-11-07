package com.hawk.data.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "news")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel("个人动态对象")
public class News extends BaseEntity{

    /**
     * 动态内容
     */
    @ApiModelProperty("动态内容")
    @Lob
    @Basic(fetch=FetchType.LAZY)
    @Column(columnDefinition="text")
    private String content;

    /**
     * 发布该动态的用户 id
     */
    @ApiModelProperty(hidden=true)
    private Long userId;
}
