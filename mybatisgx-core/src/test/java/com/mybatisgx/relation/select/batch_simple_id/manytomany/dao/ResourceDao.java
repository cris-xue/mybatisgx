package com.mybatisgx.relation.select.batch_simple_id.manytomany.dao;

import com.mybatisgx.dao.SimpleDao;
import com.mybatisgx.relation.select.batch_simple_id.manytomany.entity.Resource;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ResourceDao extends SimpleDao<Resource, Resource, Long> {
}
