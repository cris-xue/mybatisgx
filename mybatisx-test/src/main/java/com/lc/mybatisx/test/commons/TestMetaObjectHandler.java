package com.lc.mybatisx.test.commons;

import com.lc.mybatisx.scripting.MetaObjectHandler;

import java.time.LocalDateTime;

public class TestMetaObjectHandler implements MetaObjectHandler {

    @Override
    public Object insert(String field, Object object, Class<?> clazz) {
        if ("inputUserId".equals(field)) {
            return 111L;
        } else if ("inputTime".equals(field)) {
            return LocalDateTime.now();
        }
        return null;
    }

    @Override
    public Object update(String field, Object object, Class<?> clazz) {
        if ("updateUserId".equals(field)) {
            return 123L;
        } else if ("updateTime".equals(field)) {
            return LocalDateTime.now();
        }
        return null;
    }

}
