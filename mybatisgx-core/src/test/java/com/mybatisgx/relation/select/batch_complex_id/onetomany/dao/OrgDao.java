package com.mybatisgx.relation.select.batch_complex_id.onetomany.dao;

import com.mybatisgx.dao.SimpleDao;
import com.mybatisgx.entity.MultiId;
import com.mybatisgx.relation.select.batch_complex_id.onetomany.entity.Org;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrgDao extends SimpleDao<Org, Org, MultiId> {
}