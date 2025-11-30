package com.mybatisgx.relation.select.join.manytomany.dao;

import com.mybatisgx.dao.SimpleDao;
import com.mybatisgx.relation.select.join.manytomany.entity.UserRole;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserRoleDao extends SimpleDao<UserRole, Long> {
}
