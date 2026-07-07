package com.mybatisgx.relation.select.mgxql.basic.dao;

import com.mybatisgx.annotation.Statement;
import com.mybatisgx.dao.SimpleDao;
import com.mybatisgx.relation.select.simple_simple_id.onetoone.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * MGXQL DELETE / UPDATE 场景测试 DAO
 *
 * @author 薛承城
 * @date 2026/7/7
 */
@Mapper
public interface MgxqlDmlDao extends SimpleDao<User, User, Long> {

    @Statement("delete User where id = :id")
    int deleteByMgxqlId(@Param("id") Long id);

    @Statement("delete User where code = :code")
    int deleteByMgxqlCode(@Param("code") String code);

    @Statement("update User where id = :id")
    int updateByMgxqlId(@Param("id") Long id, User entity);

    @Statement("update User where code = :code")
    int updateByMgxqlCode(@Param("code") String code, User entity);
}
