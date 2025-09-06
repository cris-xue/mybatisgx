package com.mybatisgx.dao;

import com.lc.mybatisx.dao.SimpleDao;
import com.mybatisgx.entity.Role;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RoleDao extends SimpleDao<Role, Long> {
}
