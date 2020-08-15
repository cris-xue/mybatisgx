package com.lc.mybatisx.scripting;

import org.apache.ibatis.reflection.MetaObject;

public interface MetaObjectHandler extends Cloneable {

    void fillParameterObject(MetaObject metaObject);

    Object insert(String field, Object object, Class<?> clazz);

    Object update(String field, Object object, Class<?> clazz);

}
