package com.lc.mybatisx.model.handler;

import com.lc.mybatisx.annotation.Table;
import com.lc.mybatisx.context.EntityInfoContextHolder;
import com.lc.mybatisx.model.ColumnInfo;
import com.lc.mybatisx.model.EntityInfo;
import com.lc.mybatisx.utils.TypeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * @author ：薛承城
 * @description：用于解析mybatis接口
 * @date ：2023/12/1
 */
public class EntityInfoHandler {

    private static final Logger logger = LoggerFactory.getLogger(EntityInfoHandler.class);

    private ColumnInfoHandler columnInfoHandler = new ColumnInfoHandler();

    public EntityInfo execute(Class<?> entityClass) {
        Map<Type, Class<?>> typeParameterMap = TypeUtils.getTypeParameterMap(entityClass);
        List<ColumnInfo> columnInfoList = columnInfoHandler.getColumnInfoList(entityClass, typeParameterMap);
        EntityInfo entityInfo = new EntityInfo.Builder()
                .setTableName(entityClass.getAnnotation(Table.class).name())
                .setClazz(entityClass)
                .setColumnInfoList(columnInfoList)
                .setTypeParameterMap(typeParameterMap)
                .process()
                .build();
        /*entityInfo.setTableName(entityClass.getAnnotation(Table.class).name());
        entityInfo.setClazz(entityClass);
        entityInfo.setClazzName(entityClass.getName());
        entityInfo.setColumnInfoList(columnInfoList);
        entityInfo.setTypeParameterMap(typeParameterMap);*/
        return entityInfo;
    }

    public void processColumnRelation() {
        List<Class<?>> entityClassList = EntityInfoContextHolder.getEntityClassList();
        for (Class<?> entityClass : entityClassList) {
            EntityInfo entityInfo = EntityInfoContextHolder.get(entityClass);
            this.columnInfoHandler.processRelation(entityInfo);
        }
    }
}
