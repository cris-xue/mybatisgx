package com.lc.mybatisx.dao;

import java.io.Serializable;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2020/7/20 14:44
 */
public interface SimpleDao<ENTITY, ID extends Serializable> extends InsertDao, UpdateDao, QueryDao, DeleteDao {
}
