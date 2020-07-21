package com.lc.mybatisx.dao;

import org.apache.ibatis.annotations.Param;

import java.io.Serializable;

/**
 * @author ：薛承城
 * @description：删除dao
 * @date ：2020/7/20 14:46
 */
public interface DeleteDao<ENTITY, ID extends Serializable> extends Dao {

    int deleteById(@Param("id") ID id);

}
