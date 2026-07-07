package com.mybatisgx.relation.select.mgxql.basic.dao;

import com.mybatisgx.annotation.Statement;
import com.mybatisgx.dao.SimpleDao;
import com.mybatisgx.relation.select.simple_simple_id.onetoone.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * MGXQL ORDER BY + LIMIT 场景测试 DAO
 *
 * @author 薛承城
 * @date 2026/7/7
 */
@Mapper
public interface MgxqlSelectOrderLimitDao extends SimpleDao<User, User, Long> {

    @Statement("select * from User u order by u.id asc")
    List<User> findOrderByIdAsc();

    @Statement("select * from User u order by u.id desc")
    List<User> findOrderByIdDesc();

    @Statement("select * from User u order by u.code asc, u.id desc")
    List<User> findOrderByCodeAndId();

    @Statement("select * from User u order by u.id asc limit 0, 5")
    List<User> findLimit();

    @Statement("select * from User u order by u.id asc limit 5, 5")
    List<User> findLimitOffset();
}
