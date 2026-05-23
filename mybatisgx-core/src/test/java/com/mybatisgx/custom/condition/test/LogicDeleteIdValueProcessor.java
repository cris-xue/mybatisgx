package com.mybatisgx.custom.condition.test;

import com.mybatisgx.annotation.LogicDeleteId;
import com.mybatisgx.api.MethodCommandType;
import com.mybatisgx.executor.keygen.SnowKeyGenerator;
import com.mybatisgx.spi.FieldMeta;
import com.mybatisgx.spi.ValueProcessContext;
import com.mybatisgx.spi.ValueProcessor;

import java.util.EnumSet;

public class LogicDeleteIdValueProcessor implements ValueProcessor {

    private static SnowKeyGenerator snowKeyGenerator = new SnowKeyGenerator();

    @Override
    public boolean supports(FieldMeta fieldMeta) {
        return fieldMeta.hasAnnotation(LogicDeleteId.class);
    }

    @Override
    public EnumSet<MethodCommandType> commandTypes() {
        return EnumSet.of(MethodCommandType.INSERT, MethodCommandType.LOGIC_DELETE);
    }

    @Override
    public Object process(ValueProcessContext context) {
        if (context.getCommandType() == MethodCommandType.INSERT) {
            return 0;
        }
        if (context.getCommandType() == MethodCommandType.LOGIC_DELETE) {
            return snowKeyGenerator.get();
        }
        throw new RuntimeException("不支持的ValueProcessPhase");
    }
}
