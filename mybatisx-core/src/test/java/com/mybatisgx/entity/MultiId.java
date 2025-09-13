package com.mybatisgx.entity;

import java.io.Serializable;

public class MultiId<ID> implements Serializable {

    private ID testId1;

    private ID testId2;

    public ID getTestId1() {
        return testId1;
    }

    public void setTestId1(ID testId1) {
        this.testId1 = testId1;
    }

    public ID getTestId2() {
        return testId2;
    }

    public void setTestId2(ID testId2) {
        this.testId2 = testId2;
    }
}
