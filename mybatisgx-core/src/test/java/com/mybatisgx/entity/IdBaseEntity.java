package com.mybatisgx.entity;

import com.mybatisgx.annotation.GeneratedValue;
import com.mybatisgx.annotation.Id;
import com.mybatisgx.executor.genval.IdValueProcessor;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2020/7/31 15:18
 */
public abstract class IdBaseEntity<ID> {

    @Id
    @GeneratedValue(IdValueProcessor.class)
    protected ID id;

    public ID getId() {
        return id;
    }

    public void setId(ID id) {
        this.id = id;
    }
}
