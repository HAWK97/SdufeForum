package com.hawk.util;

import com.hawk.data.dto.ResponseMessage;
import com.hawk.data.constant.ResultEnum;

/**
 * 对 ResponseMessage 进行处理的工具类
 *
 * @author wangshuguang
 * @since 2018-03-17
 */
public class ResultUtil {

    /**
     * 操作成功返回具体消息
     *
     * @param object
     * @return
     */
    public static ResponseMessage success(Object object) {
        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setCode(ResultEnum.SUCCESS.getCode());
        responseMessage.setMsg(ResultEnum.SUCCESS.getMsg());
        responseMessage.setData(object);
        return responseMessage;
    }

    /**
     * 操作成功不返回消息
     *
     * @return
     */
    public static ResponseMessage success() {
        return success(null);
    }

    /**
     * 操作失败返回具体消息
     *
     * @param code
     * @param msg
     * @return
     */
    public static ResponseMessage error(int code, String msg) {
        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setCode(code);
        responseMessage.setMsg(msg);
        return responseMessage;
    }

    /**
     * 操作失败返回具体消息，对 error 方法的重载
     *
     * @param resultEnum
     * @return
     */
    public static ResponseMessage error(ResultEnum resultEnum) {
        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setCode(resultEnum.getCode());
        responseMessage.setMsg(resultEnum.getMsg());
        return responseMessage;
    }
}
