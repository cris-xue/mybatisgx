package com.lc.mybatisx.test.model.dto;

import com.lc.mybatisx.test.model.entity.BaseEntity;

public class UserDetailDto extends BaseEntity<Long> {

    private Long tenantId;

    private String code;

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

}
