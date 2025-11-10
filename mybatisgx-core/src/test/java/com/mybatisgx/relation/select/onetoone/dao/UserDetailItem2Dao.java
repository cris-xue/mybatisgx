package com.mybatisgx.relation.select.onetoone.dao;

import com.mybatisgx.dao.SimpleDao;
import com.mybatisgx.entity.MultiId;
import com.mybatisgx.relation.select.onetoone.entity.UserDetailItem2;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserDetailItem2Dao extends SimpleDao<UserDetailItem2, MultiId> {
}
