package com.mybatisgx.dsl.test.dao;

import com.mybatisgx.dao.SelectDao;
import com.mybatisgx.dsl.test.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserEntityDao extends SelectDao<UserEntity, UserEntity> {
}
