package com.mybatisgx.executor.genval;

import com.mybatisgx.api.GeneratedValueHandler;
import com.mybatisgx.model.ColumnInfo;

/**
 * 一句话描述
 * @author 薛承城
 * @date 2025/12/3 10:14
 */
public abstract class AbstractGeneratedValueHandler<T> implements GeneratedValueHandler<T> {
    @Override
    public T insert(Object columnInfo, Object originalValue) {
        return this.insert((ColumnInfo) columnInfo, originalValue);
    }

    @Override
    public T update(Object columnInfo, Object originalValue) {
        return this.update((ColumnInfo) columnInfo, originalValue);
    }

    abstract T insert(ColumnInfo columnInfo, Object originalValue);

    abstract T update(ColumnInfo columnInfo, Object originalValue);
}
