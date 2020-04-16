package com.leyou.cart.interceptor;

import com.leyou.auth.entity.UserInfo;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.cart.config.JwtProperties;
import com.leyou.common.utils.CookieUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @DESC: 登陆拦截器
 * @author: zhouben
 * @date: 2020/4/15 0015 18:06
 */
@Component
public class LoginInterceptor extends HandlerInterceptorAdapter {


    private  JwtProperties jwtProperties;

    public LoginInterceptor(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    //定义一个线程域， 存放登录用户, 县城内共享，因此请求到达controller后可以获得
    private static final ThreadLocal<UserInfo> t = new ThreadLocal<>()
;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //查询token
        String token = CookieUtils.getCookieValue(request, "LY_TOKEN");
        if (StringUtils.isBlank(token)) {
            //未登录，返回401
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }
        //有token， 查询用户信息
        try {
            UserInfo user = JwtUtils.getInfoFromToken(token, jwtProperties.getPublicKey());
            //放入线程域
            t.set(user);
            return true;
        } catch (Exception e) {
            //跑出异常，证明未登录，返回401
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        t.remove();
    }

    public static UserInfo getLoginUser() {
        return t.get();
    }
}
