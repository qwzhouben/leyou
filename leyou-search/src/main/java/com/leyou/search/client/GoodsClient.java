package com.leyou.search.client;

import com.leyou.item.api.GoodsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @DESC:
 * @AUTHOR: zhouben
 * @DATE: 2020/4/1 0001 21:50
 */
@FeignClient(value = "item-service")
public interface GoodsClient extends GoodsApi {
}
