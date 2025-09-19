package com.mybatisgx.manytomany.dao;

import com.lc.mybatisx.dao.SimpleDao;
import com.mybatisgx.manytomany.entity.UserRole;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserRoleDao extends SimpleDao<UserRole, Long> {
}
