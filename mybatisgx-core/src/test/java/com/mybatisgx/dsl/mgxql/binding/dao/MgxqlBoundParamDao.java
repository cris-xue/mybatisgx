package com.mybatisgx.dsl.mgxql.binding.dao;

import com.mybatisgx.annotation.Statement;
import com.mybatisgx.dao.SimpleDao;
import com.mybatisgx.relation.select.simple_simple_id.onetomany.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * MGXQL 绑定层测试 DAO：WHERE 条件带/不带实体别名
 * <p>
 * 使用 onetomany User（无 desc/having 等关键字字段），避免方法名语法糖解析冲突
 *
 * @author 薛承城
 * @date 2026/6/30
 */
@Mapper
public interface MgxqlBoundParamDao extends SimpleDao<User, User, Long> {

    @Statement("select * from User u left join Team t on u = t where u.code = :code")
    List<User> findJoinWhereWithAlias(@Param("code") String code);

    @Statement("select * from User u where u.code = :code")
    List<User> findWhereWithAlias(@Param("code") String code);
}
