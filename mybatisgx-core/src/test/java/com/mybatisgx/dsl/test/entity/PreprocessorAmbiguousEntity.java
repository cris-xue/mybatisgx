package com.mybatisgx.model.handler.test.entity;

import com.mybatisgx.annotation.*;
import com.mybatisgx.entity.BaseEntity;

/**
 * 测试实体：同时有 name 和 nameLike 字段
 * （有歧义场景）
 */
@Entity
@Table(name = "test_preprocessor_ambiguous")
public class PreprocessorAmbiguousEntity extends BaseEntity<Long> {

    private String name;

    private String nameLike;

    private Integer age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameLike() {
        return nameLike;
    }

    public void setNameLike(String nameLike) {
        this.nameLike = nameLike;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}
