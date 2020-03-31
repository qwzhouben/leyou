package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.mapper.*;
import com.leyou.item.pojo.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @DESC: 商品service
 * @author: zhouben
 * @date: 2020/3/31 0031 10:58
 */
@Service
public class GoodsService {

    @Autowired
    SpuMapper spuMapper;

    @Autowired
    BrandMapper brandMapper;

    @Autowired
    CategoryService categoryService;

    @Autowired
    SpuDetailMapper spuDetailMapper;

    @Autowired
    SkuMapper skuMapper;

    @Autowired
    StockMapper stockMapper;

    /**
     * 分页查询商品列表
     * @param key
     * @param saleable 是否上下架
     * @param page
     * @param rows
     * @return
     */
    public PageResult<SpuBo> querySpuBoByPage(String key, Boolean saleable, Integer page, Integer rows) {

        //构建查询条件
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();

        //过滤条件
        if (StringUtils.isNotBlank(key)) {
            criteria.andLike("title", "%" + key + "%");
        }
        if (saleable != null) {
            criteria.andEqualTo("saleable", saleable);
        }

        //分页
        PageHelper.startPage(page, rows);
        //执行查询
        List<Spu> spus = spuMapper.selectByExample(example);
        PageInfo<Spu> pageInfo = new PageInfo<>(spus);

        //转换
        List<SpuBo> spuBos = spus.stream().map(spu -> {
            SpuBo spuBo = new SpuBo();
            BeanUtils.copyProperties(spu, spuBo);
            //设置分类名
            List<String> categoryNames = categoryService.queryNamesByIds(Arrays.asList(spuBo.getCid1(), spuBo.getCid2(), spuBo.getCid3()));
            spuBo.setCname(StringUtils.join(categoryNames, "/"));
            //设置品牌名
            Brand brand = brandMapper.selectByPrimaryKey(spuBo.getBrandId());
            spuBo.setBname(brand.getName());
            return spuBo;
        }).collect(Collectors.toList());

        return new PageResult<>(pageInfo.getTotal(), spuBos);
    }

    /**
     * 保存商品
     * @param spuBo
     */
    @Transactional
    public void saveGoods(SpuBo spuBo) {
        //新增spu
        spuBo.setSaleable(true);
        spuBo.setCreateTime(new Date());
        spuBo.setLastUpdateTime(spuBo.getCreateTime());
        spuBo.setValid(true);
        spuMapper.insertSelective(spuBo);

        //新增spu详情
        SpuDetail spuDetail = spuBo.getSpuDetail();
        spuDetail.setSpuId(spuBo.getId());
        spuDetailMapper.insertSelective(spuDetail);

        //新增到库存
        saveSkuAndStock(spuBo);
    }

    private void saveSkuAndStock(SpuBo spuBo) {
        spuBo.getSkus().forEach(sku -> {
            // 新增sku
            sku.setSpuId(spuBo.getId());
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(sku.getCreateTime());
            this.skuMapper.insertSelective(sku);

            // 新增库存
            Stock stock = new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());
            this.stockMapper.insertSelective(stock);
        });
    }

    /**
     * 根据spuId查询spu详情
     * @param spuId
     * @return
     */
    public SpuDetail querySpuDetailBySpuId(Long spuId) {
        return spuDetailMapper.selectByPrimaryKey(spuId);
    }

    /**
     * 根据spuId查询sku
     * @param spuId
     * @return
     */
    public List<Sku> querySkusBySpuId(Long spuId) {
        Sku record = new Sku(){{setSpuId(spuId);}};
        List<Sku> skus = skuMapper.select(record);
        return skus.stream()
                .map(sku -> {
                    Stock stock = stockMapper.selectByPrimaryKey(sku.getId());
                    sku.setStock(stock.getStock());
                    return sku;
                })
                .collect(Collectors.toList());
    }

    /**
     * 修改商品
     * @param spuBo
     */
    @Transactional
    public void updateGoods(SpuBo spuBo) {
        //查询以前的sku
        List<Sku> skus = this.querySkusBySpuId(spuBo.getId());
        // 如果以前存在，则删除
        if (!CollectionUtils.isEmpty(skus)) {
            // 删除以前库存
            List<Long> skuIds = skus.stream().map(sku -> sku.getId()).collect(Collectors.toList());
            Example example = new Example(Stock.class);
            example.createCriteria().andIn("skuId", skuIds);
            stockMapper.deleteByExample(example);

            // 删除以前的sku
            Sku record = new Sku(){{setSpuId(spuBo.getId());}};
            skuMapper.delete(record);
        }

        // 新增sku和库存
        saveSkuAndStock(spuBo);
        // 更新spu
        Spu record = new Spu(){{
            setLastUpdateTime(new Date());
        }};
        spuMapper.updateByPrimaryKeySelective(record);

        // 更新spu详情
        spuDetailMapper.updateByPrimaryKeySelective(spuBo.getSpuDetail());
    }
}
