package com.mybatisgx.onetoone.dao;

import com.lc.mybatisx.dao.SimpleDao;
import com.mybatisgx.entity.MultiId;
import com.mybatisgx.onetoone.entity.UserDetail;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserDetailDao extends SimpleDao<UserDetail, MultiId> {
}
