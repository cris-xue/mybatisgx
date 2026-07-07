package com.mybatisgx.relation.select.mgxql.basic.dao;

import com.mybatisgx.annotation.Statement;
import com.mybatisgx.dao.SimpleDao;
import com.mybatisgx.relation.select.simple_simple_id.onetoone.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * MGXQL GROUP BY + HAVING 场景测试 DAO
 * <p>
 * 注意：SQL标准要求GROUP BY时SELECT列必须出现在GROUP BY子句或聚合函数中，
 * 因此GROUP BY场景使用纯聚合SELECT或GROUP BY字段+聚合函数。
 * HAVING中引用的字段需要使用表别名前缀。
 *
 * @author 薛承城
 * @date 2026/7/7
 */
@Mapper
public interface MgxqlSelectGroupByHavingDao extends SimpleDao<User, User, Long> {

    @Statement("select count(u.id) from User u group by u.code")
    List<Map<String, Object>> findGroupByCode();

    @Statement("select count(u.id) from User u group by u.code having count(u.id) > :minCount")
    List<Map<String, Object>> findGroupByCodeHavingCount(@Param("minCount") long minCount);

    @Statement("select count(u.id) from User u group by u.code having count(u.id) > :minCount and max(u.id) > :minId")
    List<Map<String, Object>> findGroupByCodeHavingAnd(@Param("minCount") long minCount, @Param("minId") long minId);

    @Statement("select count(u.id) from User u group by u.code having count(u.id) > :minCount or max(u.id) > :minId")
    List<Map<String, Object>> findGroupByCodeHavingOr(@Param("minCount") long minCount, @Param("minId") long minId);
}
