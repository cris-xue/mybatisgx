package com.mybatisgx.dsl.mgxql;

import com.mybatisgx.annotation.Entity;
import com.mybatisgx.annotation.Statement;
import com.mybatisgx.dao.SimpleDao;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface StatementRoutingDao extends SimpleDao<StatementRoutingEntity, StatementRoutingEntity, Long> {

    @Statement("delete where name = :name")
    void deleteByName(@Param("name") String name);

    @Statement("update where name = :name")
    int updateByName(@Param("name") String name, StatementRoutingEntity entity);

    void deleteByNameAndAge(@Param("name") String name, @Param("age") Integer age);
}
