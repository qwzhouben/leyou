package com.leyou.item.pojo;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @DESC:
 * @author: zhouben
 * @date: 2020/3/31 0031 10:55
 */
@Table(name="tb_spu_detail")
public class SpuDetail {
    @Id
    private Long spuId;// 对应的SPU的id
    private String description;// 商品描述
    private String specialSpec;// 商品特殊规格的名称及可选值模板  {"4":["白色","金色","玫瑰金"],"12":["3GB"],"13":["16GB"]}
    private String genericSpec;// 商品的全局规格属性  {"1":"其它","2":"G9青春版（全网通版）","3":2016.0,"5":143,"6":"其它","7":"Android","8":"骁龙（Snapdragon)","9":"骁龙617（msm8952）","10":"八核","11":1.5,"14":5.2,"15":"1920*1080(FHD)","16":800.0,"17":1300.0,"18":3000.0}
    private String packingList;// 包装清单
    private String afterService;// 售后服务
    // 省略getter和setter

    public Long getSpuId() {
        return spuId;
    }

    public void setSpuId(Long spuId) {
        this.spuId = spuId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSpecialSpec() {
        return specialSpec;
    }

    public void setSpecialSpec(String specialSpec) {
        this.specialSpec = specialSpec;
    }

    public String getGenericSpec() {
        return genericSpec;
    }

    public void setGenericSpec(String genericSpec) {
        this.genericSpec = genericSpec;
    }

    public String getPackingList() {
        return packingList;
    }

    public void setPackingList(String packingList) {
        this.packingList = packingList;
    }

    public String getAfterService() {
        return afterService;
    }

    public void setAfterService(String afterService) {
        this.afterService = afterService;
    }
}
