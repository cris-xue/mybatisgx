package com.mybatisgx.method.validator.param.entity.select;

import com.mybatisgx.annotation.Entity;
import com.mybatisgx.annotation.GeneratedValue;
import com.mybatisgx.annotation.Id;
import com.mybatisgx.annotation.Table;
import com.mybatisgx.executor.genval.IdValueProcessor;

@Entity
@Table(name = "select_mapper_user")
public class MapperUser {

    @Id
    @GeneratedValue(IdValueProcessor.class)
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
