package com.lc.mybatisx.annotation.handler;

import com.lc.mybatisx.annotation.Id;
import com.lc.mybatisx.model.ColumnInfo;
import org.apache.ibatis.mapping.SqlCommandType;

public class GenerateValueChain {

    private IdGenerateValueHandler<?> idGenerateValueHandler;

    public GenerateValueChain(IdGenerateValueHandler<?> idGenerateValueHandler) {
        this.idGenerateValueHandler = idGenerateValueHandler;
    }

    public Object next(SqlCommandType sqlCommandType, ColumnInfo columnInfo, Object originalValue) {
        GenerateValueHandler<?> generateValueHandler = columnInfo.getGenerateValueHandler();
        if (generateValueHandler != null) {
            return generateValueHandler.next(sqlCommandType, columnInfo, originalValue);
        }
        Id id = columnInfo.getId();
        if (id != null) {
            return this.idGenerateValueHandler.next(sqlCommandType, columnInfo, originalValue);
        }
        return originalValue;
    }

}
