package com.mybatisgx.relation.select.mgxql.basic.dao;

import com.mybatisgx.annotation.Statement;
import com.mybatisgx.dao.SimpleDao;
import com.mybatisgx.relation.select.simple_simple_id.onetoone.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * MGXQL WHERE 操作符场景测试 DAO
 *
 * @author 薛承城
 * @date 2026/7/7
 */
@Mapper
public interface MgxqlSelectWhereDao extends SimpleDao<User, User, Long> {

    @Statement("select * from User u where u.id = :id")
    User findUserById(@Param("id") Long id);

    @Statement("select * from User u where u.code != :code")
    List<User> findByCodeNotEq(@Param("code") String code);

    @Statement("select * from User u where u.id < :id")
    List<User> findByIdLessThan(@Param("id") Long id);

    @Statement("select * from User u where u.id <= :id")
    List<User> findByIdLessThanEqual(@Param("id") Long id);

    @Statement("select * from User u where u.id > :id")
    List<User> findByIdGreaterThan(@Param("id") Long id);

    @Statement("select * from User u where u.id >= :id")
    List<User> findByIdGreaterThanEqual(@Param("id") Long id);

    @Statement("select * from User u where u.code like :code")
    List<User> findByCodeLike(@Param("code") String code);

    @Statement("select * from User u where u.code left like :code")
    List<User> findByCodeLeftLike(@Param("code") String code);

    @Statement("select * from User u where u.code right like :code")
    List<User> findByCodeRightLike(@Param("code") String code);

    @Statement("select * from User u where u.id in :ids")
    List<User> findByIdIn(@Param("ids") List<Long> ids);

    @Statement("select * from User u where u.id between :ids")
    List<User> findByIdBetween(@Param("ids") List<Long> ids);

    @Statement("select * from User u where u.code is null")
    List<User> findByCodeIsNull();

    @Statement("select * from User u where u.code is not null")
    List<User> findByCodeIsNotNull();

    @Statement("select * from User u where u.id = :id or u.code = :code")
    List<User> findOrCondition(@Param("id") Long id, @Param("code") String code);

    @Statement("select * from User u where u.id > :id and(u.code like :code or u.code is not null)")
    List<User> findAndOrCondition(@Param("id") Long id, @Param("code") String code);

    @Statement("select * from User u where u.id not in :ids")
    List<User> findNotIn(@Param("ids") List<Long> ids);

    @Statement("select * from User u where u.code not like :code")
    List<User> findNotLike(@Param("code") String code);

    @Statement("select * from User u where u.id not between :ids")
    List<User> findNotBetween(@Param("ids") List<Long> ids);
}
