package com.mybatisgx.custom.condition.base;

import com.mybatisgx.annotation.GeneratedValue;
import com.mybatisgx.annotation.Id;
import com.mybatisgx.annotation.IdClass;
import com.mybatisgx.executor.genval.IdValueProcessor;

import java.io.Serializable;

@IdClass(MultiId.class)
public class MultiId<ID> implements Serializable {

    @Id
    @GeneratedValue({IdValueProcessor.class})
    private ID id1;

    @Id
    @GeneratedValue({IdValueProcessor.class})
    private ID id2;

    public ID getId1() {
        return id1;
    }

    public void setId1(ID id1) {
        this.id1 = id1;
    }

    public ID getId2() {
        return id2;
    }

    public void setId2(ID id2) {
        this.id2 = id2;
    }
}
