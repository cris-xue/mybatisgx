package com.mybatisgx.dao;

import com.mybatisgx.annotation.Dynamic;
import com.mybatisgx.executor.page.Page;
import com.mybatisgx.executor.page.Pageable;

import java.util.List;

/**
 * 查询dao接口
 * @author 薛承城
 * @date 2025/12/21 14:19
 */
public interface SelectDao<ENTITY, QUERY_ENTITY, ID> extends Dao {

    @Dynamic
    ENTITY findOne(QUERY_ENTITY entity);

    @Dynamic
    List<ENTITY> findList(QUERY_ENTITY entity);

    @Dynamic
    Page<ENTITY> findPage(QUERY_ENTITY entity, Pageable pageable);
}
