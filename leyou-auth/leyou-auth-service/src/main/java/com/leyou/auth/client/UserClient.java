package com.leyou.auth.client;

import com.leyou.user.api.UserApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @DESC:
 * @AUTHOR: zhouben
 * @DATE: 2020/4/11 0011 16:40
 */
@FeignClient("user-service")
public interface UserClient extends UserApi {
}
