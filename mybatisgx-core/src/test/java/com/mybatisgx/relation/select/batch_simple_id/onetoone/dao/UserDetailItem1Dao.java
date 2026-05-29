package com.mybatisgx.relation.select.batch_simple_id.onetoone.dao;

import com.mybatisgx.dao.SimpleDao;
import com.mybatisgx.relation.select.batch_simple_id.onetoone.entity.UserDetailItem1;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserDetailItem1Dao extends SimpleDao<UserDetailItem1, UserDetailItem1, Long> {
}