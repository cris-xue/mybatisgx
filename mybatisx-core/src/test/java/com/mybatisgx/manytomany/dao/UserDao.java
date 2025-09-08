package com.mybatisgx.manytomany.dao;

import com.lc.mybatisx.dao.SimpleDao;
import com.mybatisgx.manytomany.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserDao extends SimpleDao<User, Long> {
}
