package com.mybatisgx.annotation.handler;

public interface GenerateValueHandler<T> {

    T insert(JavaColumnInfo javaColumnInfo, Object originalValue);

    T update(JavaColumnInfo javaColumnInfo, Object originalValue);

}
