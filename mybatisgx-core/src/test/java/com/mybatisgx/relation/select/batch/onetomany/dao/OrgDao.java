package com.mybatisgx.relation.select.batch.onetomany.dao;

import com.mybatisgx.dao.SimpleDao;
import com.mybatisgx.relation.select.batch.onetomany.entity.Org;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrgDao extends SimpleDao<Org, Org, Integer> {
}
