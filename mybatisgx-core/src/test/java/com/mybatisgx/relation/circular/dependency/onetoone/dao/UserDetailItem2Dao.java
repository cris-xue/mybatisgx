package com.mybatisgx.relation.circular.dependency.onetoone.dao;

import com.mybatisgx.dao.SimpleDao;
import com.mybatisgx.relation.circular.dependency.onetoone.entity.UserDetailItem2;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserDetailItem2Dao extends SimpleDao<UserDetailItem2, UserDetailItem2, Long> {
}
