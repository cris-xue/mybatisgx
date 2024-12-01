package com.lc.mybatisx.model;

import com.lc.mybatisx.annotation.Lock;
import com.lc.mybatisx.annotation.LogicDelete;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2021/7/9 17:34
 */
public class TableInfo {

    /**
     * 表名称
     */
    private String tableName;
    /**
     * 字段信息列表
     */
    private List<ColumnInfo> columnInfoList;
    /**
     * java字段映射字段信息，userName={userName=1}
     */
    private Map<String, ColumnInfo> columnInfoMap;
    /**
     * 逻辑删除
     */
    private ColumnInfo logicDeleteColumnInfo;
    /**
     * 乐观锁
     */
    private ColumnInfo lockColumnInfo;
    /**
     * 表关联信息
     */
    private List<ManyToManyInfo> associationTableInfoList;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<ColumnInfo> getColumnInfoList() {
        return columnInfoList;
    }

    public void setColumnInfoList(List<ColumnInfo> columnInfoList) {
        this.columnInfoList = columnInfoList;
        Map<String, ColumnInfo> columnInfoMap = new HashMap<>();
        columnInfoList.forEach(columnInfo -> {
            Lock lock = columnInfo.getLock();
            if (lock != null) {
                lockColumnInfo = columnInfo;
            }
            LogicDelete logicDelete = columnInfo.getDelete();
            if (logicDelete != null) {
                logicDeleteColumnInfo = columnInfo;
            }
            columnInfoMap.put(columnInfo.getJavaColumnName(), columnInfo);
        });
        this.columnInfoMap = columnInfoMap;
    }

    public Map<String, ColumnInfo> getColumnInfoMap() {
        return columnInfoMap;
    }

    public void setColumnInfoMap(Map<String, ColumnInfo> columnInfoMap) {
        this.columnInfoMap = columnInfoMap;
    }

    public ColumnInfo getColumnInfo(String javaColumnName) {
        return this.columnInfoMap.get(javaColumnName);
    }

    public ColumnInfo getLogicDeleteColumnInfo() {
        return logicDeleteColumnInfo;
    }

    public void setLogicDeleteColumnInfo(ColumnInfo logicDeleteColumnInfo) {
        this.logicDeleteColumnInfo = logicDeleteColumnInfo;
    }

    public ColumnInfo getLockColumnInfo() {
        return lockColumnInfo;
    }

    public void setLockColumnInfo(ColumnInfo lockColumnInfo) {
        this.lockColumnInfo = lockColumnInfo;
    }

    public List<ManyToManyInfo> getAssociationTableInfoList() {
        return associationTableInfoList;
    }

    public void setAssociationTableInfoList(List<ManyToManyInfo> associationTableInfoList) {
        this.associationTableInfoList = associationTableInfoList;
    }
}
