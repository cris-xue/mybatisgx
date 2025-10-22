package com.mybatisgx.relation.select.onetoone.dao;

import com.lc.mybatisx.dao.SimpleDao;
import com.mybatisgx.entity.MultiId;
import com.mybatisgx.relation.select.onetoone.entity.UserDetailItem1;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserDetailItem1Dao extends SimpleDao<UserDetailItem1, MultiId> {
}
