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
public interface SelectDao<ENTITY> extends Dao {

    @Dynamic
    ENTITY findOne(ENTITY entity);

    @Dynamic
    List<ENTITY> findList(ENTITY entity);

    @Dynamic
    Page<ENTITY> findPage(ENTITY entity, Pageable pageable);
}
