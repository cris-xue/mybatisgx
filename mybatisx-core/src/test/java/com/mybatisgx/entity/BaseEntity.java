package com.mybatisgx.entity;

import com.lc.mybatisx.annotation.Column;
import com.lc.mybatisx.annotation.Id;

import java.time.LocalDateTime;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2020/7/31 15:18
 */
public abstract class BaseEntity<ID> {

    @Id
    private ID id;

    @Column(name = "input_user_id", insertable = true)
    private Long inputUserId;

    @Column(name = "input_time", insertable = true)
    private LocalDateTime inputTime;

    @Column(name = "update_user_id", updatable = true)
    private Long updateUserId;

    @Column(name = "update_time", updatable = true)
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
