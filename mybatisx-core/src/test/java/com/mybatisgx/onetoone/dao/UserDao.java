package com.mybatisgx.onetoone.dao;

import com.lc.mybatisx.dao.SimpleDao;
import com.mybatisgx.entity.MultiId;
import com.mybatisgx.onetoone.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserDao extends SimpleDao<User, MultiId> {
}
