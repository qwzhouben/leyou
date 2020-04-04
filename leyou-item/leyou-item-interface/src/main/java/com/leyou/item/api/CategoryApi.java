package com.leyou.item.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @DESC: 分类Api
 * @author: zhouben
 * @date: 2020/3/29 0029 16:28
 */
@RequestMapping("category")
public interface CategoryApi {

    /**
     * 根据id查询名称
     * @param ids
     * @return
     */
    @GetMapping
    public List<String> queryNamesByIds(@RequestParam("ids") List<Long> ids) ;
}
