package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.mapper.BrandMapper;
import com.leyou.item.pojo.Brand;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @DESC: 商品品牌Service
 * @author: zhouben
 * @date: 2020/3/30 0030 9:33
 */
@Service
public class BrandService {

    @Autowired
    BrandMapper brandMapper;

    /**
     * 根据查询条件分页并排序查询品牌信息
     * @param key 查询关键字
     * @param page 第几页
     * @param rows 每页展示多少条
     * @param sortBy 排序字段
     * @param desc 正序/降序
     * @return
     */
    public PageResult<Brand> queryBrandsByPage(String key, Integer page, Integer rows, String sortBy, Boolean desc) {
        //初始化查询对象
        Example example = new Example(Brand.class);
        Example.Criteria criteria = example.createCriteria();

        //根据name模糊查询，或者根据首字母查询
        if (StringUtils.isNotBlank(key)) {
            criteria.andLike("name", "%" + key + "%").orEqualTo("letter", key);
        }

        //添加分页条件
        PageHelper.startPage(page, rows);

        //添加排序条件
        if (StringUtils.isNotBlank(sortBy)) {
            example.setOrderByClause(sortBy + " " + (desc ? "desc" : "asc"));
        }

        List<Brand> brands = brandMapper.selectByExample(example);

        //包装成pageInfo
        PageInfo<Brand> pageInfo = new PageInfo<>(brands);

        // 包装成分页结果集返回
        return new PageResult<>(pageInfo.getTotal(), pageInfo.getList());
    }

    /**
     * 新增品牌
     * @param brand
     * @param cids
     */
    @Transactional
    public void saveBrand(Brand brand, List<Long> cids) {
        //新增品牌
        brandMapper.insertSelective(brand);
        //新增品牌和分类中间表
        cids.forEach(cid -> this.brandMapper.insertBrandAndCategory(cid, brand.getId()));

    }

    /**
     * 根据分类查询品牌
     * @param cid
     * @return
     */
    public List<Brand> queryBrandByCid(Long cid) {
        return brandMapper.queryBrandByCid(cid);
    }

    /**
     * 根据品牌id查询品牌
     * @param id
     * @return
     */
    public Brand queryById(Long id) {
        return brandMapper.selectByPrimaryKey(id);
    }
}
