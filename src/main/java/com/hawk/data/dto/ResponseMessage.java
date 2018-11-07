package com.hawk.data.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * HttpRequest 请求返回的最外层对象，用一种统一的格式返回给前端
 * @author wangshuguang
 * @create 2018/03/17
 */
@Data
@ApiModel("响应信息对象")
public class ResponseMessage<T> {

    // 状态码
    @ApiModelProperty("状态码")
    private int code;

    // 信息
    @ApiModelProperty("信息")
    private String msg;

    // 数据
    @ApiModelProperty("数据")
    private T data;
}
