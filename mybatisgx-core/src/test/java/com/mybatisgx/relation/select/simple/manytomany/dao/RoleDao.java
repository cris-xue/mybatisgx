package com.mybatisgx.relation.select.simple.manytomany.dao;

import com.mybatisgx.dao.SimpleDao;
import com.mybatisgx.relation.select.simple.manytomany.entity.Role;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RoleDao extends SimpleDao<Role, Role, Long> {
}
