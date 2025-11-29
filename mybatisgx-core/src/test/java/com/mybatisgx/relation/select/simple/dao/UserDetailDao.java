package com.mybatisgx.relation.select.simple.dao;

import com.mybatisgx.dao.SimpleDao;
import com.mybatisgx.entity.MultiId;
import com.mybatisgx.relation.select.simple.entity.UserDetail;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserDetailDao extends SimpleDao<UserDetail, MultiId> {
}
