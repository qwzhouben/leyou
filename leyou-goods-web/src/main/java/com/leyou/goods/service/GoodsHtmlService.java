package com.leyou.goods.service;

import com.leyou.goods.util.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Map;

/**
 * @DESC: 页面静态化
 * @author: zhouben
 * @date: 2020/4/8 0008 20:04
 */
@Service
public class GoodsHtmlService {

    @Autowired
    GoodsService goodsService;

    @Autowired
    TemplateEngine templateEngine;

    /**
     * 创建html页面
     * @param spuId
     */
    public void createHtml(Long spuId) {
        PrintWriter writer = null;

        try {
            //获取页面数据
            Map<String, Object> map = goodsService.loadData(spuId);
            //创建thymeleaf上下文对象
            Context context = new Context();
            //把数据放入上下文对象
            context.setVariables(map);
            //创建输出流
            File file = new File("D:\\application\\nginx-1.15.0\\nginx-1.15.0\\html\\item\\" + spuId + ".html");
            writer = new PrintWriter(file);
            //执行页面静态化方法
            templateEngine.process("item", context, writer);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    /**
     * 新建线程处理页面静态化
     * @param spuId
     */
    public void asyncExecute(Long spuId) {
        ThreadUtils.execute(() -> createHtml(spuId));
    }

    /**
     * 删除静态页面
     * @param spuId
     */
    public void deleteHtml(Long spuId) {
        //创建输出流
        File file = new File("D:\\application\\nginx-1.15.0\\nginx-1.15.0\\html\\item\\" + spuId + ".html");
        file.deleteOnExit();
    }
}
