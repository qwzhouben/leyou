package com.leyou.search.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.pojo.*;
import com.leyou.search.client.BrandClient;
import com.leyou.search.client.CategoryClient;
import com.leyou.search.client.GoodsClient;
import com.leyou.search.client.SpecificationClient;
import com.leyou.search.pojo.Goods;
import com.leyou.search.pojo.SearchRequest;
import com.leyou.search.pojo.SearchResult;
import com.leyou.search.repository.GoodsRepository;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @DESC:
 * @author: zhouben
 * @date: 2020/4/2 0002 21:14
 */
@Service
public class SearchService {

    @Autowired
    BrandClient brandClient;
    @Autowired
    CategoryClient categoryClient;
    @Autowired
    GoodsClient goodsClient;
    @Autowired
    SpecificationClient specificationClient;
    @Autowired
    GoodsRepository goodsRepository;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * 搜索
     * @param request
     * @return
     */
    public SearchResult search(SearchRequest request) {
        String key = request.getKey();
        //判断搜索条件
        if (StringUtils.isBlank(key)) {
            return null;
        }
        //构建查询条件
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();

        //1、对key进行全文检索查询
        //QueryBuilder basicQuery = QueryBuilders.matchQuery("all", key).operator(Operator.AND);
        BoolQueryBuilder boolQueryBuilder = buildBooleanQueryBuilder(request);
        queryBuilder.withQuery(boolQueryBuilder);
        //2、通过sourceFilter设置返回的结果字段，我们只需要id、skus、subTitle
        queryBuilder.withSourceFilter(new FetchSourceFilter(
                new String[]{"id","skus","subTitle"}, null
        ));
        //3、分页
        int page = request.getPage();
        int size = request.getSize();
        queryBuilder.withPageable(PageRequest.of(page - 1, size));

        //添加聚合
        String categoryAggName = "categories";
        String brandAggName = "brands";
        queryBuilder.addAggregation(AggregationBuilders.terms(categoryAggName).field("cid3"));
        queryBuilder.addAggregation(AggregationBuilders.terms(brandAggName).field("brandId"));

        //4、排序
        String sortBy = request.getSortBy();
        Boolean descending = request.getDescending();
        if (StringUtils.isNotBlank(sortBy)) {
            //如果不为空，则进行排序
            queryBuilder.withSort(SortBuilders.fieldSort(sortBy).order(descending ? SortOrder.DESC : SortOrder.ASC));
        }
        //5、查询
        AggregatedPage<Goods> pageInfo = (AggregatedPage<Goods>) goodsRepository.search(queryBuilder.build());

        //解析聚合结果集
        List<Map<String, Object>> categories = getCategoryAggResult(pageInfo.getAggregation(categoryAggName));
        List<Brand> brands = getBrandAggResult(pageInfo.getAggregation(brandAggName));

        //判断分类聚合的结果集大小， 等于1则聚合
        List<Map<String, Object>> specs = null;
        if (!CollectionUtils.isEmpty(categories) && categories.size() == 1) {
            specs = getParamAggResult((Long) categories.get(0).get("id"), boolQueryBuilder);
        }

        //封装结果返回
        return new SearchResult(pageInfo.getTotalElements(), pageInfo.getTotalPages(),
                pageInfo.getContent(), brands, categories, specs);
    }

    /**
     * 构建bool查询构建起
     * @param request
     * @return
     */
    private BoolQueryBuilder buildBooleanQueryBuilder(SearchRequest request) {
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        //添加基本查询条件
        boolQueryBuilder.must(QueryBuilders.matchQuery("all", request.getKey()).operator(Operator.AND));
        //添加过滤条件
        if (CollectionUtils.isEmpty(request.getFilter())) {
            return boolQueryBuilder;
        }
        request.getFilter().entrySet().forEach(entity -> {
            String key = entity.getKey();
            //如果过滤条件是品牌，过滤字段名：brandId
            if (StringUtils.equals("品牌", key)) {
                key = "brandId";
            } else if (StringUtils.equals("分类", key)) {
                key = "cid3";
            } else {
                //如果是规格参数，过滤字段名：specs.key.keyword
                key = "specs." + key + ".keyword";
            }
            boolQueryBuilder.filter(QueryBuilders.termQuery(key, entity.getValue()));
        });
        return boolQueryBuilder;
    }

    /**
     * 聚合出规格参数
     * @param cid
     * @param basicQuery
     * @return
     */
    private List<Map<String, Object>> getParamAggResult(Long cid, QueryBuilder basicQuery) {
        //初始化自定义查询构建器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //添加查询条件
        queryBuilder.withQuery(basicQuery);
        //查询要聚合的规格参数
        List<SpecParam> params = specificationClient.queryParamsByGid(null, cid, null, true);
        //添加聚合
        params.forEach(specParam ->
            queryBuilder.addAggregation(AggregationBuilders.terms(specParam.getName()).field("specs." + specParam.getName() + ".keyword"))
        );
        //只需要聚合出结果集，不需要查询结果集
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{}, null));
        //执行聚合查询
        AggregatedPage<Goods> goodsPage = (AggregatedPage<Goods>) goodsRepository.search(queryBuilder.build());

        //定义一个集合，收集聚合结果集
        List<Map<String, Object>> paramMapList = new ArrayList<>();
        //解析聚合查询的结果集
        Map<String, Aggregation> aggregationMap = goodsPage.getAggregations().asMap();
        aggregationMap.entrySet().forEach(entity -> {
            Map<String, Object> map = new HashMap<>();
            //放入规格参数名
            map.put("k", entity.getKey());
            //收集规格参数值
            List<Object> options = new ArrayList<>();
            //解析每个集合
            StringTerms terms = (StringTerms) entity.getValue();
            //遍历每个聚合中的桶，把桶中key放入收集规格参数的集合中
            terms.getBuckets().forEach(bucket -> options.add(bucket.getKeyAsString()));
            map.put("options", options);
            paramMapList.add(map);
        });
        return paramMapList;
    }

    /**
     * 解析品牌聚合结果集
     * @param aggregation
     * @return
     */
    private List<Brand> getBrandAggResult(Aggregation aggregation) {
        //处理聚合结果集
        LongTerms terms = (LongTerms) aggregation;

        return terms.getBuckets().stream()
                .map(bucket -> brandClient.queryById(bucket.getKeyAsNumber().longValue()))
                .collect(Collectors.toList());
    }

    /**
     * 解析分类聚合结果集
     * @param aggregation
     * @return
     */
    private List<Map<String, Object>> getCategoryAggResult(Aggregation aggregation) {
        //处理聚合结果集
        LongTerms terms = (LongTerms) aggregation;

        return terms.getBuckets().stream()
                .map(bucket -> {
                    Map<String, Object> map = new HashMap<>();
                    Long cid = bucket.getKeyAsNumber().longValue();
                    List<String> names = categoryClient.queryNamesByIds(Arrays.asList(cid));
                    map.put("id", cid);
                    map.put("name", names.get(0));
                    return map;
                })
                .collect(Collectors.toList());
    }

    /**
     * 构建搜索对象
     * @param spu
     * @return
     * @throws IOException
     */
    public Goods buildGoods(Spu spu) throws IOException {
        //创建goods对象
        Goods goods = new Goods(){{
            setId(spu.getId());
            setCid1(spu.getCid1());
            setCid2(spu.getCid2());
            setCid3(spu.getCid3());
            setBrandId(spu.getBrandId());
            setCreateTime(spu.getCreateTime());
            setSubTitle(spu.getSubTitle());
        }};
        //查询品牌
        Brand brand = brandClient.queryById(spu.getBrandId());

        //查询分类
        List<String> names = categoryClient.queryNamesByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));

        //查询spu下的所有sku
        List<Sku> skus = goodsClient.querySkusBySpuId(spu.getId());
        List<Long> prices = new ArrayList<>();
        List<Map<String, Object>> skuMapList = new ArrayList<>();
        //遍历sku，获取价格集合
        skus.forEach(sku -> {
            prices.add(sku.getPrice());
            Map<String, Object> skuMap = new HashMap<>();
            skuMap.put("id", sku.getId());
            skuMap.put("title", sku.getTitle());
            skuMap.put("price", sku.getPrice());
            skuMap.put("image", StringUtils.isBlank(sku.getImages()) ? "" : StringUtils.split(sku.getImages())[0]);
            skuMapList.add(skuMap);
        });

        //查询出所有的搜索规格参数
        List<SpecParam> params = specificationClient.queryParamsByGid(null, spu.getCid3(), null, Boolean.TRUE);
        //查询spuDetail，获取规格参数值
        SpuDetail spuDetail = goodsClient.querySpuDetailBySpuId(spu.getId());
        //获取通用的规格参数
        Map<Long, Object> genericMap = MAPPER.readValue(spuDetail.getGenericSpec(), new TypeReference<Map<Long, Object>>(){});
        //获取特殊的规格参数
        Map<Long, List<Object>> specialMap = MAPPER.readValue(spuDetail.getSpecialSpec(), new TypeReference<Map<Long, List<Object>>>(){});

        //定义map接收 {规格参数名:规格参数值}
        Map<String, Object> paramMap = new HashMap<>();
        params.forEach(param -> {
            //判断是否通用规格参数
            if (param.getGeneric()) {
                //获取通用规格参数值
                String value = genericMap.get(param.getId()).toString();
                //判断是否是数值类型
                if (param.getNumeric()) {
                    //如果是数值的话，判断数值落在哪个区间
                    value = chooseSegment(value, param);
                }
                //把参数和值放入结果中
                paramMap.put(param.getName(), value);
            } else {
                paramMap.put(param.getName(), specialMap.get(param.getId()));
            }
        });
        goods.setAll(spu.getTitle() + " " + brand.getName() + " " + StringUtils.join(names, " "));
        goods.setPrice(prices);
        goods.setSkus(MAPPER.writeValueAsString(skuMapList));
        goods.setSpecs(paramMap);
        return goods;
    }

    private String chooseSegment(String value, SpecParam p) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if(segs.length == 2){
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if(val >= begin && val < end){
                if(segs.length == 1){
                    result = segs[0] + p.getUnit() + "以上";
                }else if(begin == 0){
                    result = segs[1] + p.getUnit() + "以下";
                }else{
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }

}
