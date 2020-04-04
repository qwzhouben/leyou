package com.leyou.search.pojo;

/**
 * @DESC:
 * @author: zhouben
 * @date: 2020/4/4 0004 15:18
 */
public class SearchRequest {

    //搜索条件
    private String key;

    //当前页
    private Integer page;

    //每页大小
    private static final Integer DEFAULT_SIZE = 20;

    //默认页
    private static final Integer DEFAULT_PAGE = 1;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getPage() {
        if (page == null) {
            page = DEFAULT_PAGE;
        }
        return Math.max(DEFAULT_PAGE, page);
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return DEFAULT_SIZE;
    }
}
