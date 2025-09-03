package com.mybatisgx.dao.test;

import com.lc.mybatisx.dao.SimpleDao;
import com.mybatisgx.dao.test.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserDao extends SimpleDao<User, Long> {
}
