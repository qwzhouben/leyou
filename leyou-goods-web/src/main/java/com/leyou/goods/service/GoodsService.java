package com.leyou.goods.service;

import com.leyou.goods.client.BrandClient;
import com.leyou.goods.client.CategoryClient;
import com.leyou.goods.client.GoodsClient;
import com.leyou.goods.client.SpecificationClient;
import com.leyou.item.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @DESC:
 * @author: zhouben
 * @date: 2020/4/7 0007 21:22
 */
@Service
public class GoodsService {

    @Autowired
    BrandClient brandClient;
    @Autowired
    CategoryClient categoryClient;
    @Autowired
    GoodsClient goodsClient;
    @Autowired
    SpecificationClient specificationClient;

    /**
     * 组装数据模型
     * @param spuId
     * @return
     */
    public Map<String, Object> loadData(Long spuId) {
        Map<String, Object> map = new HashMap<>();
        //根据id查询spu对象
        Spu spu = goodsClient.querySpuById(spuId);
        //查询spuDetail
        SpuDetail spuDetail = goodsClient.querySpuDetailBySpuId(spuId);
        //查询sku集合
        List<Sku> skus = goodsClient.querySkusBySpuId(spuId);
        //查询分类
        List<Long> cids = Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3());
        List<String> names = categoryClient.queryNamesByIds(cids);

        List<Map<String, Object>> categories = new ArrayList<>();
        for (int i = 0; i < cids.size(); i++) {
            Map<String, Object> cateMap = new HashMap<>();
            cateMap.put("id", cids.get(i));
            cateMap.put("name", names.get(i));
            categories.add(cateMap);
        }
        //查询品牌
        Brand brand = brandClient.queryById(spu.getBrandId());
        //查询规格参数组
        List<SpecGroup> groups = specificationClient.queryGroupsWithParam(spu.getCid3());
        //查询特殊的规格参数
        List<SpecParam> params = specificationClient.queryParamsByGid(null, spu.getCid3(), false, null);
        Map<Long, String> paramMap = new HashMap<>();
        params.forEach(param -> paramMap.put(param.getId(), param.getName()));

        // 封装spu
        map.put("spu", spu);
        // 封装spuDetail
        map.put("spuDetail", spuDetail);
        // 封装sku集合
        map.put("skus", skus);
        // 分类
        map.put("categories", categories);
        // 品牌
        map.put("brand", brand);
        // 规格参数组
        map.put("groups", groups);
        // 查询特殊规格参数
        map.put("paramMap", paramMap);
        return map;
    }
}
