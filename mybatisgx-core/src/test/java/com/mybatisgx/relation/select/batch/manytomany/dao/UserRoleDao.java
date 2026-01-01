package com.mybatisgx.relation.select.batch.manytomany.dao;

import com.mybatisgx.dao.SimpleDao;
import com.mybatisgx.relation.select.batch.manytomany.entity.UserRole;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserRoleDao extends SimpleDao<UserRole, UserRole, Long> {
}
