package com.hawk.listener;

import com.hawk.data.entity.User;
import com.hawk.handle.UserContextHolder;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;

/**
 * 当Session属性变动时，执行ThreadLocal相应的set或remove操作
 *
 * @author wangshuguang
 * @since 2018/03/13
 */
@WebListener
public class SessionAttributeListener implements HttpSessionAttributeListener {

    @Override
    public void attributeAdded(HttpSessionBindingEvent event) {
        if ("user".equals(event.getName())) {
            UserContextHolder.set((User)event.getValue());
        }
    }

    @Override
    public void attributeRemoved(HttpSessionBindingEvent event) {
        if ("user".equals(event.getName())) {
            UserContextHolder.remove();
        }
    }

    @Override
    public void attributeReplaced(HttpSessionBindingEvent event) {
        if ("user".equals(event.getName())) {
            UserContextHolder.set((User)event.getValue());
        }
    }
}
