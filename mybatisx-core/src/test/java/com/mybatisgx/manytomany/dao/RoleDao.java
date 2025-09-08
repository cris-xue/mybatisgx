package com.mybatisgx.manytomany.dao;

import com.lc.mybatisx.dao.SimpleDao;
import com.mybatisgx.manytomany.entity.Role;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RoleDao extends SimpleDao<Role, Long> {
}
