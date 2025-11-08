package com.mybatisgx.simple.dao;

import com.lc.mybatisx.annotation.Dynamic;
import com.lc.mybatisx.dao.SimpleDao;
import com.mybatisgx.simple.entity.User;
import com.mybatisgx.simple.entity.UserQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserDao extends SimpleDao<User, Long> {

    User findByNameLike(String name);

    @Dynamic
    User findByNameLikeAndId(@Param("name") String name, Long id);

    User findByIdIn(List<Long> ids);

    User findByIdBetween(List<Long> ids);

    @Dynamic
    List<User> findListNew(UserQuery userQuery);

    @Dynamic
    List<User> findListNew1111(Long id, UserQuery userQuery);
}
