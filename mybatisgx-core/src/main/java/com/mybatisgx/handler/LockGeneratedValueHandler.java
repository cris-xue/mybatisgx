package com.mybatisgx.handler;

import com.mybatisgx.annotation.Lock;
import com.mybatisgx.api.GeneratedValueHandler;
import com.mybatisgx.api.JavaColumnInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LockGeneratedValueHandler implements GeneratedValueHandler<Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LockGeneratedValueHandler.class);

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
