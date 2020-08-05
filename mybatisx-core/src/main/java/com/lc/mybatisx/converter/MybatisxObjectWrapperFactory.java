package com.lc.mybatisx.converter;

import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.wrapper.BeanWrapper;
import org.apache.ibatis.reflection.wrapper.ObjectWrapper;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;

import java.util.Collection;
import java.util.Map;

public class MybatisxObjectWrapperFactory implements ObjectWrapperFactory {

    private MetaObjectHandler metaObjectHandler;

    public MybatisxObjectWrapperFactory(MetaObjectHandler metaObjectHandler) {
        this.metaObjectHandler = metaObjectHandler;
    }

    @Override
    public boolean hasWrapperFor(Object object) {
        if (object instanceof Collection || object instanceof Map) {
            return false;
        }
        return true;
    }

    @Override
    public ObjectWrapper getWrapperFor(MetaObject metaObject, Object object) {
        metaObjectHandler.set(metaObject, object);
        return new BeanWrapper(metaObject, object);
    }
}
