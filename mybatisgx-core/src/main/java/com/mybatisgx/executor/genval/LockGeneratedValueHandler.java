package com.mybatisgx.executor.genval;

import com.mybatisgx.annotation.Lock;
import com.mybatisgx.model.ColumnInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 一句话描述
 * @author 薛承城
 * @date 2025/12/3 10:17
 */
public class LockGeneratedValueHandler extends AbstractGeneratedValueHandler<Integer> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LockGeneratedValueHandler.class);

    @Override
    public Integer insert(ColumnInfo columnInfo, Object originalValue) {
        Lock lock = columnInfo.getLock();
        return lock.initValue();
    }

    @Override
    public Integer update(ColumnInfo columnInfo, Object originalValue) {
        if (originalValue == null) {
            LOGGER.warn("乐观锁值不存在，会造成数据不安全!");
        }
        return (Integer) originalValue;
    }
}
