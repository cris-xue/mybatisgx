package com.lc.mybatisx.wrapper;

import java.lang.reflect.Field;

/**
 * @author ：薛承城
 * @description：字段包装器
 * @date ：2022/1/30 14:21
 */
public class FieldWrapper {

    private Field field;

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }
}
