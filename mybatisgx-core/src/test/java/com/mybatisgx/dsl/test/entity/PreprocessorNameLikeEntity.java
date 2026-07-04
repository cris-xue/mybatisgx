package com.mybatisgx.model.handler.test.entity;

import com.mybatisgx.annotation.*;
import com.mybatisgx.entity.BaseEntity;

/**
 * 测试实体：有 nameLike 字段，无 name 字段
 * （无歧义场景）
 */
@Entity
@Table(name = "test_preprocessor_name_like")
public class PreprocessorNameLikeEntity extends BaseEntity<Long> {

    private String nameLike;

    private Integer age;

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
