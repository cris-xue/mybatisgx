package com.lc.mybatisx.dao;

import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.util.List;

@Deprecated
public interface BaseDao<T, ID extends Serializable> {

    int insertSelective(T t);

    int deleteById(@Param("ID") ID id);

    int delete(T t);

    T findById(ID id);

    List<T> findCondition(T t);

    int updateByIdSelective(T t);

    int batchUpdate(List<T> list);

}
