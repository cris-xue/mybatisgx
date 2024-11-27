package com.lc.mybatisx.dao;

import com.lc.mybatisx.annotation.Dynamic;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.util.List;

/**
 * @author ：薛承城
 * @description：查询dao
 * @date ：2019/12/5 14:46
 */
public interface QueryDao<ENTITY, ID extends Serializable> extends Dao {

    ENTITY findById(@Param("id") ID id);

    @Dynamic
    List<ENTITY> findList(ENTITY entity);

}
