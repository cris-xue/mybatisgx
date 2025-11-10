package com.mybatisgx.simple.entity;

import com.mybatisgx.annotation.QueryEntity;

import java.util.List;

@QueryEntity
public class UserQuery extends User {

    private String nameLike;

    private List<Long> idBetween;

    private List<Long> idIn;

    /*private Long idIsNull;

    private Long idIsNotNull;*/

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

    /*public Long getIdIsNull() {
        return idIsNull;
    }

    public void setIdIsNull(Long idIsNull) {
        this.idIsNull = idIsNull;
    }

    public Long getIdIsNotNull() {
        return idIsNotNull;
    }

    public void setIdIsNotNull(Long idIsNotNull) {
        this.idIsNotNull = idIsNotNull;
    }*/
}
