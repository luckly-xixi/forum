package com.example.forum.interceptor;

import com.example.forum.config.AppConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Value("${forum.login.url}")
    private String defaultURL;
    /**     登录拦截器
     *   前置处理(对请求的预处理)
     *   true:继续流程          false:流程中断
     */

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        if(session != null && session.getAttribute(AppConfig.USER_SESSION) != null) {
        return true;
        }
        //校验不通过，跳转到登录页
        //response.sendRedirect("/sign-in.html");//为降低耦合性，把目标页面放在配置文件中
        //校验URL是否正确
        if(!defaultURL.startsWith("/")) {
            defaultURL = "/" + defaultURL;
        }
        response.sendRedirect(defaultURL);
        //中断流程
        return false;
    }
}
