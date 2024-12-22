package com.lc.mybatisx.dao;

import com.lc.mybatisx.annotation.Dynamic;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.util.List;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2020/7/20 14:44
 */
public interface SimpleDao<ENTITY, ID extends Serializable> extends Dao {

    int insert(ENTITY entity);

    @Dynamic
    int insertSelective(ENTITY entity);

    int deleteById(@Param("id") ID id);

    @Dynamic
    int deleteSelective(ENTITY entity);

    int updateById(ENTITY entity);

    @Dynamic
    int updateByIdSelective(ENTITY entity);

    ENTITY findById(@Param("id") ID id);

    @Dynamic
    ENTITY findOne(ENTITY entity);

    @Dynamic
    List<ENTITY> findList(ENTITY entity);

}
