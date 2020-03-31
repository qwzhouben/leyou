package com.leyou.item.controller;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

/**
 * @DESC:
 * @author: zhouben
 * @date: 2020/3/31 0031 10:59
 */
@Controller
@RequestMapping("")
public class GoodsController {

    @Autowired
    GoodsService goodsService;

    /**
     * 分页查询商品列表
     * @param key
     * @param saleable 是否上下架
     * @param page
     * @param rows
     * @return
     */
    @GetMapping("spu/page")
    public ResponseEntity<PageResult<SpuBo>> querySpuBoByPage(@RequestParam(value = "key", required = false) String key,
                                                              @RequestParam(value = "saleable", required = false) Boolean saleable,
                                                              @RequestParam(value = "page", defaultValue = "1") Integer page,
                                                              @RequestParam(value = "rows", defaultValue = "5") Integer rows) {
        PageResult<SpuBo> result = goodsService.querySpuBoByPage(key, saleable, page, rows);
        if (result == null || CollectionUtils.isEmpty(result.getItems())) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }

    /**
     * 保存商品
     * @param spuBo
     * @return
     */
    @PostMapping("goods")
    public ResponseEntity<Void> saveGoods(@RequestBody SpuBo spuBo) {
        if (spuBo == null) {
            Assert.notNull(spuBo, "商品信息不能为空");
        }
        goodsService.saveGoods(spuBo);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
