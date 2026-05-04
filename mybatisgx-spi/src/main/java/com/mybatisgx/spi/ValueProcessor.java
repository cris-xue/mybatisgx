package com.mybatisgx.spi;

import java.util.EnumSet;

public interface ValueProcessor {

    /**
     * 是否支持当前字段
     */
    boolean supports(FieldMeta fieldMeta);

    /**
     * 支持的命令类型
     */
    EnumSet<ValueProcessCommandType> commandTypes();

    /**
     * 执行值处理
     */
    Object process(ValueProcessContext context);
}
