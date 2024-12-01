package com.lc.mybatisx.model.handler;

import com.lc.mybatisx.annotation.Table;
import com.lc.mybatisx.dao.Dao;
import com.lc.mybatisx.dao.SimpleDao;
import com.lc.mybatisx.model.AssociationTableInfo;
import com.lc.mybatisx.model.ColumnInfo;
import com.lc.mybatisx.model.MapperInfo;
import com.lc.mybatisx.model.TableInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.TypeUtils;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.reflect.Type;
import java.util.List;

/**
 * @author ：薛承城
 * @description：用于解析mybatis接口
 * @date ：2023/12/1
 */
public class TableInfoHandler {

    private static final Logger logger = LoggerFactory.getLogger(TableInfoHandler.class);

    private ColumnInfoHandler columnInfoHandler = new ColumnInfoHandler();

    public TableInfo execute(MapperInfo mapperInfo) {
        Class<?> entityClass = mapperInfo.getEntityClass();
        List<ColumnInfo> columnInfoList = columnInfoHandler.getColumnInfoList(entityClass);
        List<AssociationTableInfo> associationTableInfoList = columnInfoHandler.getAssociationTableInfoList(entityClass);
        TableInfo tableInfo = new TableInfo();
        tableInfo.setTableName(entityClass.getAnnotation(Table.class).name());
        tableInfo.setColumnInfoList(columnInfoList);
        tableInfo.setAssociationTableInfoList(associationTableInfoList);
        return tableInfo;
    }

    public TableInfo execute(Class<?> daoInterface) {
        Type[] daoInterfaceParams = getDaoInterfaceParams(daoInterface);
        Class<?> entityClass = (Class<?>) daoInterfaceParams[0];
        List<ColumnInfo> columnInfoList = columnInfoHandler.getColumnInfoList(entityClass);
        TableInfo tableInfo = new TableInfo();
        tableInfo.setTableName(entityClass.getAnnotation(Table.class).name());
        tableInfo.setColumnInfoList(columnInfoList);
        return tableInfo;
    }

    private static Type[] getDaoInterfaceParams(Class<?> daoInterface) {
        Type[] daoSuperInterfaces = daoInterface.getGenericInterfaces();
        for (int i = 0; i < daoSuperInterfaces.length; i++) {
            Type daoSuperInterfaceType = daoSuperInterfaces[i];
            ParameterizedTypeImpl daoSuperInterfaceClass = (ParameterizedTypeImpl) daoSuperInterfaceType;
            Type[] daoInterfaceParams = daoSuperInterfaceClass.getActualTypeArguments();
            if (TypeUtils.isAssignable(Dao.class, daoInterface)) {
                return daoInterfaceParams;
            }
        }
        logger.info("{} un extend {}", SimpleDao.class.getName());
        return null;
    }

}
