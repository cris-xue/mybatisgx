package com.mybatisgx.custom.condition.entity;

import com.mybatisgx.annotation.QueryEntity;

import java.util.List;

@QueryEntity
public class UserQuery extends User {

    private String nameLike;

    private List<Long> idBetween;

    private List<Long> idIn;

    public String getNameLike() {
        return nameLike;
    }

    public void setNameLike(String nameLike) {
        this.nameLike = nameLike;
    }

    public List<Long> getIdBetween() {
        return idBetween;
    }

    public void setIdBetween(List<Long> idBetween) {
        this.idBetween = idBetween;
    }

    public List<Long> getIdIn() {
        return idIn;
    }

    public void setIdIn(List<Long> idIn) {
        this.idIn = idIn;
    }
}
