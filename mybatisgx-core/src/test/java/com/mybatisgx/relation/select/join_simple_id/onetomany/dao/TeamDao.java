package com.mybatisgx.relation.select.join_simple_id.onetomany.dao;

import com.mybatisgx.dao.SimpleDao;
import com.mybatisgx.relation.select.join_simple_id.onetomany.entity.Team;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TeamDao extends SimpleDao<Team, Team, Long> {
}
