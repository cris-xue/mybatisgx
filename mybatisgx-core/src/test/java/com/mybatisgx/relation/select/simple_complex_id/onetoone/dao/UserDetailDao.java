package com.mybatisgx.relation.select.simple_complex_id.onetoone.dao;

import com.mybatisgx.dao.SimpleDao;
import com.mybatisgx.entity.MultiId;
import com.mybatisgx.relation.select.simple_complex_id.onetoone.entity.UserDetail;
import org.apache.ibatis.annotations.Mapper;

@Mapper public interface UserDetailDao extends SimpleDao<UserDetail, UserDetail, MultiId> {}
