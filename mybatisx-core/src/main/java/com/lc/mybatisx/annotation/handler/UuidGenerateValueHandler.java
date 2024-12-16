package com.lc.mybatisx.annotation.handler;

import org.apache.ibatis.mapping.SqlCommandType;

import java.util.UUID;

public class UuidGenerateValueHandler implements GenerateValueHandler<String> {

    @Override
    public String next(SqlCommandType sqlCommandType) {
        return UUID.randomUUID().toString();
    }

}
