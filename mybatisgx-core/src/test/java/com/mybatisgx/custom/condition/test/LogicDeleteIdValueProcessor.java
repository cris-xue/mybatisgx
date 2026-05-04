package com.mybatisgx.custom.condition.test;

import com.mybatisgx.annotation.LogicDeleteId;
import com.mybatisgx.executor.keygen.SnowKeyGenerator;
import com.mybatisgx.spi.FieldMeta;
import com.mybatisgx.spi.ValueProcessCommandType;
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
    public EnumSet<ValueProcessCommandType> commandTypes() {
        return EnumSet.of(ValueProcessCommandType.INSERT, ValueProcessCommandType.LOGIC_DELETE);
    }

    @Override
    public Object process(ValueProcessContext context) {
        if (context.getCommandType() == ValueProcessCommandType.INSERT) {
            return 0;
        }
        if (context.getCommandType() == ValueProcessCommandType.LOGIC_DELETE) {
            return snowKeyGenerator.get();
        }
        throw new RuntimeException("不支持的ValueProcessPhase");
    }
}
