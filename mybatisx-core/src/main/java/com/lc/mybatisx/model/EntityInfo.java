package com.lc.mybatisx.model;

import com.lc.mybatisx.annotation.Id;
import com.lc.mybatisx.annotation.Lock;
import com.lc.mybatisx.annotation.LogicDelete;
import com.lc.mybatisx.annotation.ManyToMany;
import com.lc.mybatisx.annotation.handler.GenerateValueHandler;
import org.apache.commons.lang3.StringUtils;

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
     * 实体类型
     */
    private Class<?> clazz;
    /**
     * 实体类型名称
     */
    private String clazzName;
    /**
     * 字段信息列表
     */
    private List<ColumnInfo> columnInfoList;
    /**
     * java字段映射字段信息，userName={userName=1}
     */
    private Map<String, ColumnInfo> columnInfoMap = new LinkedHashMap();
    /**
     * 数据库字段和java字段映射信息，如：user_name=userName
     */
    private Map<String, String> dbColumnInfoMap = new LinkedHashMap();
    /**
     * id字段列表
     */
    private List<ColumnInfo> idColumnInfoList = new ArrayList();
    /**
     * 生成值字段列表
     */
    private List<ColumnInfo> generateValueColumnInfoList = new ArrayList<>();
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
     * 关系字段信息
     */
    private List<ColumnInfo> relationColumnInfoList = new ArrayList<>();

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public String getClazzName() {
        return clazzName;
    }

    public void setClazzName(String clazzName) {
        this.clazzName = clazzName;
    }

    public List<ColumnInfo> getColumnInfoList() {
        return columnInfoList;
    }

    public void setColumnInfoList(List<ColumnInfo> columnInfoList) {
        this.columnInfoList = columnInfoList;
        for (ColumnInfo columnInfo : columnInfoList) {
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
            GenerateValueHandler generateValueHandler = columnInfo.getGenerateValueHandler();
            if (id != null || generateValueHandler != null) {
                generateValueColumnInfoList.add(columnInfo);
            }

            ColumnRelationInfo columnRelationInfo = columnInfo.getColumnRelationInfo();
            if (columnRelationInfo != null) {
                relationColumnInfoList.add(columnInfo);
            }

            // 1、字段不存在关联实体为表字段
            // 2、存在关联实体并且是关系维护方才是表字段【多对多关联字段在中间表，所以实体中是不存在表字段的】
            if (columnRelationInfo == null) {
                tableColumnInfoList.add(columnInfo);
            } else {
                ManyToMany manyToMany = columnRelationInfo.getManyToMany();
                String mappedBy = columnRelationInfo.getMappedBy();
                if (manyToMany == null && StringUtils.isBlank(mappedBy)) {
                    tableColumnInfoList.add(columnInfo);
                }
            }
            columnInfoMap.put(columnInfo.getJavaColumnName(), columnInfo);
        }
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

    public ColumnInfo getDbColumnInfo(String dbColumnName) {
        String javaColumnName = this.dbColumnInfoMap.get(dbColumnName);
        return this.getColumnInfo(javaColumnName);
    }

    public List<ColumnInfo> getIdColumnInfoList() {
        return idColumnInfoList;
    }

    public void setIdColumnInfoList(List<ColumnInfo> idColumnInfoList) {
        this.idColumnInfoList = idColumnInfoList;
    }

    public List<ColumnInfo> getGenerateValueColumnInfoList() {
        return generateValueColumnInfoList;
    }

    public void setGenerateValueColumnInfoList(List<ColumnInfo> generateValueColumnInfoList) {
        this.generateValueColumnInfoList = generateValueColumnInfoList;
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

    public List<ColumnInfo> getRelationColumnInfoList() {
        return relationColumnInfoList;
    }

    public void setRelationColumnInfoList(List<ColumnInfo> relationColumnInfoList) {
        this.relationColumnInfoList = relationColumnInfoList;
    }
}
