package com.mybatisgx.relation.select.simple_complex_id.manytomany.dao;

import com.mybatisgx.dao.SimpleDao;
import com.mybatisgx.relation.select.simple_complex_id.manytomany.entity.MenuResourceJoin;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MenuResourceJoinDao extends SimpleDao<MenuResourceJoin, MenuResourceJoin, Long> {
}
