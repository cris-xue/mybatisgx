package com.mybatisgx.simple.dao;

import com.lc.mybatisx.dao.SimpleDao;
import com.mybatisgx.simple.entity.UserDetail;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserDetailDao extends SimpleDao<UserDetail, Long> {
}
