package com.mybatisgx.relation.select.simple.dao;

import com.mybatisgx.dao.SimpleDao;
import com.mybatisgx.entity.MultiId;
import com.mybatisgx.relation.select.simple.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserDao extends SimpleDao<User, MultiId> {
}
