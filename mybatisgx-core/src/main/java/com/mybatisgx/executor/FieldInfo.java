package com.mybatisgx.executor;

import com.mybatisgx.model.ColumnInfo;
import com.mybatisgx.spi.FieldMeta;

import java.lang.annotation.Annotation;

/**
 * @author：薛承城
 * @description：一句话描述
 * @date：2026/4/25 14:20
 */
public class FieldInfo implements FieldMeta {

    private final ColumnInfo columnInfo;

    public FieldInfo(ColumnInfo columnInfo) {
        this.columnInfo = columnInfo;
    }

    @Override
    public String getJavaColumnName() {
        return this.columnInfo.getJavaColumnName();
    }

    @Override
    public Class<?> getJavaType() {
        return this.columnInfo.getJavaType();
    }

    @Override
    public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
        return this.columnInfo.getField().getAnnotation(annotationType);
    }

    @Override
    public boolean hasAnnotation(Class<? extends Annotation> annotationType) {
        return this.columnInfo.getField().getAnnotation(annotationType) != null;
    }
}
