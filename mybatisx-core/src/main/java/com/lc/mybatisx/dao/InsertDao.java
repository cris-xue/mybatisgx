package com.lc.mybatisx.dao;

import com.lc.mybatisx.annotation.Dynamic;

import java.io.Serializable;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2019/12/5 14:46
 */
public interface InsertDao<ENTITY, ID extends Serializable> extends Dao {

    // @MapperMethod(type = MethodType.INSERT)
    int insert(ENTITY entity);

    // @MapperMethod(type = MethodType.INSERT, dynamic = true)
    @Dynamic
    int insertSelective(ENTITY entity);

}
