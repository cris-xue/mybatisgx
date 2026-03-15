package com.mybatisgx.method.validator.param.entity.update;

import com.mybatisgx.annotation.QueryEntity;

@QueryEntity(ValidatorUser.class)
public class ValidatorUserQuery extends ValidatorUser {

    private String nameLike;

    public String getNameLike() {
        return nameLike;
    }

    public void setNameLike(String nameLike) {
        this.nameLike = nameLike;
    }
}
