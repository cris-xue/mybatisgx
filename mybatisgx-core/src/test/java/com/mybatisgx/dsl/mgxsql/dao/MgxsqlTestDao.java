package com.mybatisgx.dsl.mgxsql.dao;

import com.mybatisgx.dsl.mgxsql.entity.MgxsqlTestUser;
import com.mybatisgx.ext.scripting.xmltags.MgxsqlLanguageDriver;
import org.apache.ibatis.annotations.Lang;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * mgxsql 集成测试 Dao（使用 @Lang + @Select/@Update）
 *
 * @author 薛承城
 * @date 2026/7/9
 */
public interface MgxsqlTestDao {

    /**
     * 使用 mgxsql 语法查询（where + # 条件）
     */
    @Lang(MgxsqlLanguageDriver.class)
    @Select("select * from mgxsql_test_user where #user_name = :userName")
    List<MgxsqlTestUser> findByName(@Param("userName") String userName);

    /**
     * 使用 mgxsql 语法查询（where + #[body] 条件体）
     */
    @Lang(MgxsqlLanguageDriver.class)
    @Select("select * from mgxsql_test_user where #[user_name = :userName and age = :age]")
    List<MgxsqlTestUser> findByNameAndAge(@Param("userName") String userName, @Param("age") Integer age);

    /**
     * 使用 mgxsql 语法更新（set + where）
     */
    @Lang(MgxsqlLanguageDriver.class)
    @Update("update mgxsql_test_user set user_name = :userName where id = :id")
    int updateName(@Param("id") Long id, @Param("userName") String userName);

    /**
     * 不含 mgxsql 语法的标准 MyBatis 查询（透传测试）
     */
    @Lang(MgxsqlLanguageDriver.class)
    @Select("select * from mgxsql_test_user where id = #{id}")
    MgxsqlTestUser findById(@Param("id") Long id);
}
