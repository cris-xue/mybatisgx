package com.mybatisgx.relation.select.mgxql.manytomany.dao;

import com.mybatisgx.annotation.Statement;
import com.mybatisgx.dao.SimpleDao;
import com.mybatisgx.relation.select.mgxql.manytomany.dto.*;
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

    // === 投影多层嵌套测试方法 ===

    // 单层投影 DTO（无嵌套字段）
    @Statement("select u.* from User u left join Role r on u = r")
    List<UserFlatProjection> findFlatProjection();

    // 一层嵌套投影（User → Role）
    @Statement("select u.* from User u left join Role r on u = r")
    List<UserProjection> findOneLevelProjection();

    // 多层嵌套投影（User → Role → Menu）
    @Statement("select u.* from User u left join Role r on u = r left join Menu m on r = m")
    List<UserProjection> findMultiLevelProjection();

    // 跳层匹配投影（User 直接包含 menuList，跳过 Role）
    @Statement("select u.* from User u left join Role r on u = r left join Menu m on r = m")
    List<UserSkipProjection> findSkipLevelProjection();

    // 字段名不匹配投影
    @Statement("select u.* from User u left join Role r on u = r left join Menu m on r = m")
    List<UserUnknownFieldProjection> findUnknownFieldProjection();
}
