package com.lc.mybatisx.test.model.entity;

import com.lc.mybatisx.annotation.Column;

import javax.persistence.Id;
import java.time.LocalDateTime;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2020/7/31 15:18
 */
public abstract class BaseEntity<ID> {

    @Id
    private ID id;

    @Column(insertable = true)
    private String inputUserId;

    @Column(insertable = true)
    private LocalDateTime inputTime;

    @Column(updatable = true)
    private String updateUserId;

    @Column(updatable = true)
    private LocalDateTime updateTime;

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

    public String getUpdateUserId() {
        return updateUserId;
    }

    public void setUpdateUserId(String updateUserId) {
        this.updateUserId = updateUserId;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}
