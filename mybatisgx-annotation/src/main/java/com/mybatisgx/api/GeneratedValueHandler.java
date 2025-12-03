package com.mybatisgx.api;

public interface GeneratedValueHandler<T> {

    T insert(JavaColumnInfo javaColumnInfo, Object originalValue);

    T update(JavaColumnInfo javaColumnInfo, Object originalValue);

}
