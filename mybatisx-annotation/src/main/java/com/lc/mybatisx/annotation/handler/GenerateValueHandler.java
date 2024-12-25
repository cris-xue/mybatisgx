package com.lc.mybatisx.annotation.handler;

public interface GenerateValueHandler<T> {

    T insert(JavaColumnInfo javaColumnInfo, Object originalValue);

    T update(JavaColumnInfo javaColumnInfo, Object originalValue);

}
