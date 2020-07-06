package com.lc.mybatisx.dao;

import com.lc.mybatisx.annotation.MapperMethod;
import com.lc.mybatisx.annotation.MethodType;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.util.List;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2019/12/5 14:46
 */
public interface QueryDao<ENTITY, ID extends Serializable> {

    @MapperMethod(type = MethodType.QUERY)
    ENTITY findById(@Param("id") ID id);

    @MapperMethod(type = MethodType.QUERY)
    List<ENTITY> findAll();

}
