package com.mybatisgx.projection.entity;

import com.mybatisgx.annotation.Entity;
import com.mybatisgx.annotation.Table;

@Entity
@Table(name = "custom_condition_user")
public class User extends BaseEntity<Long> {

    private String name;

    private Integer age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}
