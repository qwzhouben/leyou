package com.leyou.search.listener;

import com.leyou.search.service.SearchService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @DESC: 商品mq监听器
 * @author: zhouben
 * @date: 2020/4/9 0009 13:59
 */
@Component
public class GoodsListener {

    @Autowired
    SearchService searchService;

    /**
     * 监听商品新增和修改
     * @param spuId
     * @throws Exception
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "leyou.create.index.queue", durable = "true"),
            exchange = @Exchange(
                    value = "leyou.item.exchange",
                    ignoreDeclarationExceptions = "true",
                    type = ExchangeTypes.TOPIC
            ),
            key = {"item.insert", "item.update"}))
    public void listenerCreate(Long spuId) throws Exception {
        if (spuId == null) {
            return;
        }
        //创建或更新索引
        searchService.createIndex(spuId);
    }

    /**
     * 监听商品删除
     * @param spuId
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "leyou.create.index.queue", durable = "true"),
            exchange = @Exchange(
                    value = "leyou.item.exchange",
                    ignoreDeclarationExceptions = "true",
                    type = ExchangeTypes.TOPIC
            ),
            key = "item.delete"))
    public void listenerDelete(Long spuId) {
        if (spuId == null) {
            return;
        }
        //删除索引
        searchService.deleteIndex(spuId);
    }
}
