package com.mybatisgx.relation.select.mgxql.onetoone.dao;

import com.mybatisgx.annotation.Statement;
import com.mybatisgx.dao.SimpleDao;
import com.mybatisgx.relation.select.simple_simple_id.onetoone.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * MGXQL join 渲染测试 DAO，复用 simple_simple_id 四层一对一实体（User→UserDetail→UserDetailItem1→UserDetailItem2）。
 *
 * @author 薛承城
 * @date 2026/6/27
 */
@Mapper
public interface MgxqlJoinWhereDao extends SimpleDao<User, User, Long> {

    @Statement("select u.* from User u where u.id = :id")
    User findUser(@Param("id") Long id);

    @Statement("select u.* from User u left join UserDetail ud on u = ud where u.id = :id")
    User findUserJoinDetailByUserId(@Param("id") Long id);

    @Statement("select u.* from User u left join UserDetail ud on u = ud where ud.code = :code")
    List<User> findUserJoinDetailByDetailCode(@Param("code") String code);
}
