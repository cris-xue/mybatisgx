package com.mybatisgx.dao;

import com.lc.mybatisx.dao.SimpleDao;
import com.mybatisgx.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserDao extends SimpleDao<User, Long> {
}
