package com.mybatisgx.relation.select.mgxql.onetoone.dao;

import com.mybatisgx.annotation.Statement;
import com.mybatisgx.dao.SimpleDao;
import com.mybatisgx.relation.select.simple_simple_id.onetoone.entity.User;
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
    List<User> findJoinList();

    @Statement("select u.code from User u left join UserDetail ud on u = ud")
    List<User> findProjectionList();

    @Statement("select count(u.id) from User u")
    long countAll();
}
