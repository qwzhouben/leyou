package com.leyou.user.service;

import com.leyou.common.utils.CodecUtils;
import com.leyou.common.utils.NumberUtils;
import com.leyou.user.mapper.UserMapper;
import com.leyou.user.pojo.User;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @DESC:
 * @author: zhouben
 * @date: 2020/4/9 0009 16:24
 */
@Service
public class UserService {

    private static Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    UserMapper userMapper;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    AmqpTemplate amqpTemplate;

    private static final String KEY_PREFIX = "user:code:phone:";

    /**
     * 校验数据是否可用
     * @param data
     * @param type
     * @return
     */
    public Boolean checkUserData(String data, Integer type) {
        User record = new User();
        switch (type) {
            case 1:
                record.setUsername(data);
                break;
            case 2:
                record.setPhone(data);
                break;
            default:
                return null;
        }

        return userMapper.selectCount(record) == 0;
    }

    /**
     * 发送短信验证码
     * @param phone
     * @return
     */
    public Boolean sendVerifyCode(String phone) {
        //生成验证码
        String code = NumberUtils.generateCode(6);
        try {
            //发送短信
            Map<String, String> msg = new HashMap<>();
            msg.put("phone", phone);
            msg.put("code", code);
            //amqpTemplate.convertAndSend("leyou.sms.exchange", "sms.verify.code", msg);
            //将code存入redis
            stringRedisTemplate.opsForValue().set(KEY_PREFIX + phone, code, 5, TimeUnit.MINUTES);
        } catch (Exception e) {
            logger.error("发送短信失败。phone：{}，code：{}", phone, code);
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    /**
     * 注册
     * @param user
     * @param code
     * @return
     */
    public Boolean register(User user, String code) {
        //校验验证码
        String cacheCode = stringRedisTemplate.opsForValue().get(KEY_PREFIX + user.getPhone());
        if (!StringUtils.equals(code, cacheCode)) {
            return Boolean.FALSE;
        }
        //生成盐
        String salt = CodecUtils.generateSalt();
        user.setSalt(salt);
        //对密码加密
        user.setPassword(CodecUtils.md5Hex(user.getPassword(), salt));

        //强制设置不能指定的参数为null
        user.setId(null);
        user.setCreated(new Date());

        //添加到数据库
        boolean b = userMapper.insertSelective(user) == 1;

        if (b) {
            //注册成功，删除redis记录
            stringRedisTemplate.delete(KEY_PREFIX + user.getPhone());
        }
        return b;
    }

    /**
     * 根据用户名密码查询用户
     * @param username
     * @param password
     * @return
     */
    public User queryUser(String username, String password) {
        User record = new User(){{
            setUsername(username);
        }};
        User user = userMapper.selectOne(record);
        //校验用户名
        if (user == null) {
            return null;
        }
        //校验密码
        if (!StringUtils.equals(CodecUtils.md5Hex(password, user.getSalt()), user.getPassword())) {
            return null;
        }
        return user;
    }
}
