package com.mybatisgx.relation.error.onetomany.dao;

import com.mybatisgx.dao.SimpleDao;
import com.mybatisgx.relation.error.onetomany.entity.Org;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrgDao extends SimpleDao<Org, Org, Long> {
}
