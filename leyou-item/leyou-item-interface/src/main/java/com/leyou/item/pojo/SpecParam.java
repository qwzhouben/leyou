package com.leyou.item.pojo;

import javax.persistence.*;

/**
 * @DESC: 规格参数
 * @author: zhouben
 * @date: 2020/3/30 0030 20:23
 */
@Table(name = "tb_spec_param")
public class SpecParam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long cid; //分类id
    private Long groupId;  //规格组id
    private String name; //参数名
    @Column(name = "`numeric`")
    private Boolean numeric; //是否是数字类型参数
    private String unit; //数字类型参数的单位
    private Boolean generic; //是否是sku通用属性
    private Boolean searching; //是否用于搜索过滤
    private String segments; //数值类型参数

    // getter和setter ...

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCid() {
        return cid;
    }

    public void setCid(Long cid) {
        this.cid = cid;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getNumeric() {
        return numeric;
    }

    public void setNumeric(Boolean numeric) {
        this.numeric = numeric;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Boolean getGeneric() {
        return generic;
    }

    public void setGeneric(Boolean generic) {
        this.generic = generic;
    }

    public Boolean getSearching() {
        return searching;
    }

    public void setSearching(Boolean searching) {
        this.searching = searching;
    }

    public String getSegments() {
        return segments;
    }

    public void setSegments(String segments) {
        this.segments = segments;
    }
}
