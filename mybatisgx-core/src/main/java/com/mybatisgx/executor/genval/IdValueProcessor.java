package com.mybatisgx.executor.genval;

import com.mybatisgx.annotation.Id;
import com.mybatisgx.api.FieldMeta;
import com.mybatisgx.api.ValueProcessContext;
import com.mybatisgx.api.ValueProcessPhase;
import com.mybatisgx.api.ValueProcessor;
import com.mybatisgx.context.MybatisgxObjectFactory;
import com.mybatisgx.exception.MybatisgxException;
import com.mybatisgx.executor.keygen.KeyGenerator;

import java.util.EnumSet;

/**
 * 一句话描述
 * @author 薛承城
 * @date 2025/12/14 19:05
 */
public class IdValueProcessor implements ValueProcessor {

    @Override
    public boolean supports(FieldMeta fieldMeta) {
        return fieldMeta.hasAnnotation(Id.class);
    }

    @Override
    public EnumSet<ValueProcessPhase> phases() {
        return EnumSet.of(ValueProcessPhase.INSERT);
    }

    @Override
    public Object process(ValueProcessContext context) {
        KeyGenerator keyGenerator = MybatisgxObjectFactory.get(KeyGenerator.class);
        if (keyGenerator == null) {
            throw new MybatisgxException("keyGenerator is null");
        }
        return keyGenerator.get();
    }
}
