package com.leyou.user.controller;

import com.leyou.user.pojo.User;
import com.leyou.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @DESC:
 * @author: zhouben
 * @date: 2020/4/9 0009 16:24
 */
@Controller
public class UserController {

    @Autowired
    UserService userService;

    /**
     * 注册
     * @param user
     * @param code
     * @return
     */
    @PostMapping("register")
    public ResponseEntity<Void> register(@Valid User user, @RequestParam("code") String code) {
        Boolean boo = userService.register(user, code);
        if (boo == null || !boo) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 根据用户名密码查询用户
     * @param username
     * @param password
     * @return
     */
    @GetMapping("query")
    public ResponseEntity<User> queryUser(@RequestParam("username") String username,
                                          @RequestParam("password") String password) {
        User user = userService.queryUser(username, password);
        if (user == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(user);
    }

    /**
     * 校验数据是否可用
     * @param data 要校验的数据
     * @param type 1：用户名，2：手机
     * @return
     */
    @GetMapping("check/{data}/{type}")
    public ResponseEntity<Boolean> checkUserData(@PathVariable("data") String data,
                                                 @PathVariable("type") Integer type) {
        Boolean boo = userService.checkUserData(data, type);
        if (boo == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(boo);
    }

    /**
     * 发送手机验证码
     * @param phone
     * @return
     */
    @PostMapping("code")
    @CrossOrigin
    public ResponseEntity<Void> sendVerifyCode(String phone) {
        Boolean boo = userService.sendVerifyCode(phone);
        if (boo == null || !boo) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
