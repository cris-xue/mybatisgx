package com.mybatisgx.relation.circular.dependency.onetoone.dao;

import com.mybatisgx.dao.SimpleDao;
import com.mybatisgx.relation.circular.dependency.onetoone.entity.UserDetail;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserDetailDao extends SimpleDao<UserDetail, UserDetail, Long> {
}
