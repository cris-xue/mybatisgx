package com.lc.mybatisx.test.model.entity;

import com.lc.mybatisx.annotation.*;

@Entity
@Table(name = "order")
public class Order extends BaseEntity<Long> {

    @Id
    private Long tenantId;

    private String name;

    private String code;

    /*@ManyToOne
    @JoinColumn(name = "user_id")*/
    private Long userId;

    @LogicDelete
    private Integer status;

    @Lock
    private Integer version;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
