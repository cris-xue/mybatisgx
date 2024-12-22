package com.lc.mybatisx.annotation.handler;

import com.lc.mybatisx.model.ColumnInfo;
import org.apache.ibatis.mapping.SqlCommandType;

import java.util.UUID;

public class UuidGenerateValueHandler implements GenerateValueHandler<String> {

    @Override
    public String next(SqlCommandType sqlCommandType, ColumnInfo columnInfo, Object originalValue) {
        if (sqlCommandType == SqlCommandType.INSERT) {
            return UUID.randomUUID().toString();
        }
        return (String) originalValue;
    }

}
