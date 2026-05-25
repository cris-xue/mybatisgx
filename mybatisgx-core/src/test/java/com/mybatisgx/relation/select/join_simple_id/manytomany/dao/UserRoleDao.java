package com.mybatisgx.relation.select.join_simple_id.manytomany.dao;

import com.mybatisgx.dao.SimpleDao;
import com.mybatisgx.relation.select.join_simple_id.manytomany.entity.UserRole;
import org.apache.ibatis.annotations.Mapper;

@Mapper public interface UserRoleDao extends SimpleDao<UserRole, UserRole, Long> {}
