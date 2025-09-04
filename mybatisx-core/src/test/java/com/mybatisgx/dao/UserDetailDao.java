package com.mybatisgx.dao;

import com.lc.mybatisx.dao.SimpleDao;
import com.mybatisgx.entity.UserDetail;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserDetailDao extends SimpleDao<UserDetail, Long> {
}
