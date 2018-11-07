package com.hawk.exception;

import com.hawk.data.dto.ResponseMessage;
import com.hawk.data.constant.ResultEnum;
import com.hawk.util.ResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 异常处理类
 *
 * @author wangshuguang
 * @since 2018/03/13
 */
// 把 @ControllerAdvice 注解内部使用 @ExceptionHandler、@InitBinder、@ModelAttribute 注解的方法
// 应用到所有的 @RequestMapping 注解的方法
@ControllerAdvice
public class ExceptionHandle {

    private final static Logger logger = LoggerFactory.getLogger(Exception.class);

    // 定义当出现特定的异常时进行处理的方法
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResponseMessage handle(Exception e) {
        if (e instanceof MyException) {
            MyException myException = (MyException) e;
            return ResultUtil.error(myException.getCode(), myException.getMessage());
        } else {
            logger.error("[系统异常] {}", e);
            return ResultUtil.error(ResultEnum.SYSTEM_ERROR);
        }
    }
}
