package com.mybatisgx.dao;

import com.mybatisgx.annotation.BatchData;
import com.mybatisgx.annotation.BatchOperation;
import com.mybatisgx.annotation.BatchSize;
import com.mybatisgx.annotation.Dynamic;
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

    @BatchOperation
    int insertBatch(@BatchData List<ENTITY> entityList, @BatchSize int batchSize);

    @Dynamic
    int insertSelective(ENTITY entity);

    int deleteById(@Param("id") ID id);

    @BatchOperation
    int deleteBatchById(@BatchData List<ID> ids, @BatchSize int batchSize);

    int updateById(ENTITY entity);

    @BatchOperation
    int updateBatchById(@BatchData List<ENTITY> entityList, @BatchSize int batchSize);

    @Dynamic
    int updateByIdSelective(ENTITY entity);

    ENTITY findById(@Param("id") ID id);
}
