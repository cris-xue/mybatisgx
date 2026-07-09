package com.mybatisgx.dsl.mgxsql.entity;

import com.mybatisgx.annotation.Column;
import com.mybatisgx.annotation.Entity;
import com.mybatisgx.annotation.Id;
import com.mybatisgx.annotation.Table;

/**
 * mgxsql 集成测试实体
 *
 * @author 薛承城
 * @date 2026/7/9
 */
@Entity
@Table(name = "mgxsql_test_user")
public class MgxsqlTestUser {

    @Id
    private Long id;

    @Column(name = "user_name")
    private String userName;

    private Integer age;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}
