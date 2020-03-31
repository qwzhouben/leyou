package com.leyou.item.bo;

import com.leyou.item.pojo.Spu;

/**
 * @DESC:
 * @author: zhouben
 * @date: 2020/3/31 0031 11:01
 */
public class SpuBo extends Spu {

    //商品分类
    private String cname;

    //品牌
    private String bname;

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public String getBname() {
        return bname;
    }

    public void setBname(String bname) {
        this.bname = bname;
    }
}
