package com.lc.mybatisx.annotation.handler;

import org.apache.ibatis.mapping.SqlCommandType;

public interface GenerateValueHandler<T> {

    T next(SqlCommandType sqlCommandType);

}
