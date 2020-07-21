package com.lc.mybatisx.dao;

import com.lc.mybatisx.annotation.MapperMethod;
import com.lc.mybatisx.annotation.MethodType;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2020/7/19 13:59
 */
public interface UpdateDao<ENTITY, ID extends Serializable> extends Dao {

    @MapperMethod(type = MethodType.UPDATE)
    int updateById(@Param("id") ID id);

}
