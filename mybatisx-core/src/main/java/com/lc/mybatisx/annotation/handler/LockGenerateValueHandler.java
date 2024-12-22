package com.lc.mybatisx.annotation.handler;

import com.lc.mybatisx.annotation.Lock;
import com.lc.mybatisx.model.ColumnInfo;
import org.apache.ibatis.mapping.SqlCommandType;

public class LockGenerateValueHandler implements GenerateValueHandler<Integer> {

    @Override
    public Integer next(SqlCommandType sqlCommandType, ColumnInfo columnInfo, Object originalValue) {
        Lock lock = columnInfo.getLock();
        if (lock == null) {
            return (Integer) originalValue;
        }
        if (sqlCommandType == SqlCommandType.INSERT) {
            return lock.initValue();
        }
        if (originalValue == null) {
            throw new RuntimeException("");
        }
        return (Integer) originalValue;
    }

}
