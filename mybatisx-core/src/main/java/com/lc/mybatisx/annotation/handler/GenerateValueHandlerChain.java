package com.lc.mybatisx.annotation.handler;

import com.lc.mybatisx.model.ColumnInfo;
import org.apache.ibatis.mapping.SqlCommandType;

public interface GenerateValueHandlerChain<T> {

    T next(SqlCommandType sqlCommandType, ColumnInfo columnInfo, Object originalValue);

}
