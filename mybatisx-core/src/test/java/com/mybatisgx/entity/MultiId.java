package com.mybatisgx.entity;

import java.io.Serializable;

public class MultiId<ID> implements Serializable {

    private ID id1;

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
