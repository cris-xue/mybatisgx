package com.mybatisgx.relation.select.join_complex_id.manytomany.dao;

import com.mybatisgx.dao.SimpleDao;
import com.mybatisgx.entity.MultiId;
import com.mybatisgx.relation.select.join_complex_id.manytomany.entity.Resource;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ResourceDao extends SimpleDao<Resource, Resource, MultiId> {
}
