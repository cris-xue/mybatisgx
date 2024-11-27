package com.lc.mybatisx.dao;

import com.lc.mybatisx.annotation.Dynamic;

import java.io.Serializable;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2020/7/19 13:59
 */
public interface UpdateDao<ENTITY, ID extends Serializable> extends Dao {

    int updateById(ENTITY entity);

    @Dynamic
    int updateByIdSelective(ENTITY entity);

}
