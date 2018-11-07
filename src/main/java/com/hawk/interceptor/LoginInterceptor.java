package com.hawk.interceptor;

import com.hawk.annotation.LoginRequired;
import com.hawk.data.constant.ResultEnum;
import com.hawk.data.entity.User;
import com.hawk.exception.MyException;
import com.hawk.handle.UserContextHolder;
import com.hawk.service.UserService;
import com.hawk.util.DigestUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * 登录拦截器
 *
 * @author wangshugaung
 * @since 2018/03/13
 */
@Slf4j
public class LoginInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 设置请求和响应的编码
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        if (request.getRequestURI().contains("swagger")) {
            return true;
        }
        // 如果用户已登出，将 ThreadLocal 中存放的用户对象删除
        if (null == request.getSession().getAttribute("user")) {
            UserContextHolder.remove();
        }
        Cookie[] cookies = request.getCookies();
        if (null != cookies) {
            for (Cookie cookie : cookies) {
                if ("ticket".equals(cookie.getName())) {
                    String ticket = null;
                    if (!StringUtils.isEmpty(cookie.getValue())) {
                        ticket = DigestUtil.Decrypt(cookie.getValue());
                    }
                    User user = userService.findByTicket(ticket);
                    request.getSession().setAttribute("user", user);
                    log.info("ticket: " + ticket);
                    log.info("user: " + user);
                }
            }
        }
        // 参数中的 Object handler 是下一个拦截器
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        Method method = ((HandlerMethod) handler).getMethod();
        if (isAnnotationPresent(method, LoginRequired.class)) {
            if (null == request.getSession().getAttribute("user")) {
                UserContextHolder.remove();
                throw new MyException(ResultEnum.NEED_LOGIN);
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {}

    private boolean isAnnotationPresent(Method method, Class<? extends Annotation> annotationClass) {
        return method.getDeclaringClass().isAnnotationPresent(annotationClass) || method.isAnnotationPresent(annotationClass);
    }
}
