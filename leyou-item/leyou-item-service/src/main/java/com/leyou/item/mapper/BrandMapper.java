package com.leyou.item.mapper;

import com.leyou.item.pojo.Brand;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @DESC:
 * @author: zhouben
 * @date: 2020/3/30 0030 9:24
 */
public interface BrandMapper extends Mapper<Brand> {


    /**
     * 新增商品分类和品牌中间表数据
     * @param cid 商品分类id
     * @param bid 品牌id
     * @return
     */
    @Insert("INSERT INTO tb_category_brand(category_id, brand_id) VALUES (#{cid},#{bid})")
    void insertBrandAndCategory(@Param("bid") Long bid, @Param("cid") Long cid);
}
