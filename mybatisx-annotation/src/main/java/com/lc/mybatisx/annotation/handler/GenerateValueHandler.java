package com.lc.mybatisx.annotation.handler;

import org.apache.ibatis.mapping.SqlCommandType;

public interface GenerateValueHandler<T> {

    T next(SqlCommandType sqlCommandType, JavaColumnInfo javaColumnInfo, Object originalValue);

    default T insert(SqlCommandType sqlCommandType, JavaColumnInfo javaColumnInfo, Object originalValue) {
        return null;
    }

    default T update(SqlCommandType sqlCommandType, JavaColumnInfo javaColumnInfo, Object originalValue) {
        return null;
    }

}
