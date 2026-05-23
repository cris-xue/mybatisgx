package com.mybatisgx.relation.error.onetomany.dao;

import com.mybatisgx.dao.SimpleDao;
import com.mybatisgx.relation.error.onetomany.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserDao extends SimpleDao<User, User, Long> {
}
