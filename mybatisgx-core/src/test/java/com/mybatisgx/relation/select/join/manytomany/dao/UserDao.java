package com.mybatisgx.relation.select.join.manytomany.dao;

import com.mybatisgx.dao.SimpleDao;
import com.mybatisgx.relation.select.join.manytomany.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserDao extends SimpleDao<User, Long> {
}
