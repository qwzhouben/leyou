package com.leyou.item.service;

import com.leyou.item.mapper.SpecGroupMapper;
import com.leyou.item.mapper.SpecParamMapper;
import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @DESC:
 * @author: zhouben
 * @date: 2020/3/30 0030 20:26
 */
@Service
public class SpecGroupService {

    @Autowired
    SpecGroupMapper specGroupMapper;
    @Autowired
    SpecParamMapper specParamMapper;

    /**
     * 根据分类查询规格组
     * @param cid
     * @return
     */
    public List<SpecGroup> queryGroupsByCid(Long cid) {
        SpecGroup record = new SpecGroup(){{setCid(cid);}};
        return specGroupMapper.select(record);
    }

    /**
     * 根据规格组id查询规格参数
     * @param gid
     * @return
     */
    public List<SpecParam> queryParamsByGid(Long gid, Long cid, Boolean generic, Boolean searching) {
        SpecParam record = new SpecParam(){{
            setGroupId(gid);
            setCid(cid);
            setGeneric(generic);
            setSearching(searching);
        }};
        return specParamMapper.select(record);
    }
}
