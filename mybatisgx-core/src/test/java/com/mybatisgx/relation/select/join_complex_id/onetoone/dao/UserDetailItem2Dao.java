package com.mybatisgx.relation.select.join_complex_id.onetoone.dao;

import com.mybatisgx.dao.SimpleDao;
import com.mybatisgx.entity.MultiId;
import com.mybatisgx.relation.select.join_complex_id.onetoone.entity.UserDetailItem2;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserDetailItem2Dao extends SimpleDao<UserDetailItem2, UserDetailItem2, MultiId> {
}
