package com.lc.mybatisx.annotation.handler;

import com.lc.mybatisx.annotation.Lock;
import com.lc.mybatisx.model.ColumnInfo;
import org.apache.ibatis.mapping.SqlCommandType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LockGenerateValueHandler implements GenerateValueHandler<Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LockGenerateValueHandler.class);

    @Override
    public Object next(SqlCommandType sqlCommandType, ColumnInfo columnInfo, Object originalValue) {
        Lock lock = columnInfo.getLock();
        if (lock == null) {
            return originalValue;
        }
        if (sqlCommandType == SqlCommandType.INSERT) {
            return lock.initValue();
        }
        if (originalValue == null) {
            LOGGER.warn("乐观锁值不存在，会造成数据不安全!");
        }
        return originalValue;
    }

}
