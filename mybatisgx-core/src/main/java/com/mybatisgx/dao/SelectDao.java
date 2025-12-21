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
public interface SelectDao<QUERY_ENTITY> extends Dao {

    @Dynamic
    QUERY_ENTITY findOne(QUERY_ENTITY entity);

    @Dynamic
    List<QUERY_ENTITY> findList(QUERY_ENTITY entity);

    @Dynamic
    Page<QUERY_ENTITY> findPage(QUERY_ENTITY entity, Pageable pageable);
}
