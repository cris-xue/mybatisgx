package com.mybatisgx.relation.select.join_simple_id.onetomany.dao;

import com.mybatisgx.dao.SimpleDao;
import com.mybatisgx.relation.select.join_simple_id.onetomany.entity.Org;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrgDao extends SimpleDao<Org, Org, Long> {
}
