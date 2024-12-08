package com.lc.mybatisx.test.model.entity;

import com.lc.mybatisx.annotation.ConditionEntity;

import javax.persistence.Table;

@ConditionEntity
@Table(name = "user")
public class UserQuery extends User {

    private String nameLike;

    public String getNameLike() {
        return nameLike;
    }

    public void setNameLike(String nameLike) {
        this.nameLike = nameLike;
    }

}
