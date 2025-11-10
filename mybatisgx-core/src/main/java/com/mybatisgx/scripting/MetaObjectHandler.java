package com.mybatisgx.scripting;

import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.reflection.MetaObject;

public interface MetaObjectHandler extends Cloneable {

    void fillParameterObject(SqlCommandType sqlCommandType, MetaObject metaObject);

    Object insert(String field, Object object, Class<?> clazz);

    Object update(String field, Object object, Class<?> clazz);

}
