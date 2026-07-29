package com.mybatisgx.dsl.mgxsql.dao;

import com.mybatisgx.dsl.mgxsql.entity.MgxsqlTestUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * mgxsql XML 集成测试 Dao（SQL 在 XML 中，配合 default-scripting-language 使用）
 *
 * @author 薛承城
 * @date 2026/7/9
 */
public interface MgxsqlXmlTestDao {

    List<MgxsqlTestUser> findByName(@Param("userName") String userName);

    List<MgxsqlTestUser> findByNameAndAge(@Param("userName") String userName, @Param("age") Integer age);

    int updateName(@Param("id") Long id, @Param("userName") String userName);

    MgxsqlTestUser findById(@Param("id") Long id);
}
