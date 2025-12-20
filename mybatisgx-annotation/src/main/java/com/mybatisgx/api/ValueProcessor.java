package com.mybatisgx.api;

import java.util.EnumSet;

public interface ValueProcessor {

    /**
     * 是否支持当前字段
     */
    boolean supports(FieldMeta fieldMeta);

    /**
     * 支持的生命周期
     */
    EnumSet<ValueProcessPhase> phases();

    /**
     * 执行值处理
     */
    Object process(ValueProcessContext context);
}
