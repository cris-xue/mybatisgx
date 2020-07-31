package com.lc.mybatisx.test.model.entity;

import java.time.LocalDateTime;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2020/7/31 15:18
 */
public abstract class BaseEntity<ID> {

    private ID id;

    private String inputUserId;

    private LocalDateTime inputTime;

    public ID getId() {
        return id;
    }

    public void setId(ID id) {
        this.id = id;
    }

    public String getInputUserId() {
        return inputUserId;
    }

    public void setInputUserId(String inputUserId) {
        this.inputUserId = inputUserId;
    }

    public LocalDateTime getInputTime() {
        return inputTime;
    }

    public void setInputTime(LocalDateTime inputTime) {
        this.inputTime = inputTime;
    }
}
