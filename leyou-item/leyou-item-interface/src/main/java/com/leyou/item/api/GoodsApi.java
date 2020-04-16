package com.leyou.item.api;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @DESC:
 * @author: zhouben
 * @date: 2020/3/31 0031 10:59
 */
public interface GoodsApi {

    /**
     * 分页查询商品列表
     * @param key
     * @param saleable 是否上下架
     * @param page
     * @param rows
     * @return
     */
    @GetMapping("spu/page")
    public PageResult<SpuBo> querySpuBoByPage(@RequestParam(value = "key", required = false) String key,
                                              @RequestParam(value = "saleable", required = false) Boolean saleable,
                                              @RequestParam(value = "page", defaultValue = "1") Integer page,
                                              @RequestParam(value = "rows", defaultValue = "5") Integer rows);

    /**
     * 根据spuId查询商品详情
     * @param spuId
     * @return
     */
    @GetMapping("spu/detail/{spuId}")
    public SpuDetail querySpuDetailBySpuId(@PathVariable("spuId") Long spuId);

    /**
     * 根据spuId查询sku
     * @param spuId
     * @return
     */
    @GetMapping("sku/list")
    public List<Sku> querySkusBySpuId(@RequestParam("id") Long spuId);

    /**
     * 根据spu的id查询spu
     * @param id
     * @return
     */
    @GetMapping("spu/{id}")
    public Spu querySpuById(@PathVariable("id") Long id);

    /**
     * 根据sku的id查询sku
     * @param id
     * @return
     */
    @GetMapping("sku/{id}")
    public Sku querySkuById(@PathVariable("id") Long id);
}
