package com.mybatisgx.relation.select.simple_complex_id.manytomany.dao;

import com.mybatisgx.dao.SimpleDao; import com.mybatisgx.relation.select.simple_complex_id.manytomany.entity.Role;
import org.apache.ibatis.annotations.Mapper;
@Mapper public interface RoleDao extends SimpleDao<Role, Role, Long> {}
