package com.mybatisgx.simple.dao;

import com.mybatisgx.annotation.Dynamic;
import com.mybatisgx.dao.SimpleDao;
import com.mybatisgx.simple.entity.User;
import com.mybatisgx.simple.entity.UserQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface UserDao extends SimpleDao<User, Long> {

    @Dynamic
    int insertNew(User user, User user1);

    List<User> findByNameLike(String name);

    @Dynamic
    User findByNameLikeAndId(@Param("name") String name, Long id);

    List<User> findByIdIn(List<Long> ids);

    List<User> findByInputTimeBetween(List<LocalDateTime> inputTimeList);

    @Dynamic
    List<User> findListNew(UserQuery userQuery);

    @Dynamic
    List<User> findListNew1111(@Param("id") Long id, UserQuery userQuery);

    @Dynamic
    List<User> findListNew2222(Long id, UserQuery userQuery);

    int findCountNameByName(@Param("name") String name);
}
