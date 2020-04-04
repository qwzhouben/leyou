package com.leyou.item.api;

import com.leyou.item.pojo.SpecParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @DESC: 规格参数控制器
 * @author: zhouben
 * @date: 2020/3/30 0030 20:27
 */
@RequestMapping("spec")
public interface SpecificationApi {


    /**
     * 根据规格组id查询规格参数
     * @param gid
     * @return
     */
    @GetMapping("params")
    public List<SpecParam> queryParamsByGid(@RequestParam(value = "gid", required = false)Long gid,
                                                            @RequestParam(value = "cid", required = false)Long cid,
                                                            @RequestParam(value = "generic", required = false)Boolean generic,
                                                            @RequestParam(value = "searching", required = false)Boolean searching) ;

}
