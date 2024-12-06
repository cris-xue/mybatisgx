package com.lc.mybatisx.model;

import com.lc.mybatisx.annotation.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2021/7/9 17:34
 */
public class EntityInfo {

    /**
     * 表名称
     */
    private String tableName;
    /**
     * 表实体类型
     */
    private Class<?> tableEntityClass;
    /**
     * 字段信息列表
     */
    private List<ColumnInfo> columnInfoList;
    /**
     * java字段映射字段信息，userName={userName=1}
     */
    private Map<String, ColumnInfo> columnInfoMap = new LinkedHashMap<>();
    /**
     * id字段列表
     */
    private List<ColumnInfo> idColumnInfoList = new ArrayList<>();
    /**
     * 表字段信息
     */
    private List<ColumnInfo> tableColumnInfoList = new ArrayList<>();
    /**
     * 逻辑删除
     */
    private ColumnInfo logicDeleteColumnInfo;
    /**
     * 乐观锁
     */
    private ColumnInfo lockColumnInfo;
    /**
     * 关联字段信息
     */
    private List<ColumnInfo> associationColumnInfoList = new ArrayList<>();

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Class<?> getTableEntityClass() {
        return tableEntityClass;
    }

    public void setTableEntityClass(Class<?> tableEntityClass) {
        this.tableEntityClass = tableEntityClass;
    }

    public List<ColumnInfo> getColumnInfoList() {
        return columnInfoList;
    }

    public void setColumnInfoList(List<ColumnInfo> columnInfoList) {
        this.columnInfoList = columnInfoList;
        columnInfoList.forEach(columnInfo -> {
            Id id = columnInfo.getId();
            if (id != null) {
                idColumnInfoList.add(columnInfo);
            }
            Lock lock = columnInfo.getLock();
            if (lock != null) {
                lockColumnInfo = columnInfo;
            }
            LogicDelete logicDelete = columnInfo.getLogicDelete();
            if (logicDelete != null) {
                logicDeleteColumnInfo = columnInfo;
            }
            Boolean associationSelect = columnInfo.getAssociationSelect();
            if (!associationSelect) {
                tableColumnInfoList.add(columnInfo);
            }

            ManyToMany manyToMany = columnInfo.getManyToMany();
            if (manyToMany != null) {
                associationColumnInfoList.add(columnInfo);
            }
            ManyToOne manyToOne = columnInfo.getManyToOne();
            if (manyToOne != null) {
                associationColumnInfoList.add(columnInfo);
            }
            OneToOne oneToOne = columnInfo.getOneToOne();
            if (oneToOne != null) {
                associationColumnInfoList.add(columnInfo);
            }
            OneToMany oneToMany = columnInfo.getOneToMany();
            if (oneToMany != null) {
                associationColumnInfoList.add(columnInfo);
            }

            columnInfoMap.put(columnInfo.getJavaColumnName(), columnInfo);
        });
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

    public List<ColumnInfo> getIdColumnInfoList() {
        return idColumnInfoList;
    }

    public void setIdColumnInfoList(List<ColumnInfo> idColumnInfoList) {
        this.idColumnInfoList = idColumnInfoList;
    }

    public List<ColumnInfo> getTableColumnInfoList() {
        return tableColumnInfoList;
    }

    public void setTableColumnInfoList(List<ColumnInfo> tableColumnInfoList) {
        this.tableColumnInfoList = tableColumnInfoList;
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

    public List<ColumnInfo> getAssociationColumnInfoList() {
        return associationColumnInfoList;
    }

    public void setAssociationColumnInfoList(List<ColumnInfo> associationColumnInfoList) {
        this.associationColumnInfoList = associationColumnInfoList;
    }
}
