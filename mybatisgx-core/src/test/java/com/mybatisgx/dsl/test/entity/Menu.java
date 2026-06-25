package com.mybatisgx.dsl.test.entity;

import com.mybatisgx.annotation.Entity;
import com.mybatisgx.annotation.Table;
import com.mybatisgx.entity.BaseEntity;

@Entity
@Table(name = "test_menu")
public class Menu extends BaseEntity<Long> {

    private String name;

    private String code;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
