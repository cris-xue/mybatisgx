package com.mybatisgx.custom.condition.entity;

import com.mybatisgx.annotation.EmbeddedId;
import com.mybatisgx.annotation.Entity;
import com.mybatisgx.annotation.Table;
import com.mybatisgx.entity.MultiId;

@Entity
@Table(name = "custom_condition_multi_id_user")
public class MultiIdUser {

    @EmbeddedId
    private MultiId<Long> id;

    private String code;

    private String name;

    public MultiId<Long> getId() {
        return id;
    }

    public void setId(MultiId<Long> id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
