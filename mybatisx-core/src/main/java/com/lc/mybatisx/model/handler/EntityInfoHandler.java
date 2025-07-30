package com.lc.mybatisx.model.handler;

import com.lc.mybatisx.annotation.Table;
import com.lc.mybatisx.model.ColumnInfo;
import com.lc.mybatisx.model.EntityInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author ：薛承城
 * @description：用于解析mybatis接口
 * @date ：2023/12/1
 */
public class EntityInfoHandler {

    private static final Logger logger = LoggerFactory.getLogger(EntityInfoHandler.class);

    private ColumnInfoHandler columnInfoHandler = new ColumnInfoHandler();

    public EntityInfo execute(Class<?> entityClass) {
        List<ColumnInfo> columnInfoList = columnInfoHandler.getColumnInfoList(entityClass);
        EntityInfo entityInfo = new EntityInfo();
        entityInfo.setTableName(entityClass.getAnnotation(Table.class).name());
        entityInfo.setClazz(entityClass);
        entityInfo.setClazzName(entityClass.getName());
        entityInfo.setColumnInfoList(columnInfoList);
        return entityInfo;
    }
}
