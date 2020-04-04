package com.leyou.search.repository;

import com.leyou.search.pojo.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @DESC:
 * @author: zhouben
 * @date: 2020/4/2 0002 21:08
 */
public interface GoodsRepository extends ElasticsearchRepository<Goods, Long> {
}
