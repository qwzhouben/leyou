package com.leyou.cart.controller;

import com.leyou.cart.pojo.Cart;
import com.leyou.cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @DESC: 购物车控制类
 * @author: zhouben
 * @date: 2020/4/15 0015 18:38
 */
@Controller
public class CartController {

    @Autowired
    CartService cartService;

    /**
     * 添加购物车
     * @param cart
     * @return
     */
    @PostMapping
    public ResponseEntity<Void> addCart(@RequestBody Cart cart) {
        cartService.addCart(cart);
        return ResponseEntity.ok().build();
    }

    /**
     * 查询购物车列表
     * @return
     */
    @GetMapping
    public ResponseEntity<List<Cart>> queryCartList() {
        List<Cart> carts = cartService.queryCartList();
        if (CollectionUtils.isEmpty(carts)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(carts);
    }

    /**
     * 修改购物车
     * @param cart
     * @return
     */
    @PutMapping
    public ResponseEntity<Void> updateNum(@RequestBody Cart cart) {
        cartService.updateCarts(cart);
        return ResponseEntity.noContent().build();
    }

    /**
     * 删除购物车
     * @param skuId
     * @return
     */
    @DeleteMapping("{skuId}")
    public ResponseEntity<Void> deleteCarts(@PathVariable("skuId") String skuId) {
        cartService.deleteCarts(skuId);
        return ResponseEntity.ok().build();
    }
}
