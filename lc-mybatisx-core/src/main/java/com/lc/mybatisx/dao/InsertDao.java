package com.lc.mybatisx.dao;

import com.lc.mybatisx.annotation.MapperMethod;
import com.lc.mybatisx.annotation.MethodType;

import java.io.Serializable;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2019/12/5 14:46
 */
public interface InsertDao<ENTITY, ID extends Serializable> {

    @MapperMethod(type = MethodType.INSERT)
    int insertSelective(ENTITY entity);

}
