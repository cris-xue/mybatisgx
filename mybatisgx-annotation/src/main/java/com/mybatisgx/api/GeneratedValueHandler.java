package com.mybatisgx.api;

/**
 * 字段值生成处理器接口
 * @author 薛承城
 * @date 2025/12/3 10:21
 */
public interface GeneratedValueHandler<T> {

    T insert(Object columnInfo, Object originalValue);

    T update(Object columnInfo, Object originalValue);

    T logicDelete(Object columnInfo);
}
