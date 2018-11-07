package com.hawk.data.entity;

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
@Table(name = "comment")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel("评论对象")
// todo 目前只能评论动态，不能回复动态下的评论
public class Comment extends BaseEntity {

    /**
     * 评论内容
     */
    @ApiModelProperty("评论内容")
    private String content;

    /**
     * 发表该评论的用户 id
     */
    @ApiModelProperty(hidden=true)
    private Long userId;

    /**
     * 被评论的动态 id
     */
    @ApiModelProperty("被评论的动态 id")
    private Long newsId;
}
