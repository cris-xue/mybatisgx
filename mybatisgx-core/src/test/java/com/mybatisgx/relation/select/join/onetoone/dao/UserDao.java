package com.mybatisgx.relation.select.join.onetoone.dao;

import com.mybatisgx.dao.SimpleDao;
import com.mybatisgx.entity.MultiId;
import com.mybatisgx.relation.select.join.onetoone.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserDao extends SimpleDao<User, MultiId> {
}
