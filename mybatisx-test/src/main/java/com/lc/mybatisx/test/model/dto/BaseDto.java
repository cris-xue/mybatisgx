package com.lc.mybatisx.test.model.dto;

import java.time.LocalDateTime;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2020/7/31 15:18
 */
public abstract class BaseDto<ID> {

    private ID id;

    private Long inputUserId;

    private LocalDateTime inputTime;

    private Long updateUserId;

    private LocalDateTime updateTime;

    public ID getId() {
        return id;
    }

    public void setId(ID id) {
        this.id = id;
    }

    public Long getInputUserId() {
        return inputUserId;
    }

    public void setInputUserId(Long inputUserId) {
        this.inputUserId = inputUserId;
    }

    public LocalDateTime getInputTime() {
        return inputTime;
    }

    public void setInputTime(LocalDateTime inputTime) {
        this.inputTime = inputTime;
    }

    public Long getUpdateUserId() {
        return updateUserId;
    }

    public void setUpdateUserId(Long updateUserId) {
        this.updateUserId = updateUserId;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}
