package com.leyou.goods.client;

import com.leyou.item.api.CategoryApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @DESC:
 * @AUTHOR: zhouben
 * @DATE: 2020/4/1 0001 21:50
 */
@FeignClient(value = "item-service")
public interface CategoryClient extends CategoryApi {
}
