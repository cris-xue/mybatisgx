package com.lc.mybatisx.converter;

import org.apache.ibatis.reflection.MetaObject;

public interface MetaObjectHandler {

    void set(MetaObject metaObject, Object object);

}
