package com.lc.mybatisx.annotation.handler;

import com.lc.mybatisx.model.ColumnInfo;
import org.apache.ibatis.mapping.SqlCommandType;

public interface GenerateValueHandler<T> {

    T next(SqlCommandType sqlCommandType, ColumnInfo columnInfo, Object originalValue);

    default T insert(SqlCommandType sqlCommandType, ColumnInfo columnInfo, Object originalValue) {
        return null;
    }

    default T update(SqlCommandType sqlCommandType, ColumnInfo columnInfo, Object originalValue) {
        return null;
    }

}
