package com.leyou.user.api;

import com.leyou.user.pojo.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @DESC:
 * @AUTHOR: zhouben
 * @DATE: 2020/4/11 0011 16:39
 */
public interface UserApi {

    /**
     * 根据用户名密码查询用户
     * @param username
     * @param password
     * @return
     */
    @GetMapping("query")
    public User queryUser(@RequestParam("username") String username,
                                          @RequestParam("password") String password);
}
