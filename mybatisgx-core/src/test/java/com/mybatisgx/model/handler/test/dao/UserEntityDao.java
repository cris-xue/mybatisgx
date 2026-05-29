package com.mybatisgx.model.handler.test.dao;

import com.mybatisgx.dao.SelectDao;
import com.mybatisgx.model.handler.test.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserEntityDao extends SelectDao<UserEntity, UserEntity> {
}
