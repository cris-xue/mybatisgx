package com.mybatisgx.custom.condition.dao;

import com.mybatisgx.annotation.Dynamic;
import com.mybatisgx.annotation.Sql;
import com.mybatisgx.custom.condition.entity.User;
import com.mybatisgx.custom.condition.entity.UserQuery;
import com.mybatisgx.dao.SimpleDao;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface UserDao extends SimpleDao<User, UserQuery, Long> {

    @Dynamic
    int insertNew(User user);

    int deleteByIdAndName(Long id, String name);

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

    long countByNameByName(@Param("name") String name);

    List<User> findTop5ByNameLikeOrderByNameDesc(String name);

    List<User> findTop5ByNameStartingWithOrderByNameDesc(String name);

    List<User> findTop5ByNameEndingWithOrderByNameDesc(String name);

    List<User> findByIdNotInAndNameNotLike(List<Long> idList, String name);

    @Dynamic
    List<User> findByNameNotNullAndNameLike(@Param("name") String name);

    List<User> findByNameNotNull();

    List<User> findByNameIsNotNull();

    List<User> findByNameIsNull();

    List<User> findByNameIsNullAndNameIsNotNullAndNameNotNull();

    @Sql("findByIdAnd(NameLikeOrNameIn)")
    List<User> findCustomSql(Long id, String name, List<String> nameList);
}
