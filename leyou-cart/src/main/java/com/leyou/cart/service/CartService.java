package com.leyou.cart.service;

import com.leyou.auth.entity.UserInfo;
import com.leyou.cart.client.GoodsClient;
import com.leyou.cart.interceptor.LoginInterceptor;
import com.leyou.cart.pojo.Cart;
import com.leyou.common.utils.JsonUtils;
import com.leyou.item.pojo.Sku;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @DESC:
 * @author: zhouben
 * @date: 2020/4/15 0015 18:40
 */
@Service
public class CartService {

    private static final String KEY_PREFIX = "leyou:cart:uid:";

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    GoodsClient goodsClient;

    /**
     * 添加购物车
     * @param cart
     */
    public void addCart(Cart cart) {
        //获取登录用户
        UserInfo user = LoginInterceptor.getLoginUser();
        //Redis的key
        String key = KEY_PREFIX + user.getId();
        //获取hash操作对象
        BoundHashOperations<String, Object, Object> hashOps = redisTemplate.boundHashOps(key);
        //查询是否存在
        Long skuId = cart.getSkuId();
        Integer num = cart.getNum();
        Boolean boo = hashOps.hasKey(skuId.toString());
        if (boo) {
            //存在，获取购物车数据
            String json = hashOps.get(skuId.toString()).toString();
            cart = JsonUtils.parse(json, Cart.class);
            //修改购物车数量
            cart.setNum(cart.getNum() + num);
        } else {
            //不存在，新增购物车数据
            cart.setUserId(user.getId());
            //其他商品信息，需要查询商品服务
            Sku sku = goodsClient.querySkuById(skuId);
            cart.setImage(StringUtils.isBlank(sku.getImages()) ? "" : StringUtils.split(sku.getImages(), ",")[0]);
            cart.setPrice(sku.getPrice());
            cart.setTitle(sku.getTitle());
            cart.setOwnSpec(sku.getOwnSpec());
        }
        //将购物车数据写入redis
        hashOps.put(cart.getSkuId().toString(), JsonUtils.serialize(cart));
    }

    /**
     * 查询购物车
     * @return
     */
    public List<Cart> queryCartList() {
        //获取登录用户
        UserInfo user = LoginInterceptor.getLoginUser();
        //判断是否存在购物车
        String key = KEY_PREFIX + user.getId();

        if (!redisTemplate.hasKey(key)) {
            //不存在， 直接返回
            return null;
        }
        BoundHashOperations<String, Object, Object> hashOps = redisTemplate.boundHashOps(key);
        List<Object> carts = hashOps.values();
        //判断是否有数据
        if (CollectionUtils.isEmpty(carts)) {
            return null;
        }
        //查询购物车数据
        return carts.stream().map(o ->
            JsonUtils.parse(o.toString(), Cart.class)
        ).collect(Collectors.toList());
    }

    /**
     * 修改购物车
     * @param cart
     */
    public void updateCarts(Cart cart) {
        //获取登录信息
        UserInfo user = LoginInterceptor.getLoginUser();
        String key = KEY_PREFIX + user.getId();
        //获取hash操作对象
        BoundHashOperations<String, Object, Object> hashOps = redisTemplate.boundHashOps(key);
        //获取购物车信息
        String cartJson = hashOps.get(cart.getSkuId().toString()).toString();
        Cart cart1 = JsonUtils.parse(cartJson, Cart.class);
        //更新数量
        cart1.setNum(cart.getNum());
        //导入购物车
        hashOps.put(cart.getSkuId().toString(), JsonUtils.serialize(cart1));
    }

    /**
     * 删除购物车
     * @param skuId
     */
    public void deleteCarts(String skuId) {
        // 获取登录用户
        UserInfo user = LoginInterceptor.getLoginUser();
        String key = KEY_PREFIX + user.getId();
        BoundHashOperations<String, Object, Object> hashOps = this.redisTemplate.boundHashOps(key);
        hashOps.delete(skuId);
    }
}
