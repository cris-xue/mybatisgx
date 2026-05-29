package com.mybatisgx.relation.select.join_simple_id.manytomany.dao;

import com.mybatisgx.dao.SimpleDao;
import com.mybatisgx.relation.select.join_simple_id.manytomany.entity.UserRoleJoin;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserRoleJoinDao extends SimpleDao<UserRoleJoin, UserRoleJoin, Long> {
}
