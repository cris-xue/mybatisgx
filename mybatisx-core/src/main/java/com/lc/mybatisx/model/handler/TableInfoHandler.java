package com.lc.mybatisx.model.handler;

import com.lc.mybatisx.annotation.Table;
import com.lc.mybatisx.model.ColumnInfo;
import com.lc.mybatisx.model.TableInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author ：薛承城
 * @description：用于解析mybatis接口
 * @date ：2023/12/1
 */
public class TableInfoHandler {

    private static final Logger logger = LoggerFactory.getLogger(TableInfoHandler.class);

    private ColumnInfoHandler columnInfoHandler = new ColumnInfoHandler();

    public TableInfo execute(Class<?> entityClass) {
        List<ColumnInfo> columnInfoList = columnInfoHandler.getColumnInfoList(entityClass);
        // List<AssociationTableInfo> associationTableInfoList = columnInfoHandler.getAssociationTableInfoList(entityClass);
        TableInfo tableInfo = new TableInfo();
        tableInfo.setTableName(entityClass.getAnnotation(Table.class).name());
        tableInfo.setColumnInfoList(columnInfoList);
        // tableInfo.setAssociationTableInfoList(associationTableInfoList);
        return tableInfo;
    }

}
