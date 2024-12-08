package com.lc.mybatisx.test.model.entity;

import com.lc.mybatisx.annotation.Entity;
import com.lc.mybatisx.annotation.Id;
import com.lc.mybatisx.annotation.Table;

@Entity
@Table(name = "user_detail")
public class UserDetail extends BaseEntity<Long> {

    @Id
    private Long tenantId;

    private String code;

    /*@OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")*/
    private Long userId;

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
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
}
