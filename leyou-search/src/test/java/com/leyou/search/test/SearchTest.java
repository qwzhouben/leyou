package com.leyou.search.test;

import com.leyou.LeyouSearchApplication;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.search.client.GoodsClient;
import com.leyou.search.pojo.Goods;
import com.leyou.search.repository.GoodsRepository;
import com.leyou.search.service.SearchService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @DESC:
 * @author: zhouben
 * @date: 2020/4/2 0002 21:12
 */
@SpringBootTest(classes = LeyouSearchApplication.class)
@RunWith(SpringRunner.class)
public class SearchTest {

    @Autowired
    GoodsRepository goodsRepository;

    @Autowired
    ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    GoodsClient goodsClient;

    @Autowired
    SearchService searchService;

    @Test
    public void importData() {
        //创建索引库
        elasticsearchTemplate.createIndex(Goods.class);
        //配置映射
        elasticsearchTemplate.putMapping(Goods.class);

        Integer page = 1;
        Integer rows = 100;
        do {
            //分批查询spuBo
            PageResult<SpuBo> pageResult = goodsClient.querySpuBoByPage(null, null, page, rows);
            //遍历spuBo集合转换成List<Goods>
            List<Goods> goods = pageResult.getItems().stream()
                    .map(spuBo -> {
                        try {
                            return searchService.buildGoods(spuBo);
                        } catch (IOException e) {
                            e.printStackTrace();
                            return null;
                        }
                    })
                    .collect(Collectors.toList());
            goodsRepository.saveAll(goods);
            //获取当前页的数据条数，如果是最后一页
            rows = pageResult.getItems().size();
            //每次循环页码加1
            page++;
        } while (rows == 100);

    }
}
