package com.leyou.item.api;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.pojo.Brand;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * @DESC: 品牌Api
 * @author: zhouben
 * @date: 2020/3/30 0030 9:28
 */
@RequestMapping("brand")
public interface BrandApi {

    /**
     * 根据查询条件分页并排序查询品牌信息
     * @param key 查询关键字
     * @param page 第几页
     * @param rows 每页展示多少条
     * @param sortBy 排序字段
     * @param desc 正序/降序
     * @return
     */
    @GetMapping("page")
    public PageResult<Brand> queryBrandsByPage(@RequestParam(value = "key", required = false) String key,
                                               @RequestParam(value = "page", defaultValue = "1") Integer page,
                                               @RequestParam(value = "rows", defaultValue = "5") Integer rows,
                                               @RequestParam(value = "sortBy", required = false) String sortBy,
                                               @RequestParam(value = "desc", required = false) Boolean desc) ;


    /**
     * 根据品牌id查询品牌
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public Brand queryById(@PathVariable("id") Long id);
}
