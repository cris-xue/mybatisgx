package com.mybatisgx.relation.select.mgxql.manytomany.dao;

import com.mybatisgx.annotation.Statement;
import com.mybatisgx.dao.SimpleDao;
import com.mybatisgx.relation.select.simple_simple_id.manytomany.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * MGXQL 多对多 join 渲染测试 DAO
 *
 * @author 薛承城
 * @date 2026/6/30
 */
@Mapper
public interface MgxqlManyToManyJoinDao extends SimpleDao<User, User, Long> {

    @Statement("select * from User u left join Role r on u = r")
    List<User> findJoinStarManyToMany();

    @Statement("select u.* from User u left join Role r on u = r")
    List<User> findJoinAliasStar();
}
