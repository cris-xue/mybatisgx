package com.mybatisgx.relation.select.simple_simple_id.manytomany.dao;

import com.mybatisgx.dao.SimpleDao;
import com.mybatisgx.relation.select.simple_simple_id.manytomany.entity.Menu;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MenuDao extends SimpleDao<Menu, Menu, Long> {
}
