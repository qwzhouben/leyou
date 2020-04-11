package com.leyou.auth.service;

import com.leyou.auth.client.UserClient;
import com.leyou.auth.config.JwtProperties;
import com.leyou.auth.entity.UserInfo;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.user.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @DESC:
 * @author: zhouben
 * @date: 2020/4/11 0011 16:33
 */
@Service
public class AuthService {

    @Autowired
    UserClient userClient;

    @Autowired
    JwtProperties jwtProperties;

    /**
     * 登录授权
     * @param username
     * @param password
     * @return
     */
    public String authentication(String username, String password) {
        try {
            //调用微服务，执行查询
            User user = userClient.queryUser(username, password);

            //如果查询结果为null，直接返回null
            if (user == null) {
                return null;
            }
            //生成token
            return JwtUtils.generateToken(new UserInfo(user.getId(), user.getUsername()),
                    jwtProperties.getPrivateKey(), jwtProperties.getExpire());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
