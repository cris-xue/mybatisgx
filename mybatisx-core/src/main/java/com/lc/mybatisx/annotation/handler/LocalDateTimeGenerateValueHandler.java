package com.lc.mybatisx.annotation.handler;

import com.lc.mybatisx.model.ColumnInfo;
import org.apache.ibatis.mapping.SqlCommandType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeGenerateValueHandler implements GenerateValueHandler<String> {

    @Override
    public String next(SqlCommandType sqlCommandType, ColumnInfo columnInfo, Object originalValue) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.now().format(formatter);
    }

}
