package com.mybatisgx.annotation.handler;

import com.mybatisgx.annotation.Lock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LockGenerateValueHandler implements GenerateValueHandler<Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LockGenerateValueHandler.class);

    @Override
    public Object insert(JavaColumnInfo javaColumnInfo, Object originalValue) {
        Lock lock = javaColumnInfo.getLock();
        return lock.initValue();
    }

    @Override
    public Object update(JavaColumnInfo javaColumnInfo, Object originalValue) {
        if (originalValue == null) {
            LOGGER.warn("乐观锁值不存在，会造成数据不安全!");
        }
        return originalValue;
    }
}
