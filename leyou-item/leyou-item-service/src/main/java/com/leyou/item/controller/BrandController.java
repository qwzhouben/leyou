package com.leyou.item.controller;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.pojo.Brand;
import com.leyou.item.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @DESC: 品牌控制器
 * @author: zhouben
 * @date: 2020/3/30 0030 9:28
 */
@Controller
@RequestMapping("brand")
public class BrandController {

    @Autowired
    BrandService brandService;

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
    public ResponseEntity<PageResult<Brand>> queryBrandsByPage( @RequestParam(value = "key", required = false)String key,
                                                                @RequestParam(value = "page", defaultValue = "1")Integer page,
                                                                @RequestParam(value = "rows", defaultValue = "5")Integer rows,
                                                                @RequestParam(value = "sortBy", required = false)String sortBy,
                                                                @RequestParam(value = "desc", required = false)Boolean desc) {
        PageResult<Brand> result = brandService.queryBrandsByPage(key, page, rows, sortBy, desc);
        if (CollectionUtils.isEmpty(result.getItems())) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }

    /**
     * 新增品牌
     * @param brand 品牌
     * @param cids  商品分类id集合
     * @return
     */
    @PostMapping
    public ResponseEntity<Void> saveBrand(Brand brand, @RequestParam("cids") List<Long> cids) {
        brandService.saveBrand(brand, cids);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
