package com.mybatisgx.relation.select.onetomany.dao;

import com.lc.mybatisx.dao.SimpleDao;
import com.mybatisgx.entity.MultiId;
import com.mybatisgx.relation.select.onetomany.entity.Org;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrgDao extends SimpleDao<Org, MultiId> {
}
