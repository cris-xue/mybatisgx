package com.mybatisgx.model.handler.test.entity;

import com.mybatisgx.annotation.*;
import com.mybatisgx.entity.BaseEntity;

/**
 * 测试实体：有 lockAnd 字段，无 lock 字段（字段名以 And 结尾的无歧义场景）
 */
@Entity
@Table(name = "test_preprocessor_lock_and")
public class PreprocessorLockAndEntity extends BaseEntity<Long> {

    private String lockAnd;

    private Integer age;

    public String getLockAnd() {
        return lockAnd;
    }

    public void setLockAnd(String lockAnd) {
        this.lockAnd = lockAnd;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}
