package com.mybatisgx.custom.condition.test;

import com.mybatisgx.annotation.LogicDeleteId;
import com.mybatisgx.api.FieldMeta;
import com.mybatisgx.api.ValueProcessContext;
import com.mybatisgx.api.ValueProcessPhase;
import com.mybatisgx.api.ValueProcessor;
import com.mybatisgx.executor.keygen.SnowKeyGenerator;

import java.util.EnumSet;

public class LogicDeleteIdValueProcessor implements ValueProcessor {

    private static SnowKeyGenerator snowKeyGenerator = new SnowKeyGenerator();

    @Override
    public boolean supports(FieldMeta fieldMeta) {
        return fieldMeta.hasAnnotation(LogicDeleteId.class);
    }

    @Override
    public EnumSet<ValueProcessPhase> phases() {
        return EnumSet.of(ValueProcessPhase.INSERT, ValueProcessPhase.LOGIC_DELETE);
    }

    @Override
    public Object process(ValueProcessContext context) {
        if (context.getPhase() == ValueProcessPhase.INSERT) {
            return 0;
        }
        if (context.getPhase() == ValueProcessPhase.LOGIC_DELETE) {
            return snowKeyGenerator.get();
        }
        throw new RuntimeException("不支持的ValueProcessPhase");
    }
}
