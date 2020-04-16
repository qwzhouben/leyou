package com.leyou.cart.client;

import com.leyou.item.api.GoodsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @DESC:
 * @AUTHOR: zhouben
 * @DATE: 2020/4/15 0015 20:05
 */
@FeignClient("item-service")
public interface GoodsClient extends GoodsApi {
}
