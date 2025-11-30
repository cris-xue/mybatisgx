package com.mybatisgx.relation.select.simple.onetomany.dao;

import com.mybatisgx.dao.SimpleDao;
import com.mybatisgx.relation.select.simple.onetomany.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserDao extends SimpleDao<User, Long> {
}
