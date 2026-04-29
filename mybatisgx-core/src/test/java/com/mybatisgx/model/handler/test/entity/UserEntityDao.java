package com.mybatisgx.model.handler.test.entity;

import com.mybatisgx.dao.SelectDao;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserEntityDao extends SelectDao<UserEntity, UserEntity> {
}
