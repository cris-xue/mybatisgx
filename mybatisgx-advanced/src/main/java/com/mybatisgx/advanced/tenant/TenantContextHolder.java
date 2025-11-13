package com.mybatisgx.advanced.tenant;

public class TenantContextHolder {

    private static final ThreadLocal<String> TENANT_ID = new ThreadLocal();

    public static void set(String tenantId) {
        TENANT_ID.set(tenantId);
    }

    public static String get() {
        return TENANT_ID.get();
    }
}
