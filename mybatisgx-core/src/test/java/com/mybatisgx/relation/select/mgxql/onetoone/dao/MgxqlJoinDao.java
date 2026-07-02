package com.mybatisgx.relation.select.mgxql.onetoone.dao;

import com.mybatisgx.annotation.Statement;
import com.mybatisgx.dao.SimpleDao;
import com.mybatisgx.relation.select.mgxql.onetoone.dto.*;
import com.mybatisgx.relation.select.simple_simple_id.onetoone.entity.User;
import com.mybatisgx.relation.select.simple_simple_id.onetoone.entity.UserDetail;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * MGXQL join 渲染测试 DAO，复用 simple_simple_id 四层一对一实体（User→UserDetail→UserDetailItem1→UserDetailItem2）。
 *
 * @author 薛承城
 * @date 2026/6/27
 */
@Mapper
public interface MgxqlJoinDao extends SimpleDao<User, User, Long> {

    @Statement("select u.* from User u left join UserDetail ud on u = ud left join UserDetailItem1 i1 on ud = i1 left join UserDetailItem2 i2 on i1 = i2")
    List<User> findJoinList1();

    @Statement("select u.* from User u left join UserDetail ud on u = ud")
    List<User> findJoinList2();

    @Statement("select ud.* from User u left join UserDetail ud on u = ud")
    List<User> findJoinList3();

    @Statement("select ud.* from User u left join UserDetail ud on u = ud")
    List<UserDetail> findJoinList4();

    @Statement("select ud.* from User u left join UserDetail ud on u = ud")
    List<UserProjection> findJoinList5();

    @Statement("select u.code from User u left join UserDetail ud on u = ud")
    List<User> findProjectionList();

    @Statement("select count(u.id) from User u")
    long countAll();

    @Statement("select * from User u left join UserDetail ud on u = ud")
    List<User> findJoinStarTwoEntity();

    // === 投影多层嵌套测试方法 ===

    // 单层投影 DTO（无嵌套字段）
    @Statement("select u.* from User u left join UserDetail ud on u = ud")
    List<UserFlatProjection> findFlatProjection();

    // 一层嵌套投影（User → UserDetail）
    @Statement("select u.* from User u left join UserDetail ud on u = ud")
    List<UserOneLevelProjection> findOneLevelProjection();

    // 多层嵌套投影（User → UserDetail → UserDetailItem1 → UserDetailItem2）
    @Statement("select u.* from User u left join UserDetail ud on u = ud left join UserDetailItem1 i1 on ud = i1 left join UserDetailItem2 i2 on i1 = i2")
    List<UserMultiLevelProjection> findMultiLevelProjection();

    // 跳层匹配投影（User 直接包含 userDetailItem2，跳过 UserDetailItem1）
    @Statement("select u.* from User u left join UserDetail ud on u = ud left join UserDetailItem1 i1 on ud = i1 left join UserDetailItem2 i2 on i1 = i2")
    List<UserSkipProjection> findSkipLevelProjection();

    // 字段名不匹配投影（unknownRelation 字段在路径链中不存在）
    @Statement("select u.* from User u left join UserDetail ud on u = ud left join UserDetailItem1 i1 on ud = i1 left join UserDetailItem2 i2 on i1 = i2")
    List<UserUnknownFieldProjection> findUnknownFieldProjection();
}
