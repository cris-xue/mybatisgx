package com.mybatisgx.relation.select.batch_simple_id.onetoone.dao;

import com.mybatisgx.dao.SimpleDao;
import com.mybatisgx.relation.select.batch_simple_id.onetoone.entity.UserDetail;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserDetailDao extends SimpleDao<UserDetail, UserDetail, Long> {
}
