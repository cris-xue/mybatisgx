package com.mybatisgx.executor.genval;

import com.mybatisgx.executor.keygen.SnowGeneratedValueHandler;
import com.mybatisgx.model.ColumnInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 一句话描述
 * @author 薛承城
 * @date 2025/12/3 9:30
 */
public class IdSnowGeneratedValueHandler extends AbstractGeneratedValueHandler<Long> {

    private static final Logger LOGGER = LoggerFactory.getLogger(IdSnowGeneratedValueHandler.class);

    private static SnowGeneratedValueHandler snowGeneratedValueHandler = new SnowGeneratedValueHandler();

    @Override
    public Long insert(ColumnInfo columnInfo, Object originalValue) {
        return snowGeneratedValueHandler.get();
    }

    @Override
    public Long update(ColumnInfo columnInfo, Object originalValue) {
        return null;
    }
}
