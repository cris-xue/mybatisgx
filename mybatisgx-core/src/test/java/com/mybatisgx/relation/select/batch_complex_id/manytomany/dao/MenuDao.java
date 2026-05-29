package com.mybatisgx.relation.select.batch_complex_id.manytomany.dao;

import com.mybatisgx.dao.SimpleDao;
import com.mybatisgx.entity.MultiId;
import com.mybatisgx.relation.select.batch_complex_id.manytomany.entity.Menu;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MenuDao extends SimpleDao<Menu, Menu, MultiId> {
}