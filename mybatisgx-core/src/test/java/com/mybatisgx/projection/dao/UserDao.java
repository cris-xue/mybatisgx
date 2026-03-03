package com.mybatisgx.projection.dao;

import com.mybatisgx.dao.SimpleDao;
import com.mybatisgx.projection.dto.UserDto;
import com.mybatisgx.projection.entity.User;
import com.mybatisgx.projection.entity.UserQuery;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserDao extends SimpleDao<User, UserQuery, Long> {

    List<UserDto> findByName(String name);
}
