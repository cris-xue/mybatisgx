package com.mybatisgx.method.validator.param.entity;

import com.mybatisgx.annotation.Entity;
import com.mybatisgx.annotation.GeneratedValue;
import com.mybatisgx.annotation.Id;
import com.mybatisgx.annotation.Table;
import com.mybatisgx.executor.genval.IdValueProcessor;

@Entity
@Table(name = "validator_user")
public class ValidatorUser {

    @Id
    @GeneratedValue(IdValueProcessor.class)
    private Long id;

    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
