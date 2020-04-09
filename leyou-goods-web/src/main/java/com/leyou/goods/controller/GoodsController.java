package com.leyou.goods.controller;

import com.leyou.goods.service.GoodsHtmlService;
import com.leyou.goods.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * @DESC:
 * @author: zhouben
 * @date: 2020/4/7 0007 20:12
 */
@Controller
@RequestMapping("item")
public class GoodsController {

    @Autowired
    GoodsService goodsService;

    @Autowired
    GoodsHtmlService goodsHtmlService;

    /**
     * 跳转到详情页
     * @param model
     * @param id
     * @return
     */
    @GetMapping("{id}.html")
    public String toItemPage(Model model, @PathVariable("id") Long id) {
        // 加载所需的数据
        Map<String, Object> modelMap = goodsService.loadData(id);
        // 放入模型
        model.addAllAttributes(modelMap);
        //页面静态化
        goodsHtmlService.asyncExecute(id);
        return "item";
    }

}
