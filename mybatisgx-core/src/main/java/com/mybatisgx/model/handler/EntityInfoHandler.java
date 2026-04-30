package com.mybatisgx.model.handler;

import com.mybatisgx.annotation.Entity;
import com.mybatisgx.annotation.Property;
import com.mybatisgx.annotation.QueryEntity;
import com.mybatisgx.annotation.Table;
import com.mybatisgx.context.EntityInfoContextHolder;
import com.mybatisgx.exception.MybatisgxException;
import com.mybatisgx.model.ColumnInfo;
import com.mybatisgx.model.EntityInfo;
import com.mybatisgx.utils.TypeUtils;
import org.apache.commons.lang3.StringUtils;
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
        Entity entity = entityClass.getAnnotation(Entity.class);
        Table table = null;
        if (entity != null) {
            table = entityClass.getAnnotation(Table.class);
            if (table == null) {
                throw new MybatisgxException("实体必须有 Table 注解: %s", entityClass.getName());
            }
        }

        QueryEntity queryEntity = entityClass.getAnnotation(QueryEntity.class);
        if (queryEntity != null && table == null) {
            Class<?> target = queryEntity.value();
            if (target.getAnnotation(Entity.class) == null) {
                throw new MybatisgxException("@QueryEntity 指向的类必须是实体: %s", target.getName());
            }
            table = target.getAnnotation(Table.class);
            if (table == null) {
                throw new MybatisgxException("实体必须有 Table 注解: %s", target.getName());
            }
        }

        Map<Type, Class<?>> typeParameterMap = TypeUtils.getTypeParameterMap(entityClass);
        List<ColumnInfo> columnInfoList = columnInfoHandler.getColumnInfoList(entityClass, typeParameterMap);
        EntityInfo entityInfo = new EntityInfo.Builder()
                .setTableName(table.name())
                .setClazz(entityClass)
                .setColumnInfoList(columnInfoList)
                .setTypeParameterMap(typeParameterMap)
                .process()
                .build();
        this.validatePropertyExist(entityInfo);
        return entityInfo;
    }

    public void processColumnRelation() {
        List<Class<?>> entityClassList = EntityInfoContextHolder.getEntityClassList();
        for (Class<?> entityClass : entityClassList) {
            EntityInfo entityInfo = EntityInfoContextHolder.get(entityClass);
            this.columnInfoHandler.processRelation(entityInfo);
        }
    }

    /**
     * 校验 Property 注解的字段是否有对应的实体字段，如果不存在，字段编写错误
     * @param entityInfo
     */
    private void validatePropertyExist(EntityInfo entityInfo) {
        for (ColumnInfo columnInfo : entityInfo.getColumnInfoList()) {
            Property property = columnInfo.getProperty();
            if (property == null) {
                continue;
            }

            String propertyName = property.name();
            if (StringUtils.isBlank(propertyName)) {
                throw new MybatisgxException(
                        "实体 [%s] 字段 [%s] 的 @Property.name 不能为空",
                        entityInfo.getClazzName(),
                        columnInfo.getJavaColumnName()
                );
            }

            if (entityInfo.getColumnInfo(propertyName) == null) {
                throw new MybatisgxException(
                        "实体 [%s] 字段 [%s] 的 @Property(name=\"%s\") 未找到对应实体字段",
                        entityInfo.getClazzName(),
                        columnInfo.getJavaColumnName(),
                        propertyName
                );
            }
        }
    }
}
