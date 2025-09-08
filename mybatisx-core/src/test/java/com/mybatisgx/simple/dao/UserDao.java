package com.mybatisgx.simple.dao;

import com.lc.mybatisx.dao.SimpleDao;
import com.mybatisgx.simple.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserDao extends SimpleDao<User, Long> {
}
