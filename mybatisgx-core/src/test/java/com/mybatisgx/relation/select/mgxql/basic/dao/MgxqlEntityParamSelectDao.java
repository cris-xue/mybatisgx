package com.mybatisgx.relation.select.mgxql.basic.dao;

import com.mybatisgx.annotation.Statement;
import com.mybatisgx.dao.SimpleDao;
import com.mybatisgx.relation.select.simple_simple_id.onetoone.entity.MgxqlEntityParamUserQuery;
import com.mybatisgx.relation.select.simple_simple_id.onetoone.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * MGXQL 实体参数 SELECT 场景测试 DAO
 *
 * @author 薛承城
 * @date 2026/7/24
 */
@Mapper
public interface MgxqlEntityParamSelectDao extends SimpleDao<User, MgxqlEntityParamUserQuery, Long> {

    @Statement("select * from User u where u.code = :code")
    List<User> findByEntityCode(User entity);

    @Statement("select * from User u where #[u.code = :code]")
    List<User> findByEntityBracketCode(User entity);

    @Statement("select * from User u where #if(:code != null)[u.code = :code]")
    List<User> findByEntityIfCode(User entity);

    @Statement("select * from User u where #choose[#when(:code != null)[u.code = :code] #otherwise[u.id is not null]]")
    List<User> findByEntityChooseCode(User entity);

    @Statement("select * from User u where u.code like %:code%")
    List<User> findByEntityCodeLike(User entity);

    @Statement("select * from User u where #[u.code = :code] #[and u.inputUserId = :inputUserId] #[or u.updateUserId = :updateUserId]")
    List<User> findByEntityBracketAndOr(User entity);

    @Statement("select * from User u where u.id in :id")
    List<User> findByQueryEntityIdIn(MgxqlEntityParamUserQuery query);

    @Statement("select * from User u where u.id in (item:userList)=>$item.id")
    List<User> findByQueryEntityComplexIn(MgxqlEntityParamUserQuery query);
}
