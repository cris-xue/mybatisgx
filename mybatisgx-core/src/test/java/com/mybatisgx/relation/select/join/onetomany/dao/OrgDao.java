package com.mybatisgx.relation.select.join.onetomany.dao;

import com.mybatisgx.dao.SimpleDao;
import com.mybatisgx.relation.select.join.onetomany.entity.Org;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrgDao extends SimpleDao<Org, Long> {
}
