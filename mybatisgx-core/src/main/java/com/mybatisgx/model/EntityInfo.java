package com.mybatisgx.model;

import java.lang.reflect.Type;
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
    private Map<String, String> tableColumnInfoMap = new LinkedHashMap();
    /**
     * id字段
     */
    private IdColumnInfo idColumnInfo;
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
     * 逻辑删除id字段
     */
    private ColumnInfo logicDeleteIdColumnInfo;
    /**
     * 乐观锁
     */
    private ColumnInfo versionColumnInfo;
    /**
     * 关系字段信息
     */
    private List<RelationColumnInfo> relationColumnInfoList = new ArrayList<>();
    /**
     * 实体类泛型参数类型映射
     */
    private Map<Type, Class<?>> typeParameterMap;

    public String getTableName() {
        return tableName;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public String getClazzName() {
        return clazzName;
    }

    public List<ColumnInfo> getColumnInfoList() {
        return columnInfoList;
    }

    public ColumnInfo getColumnInfo(String javaColumnName) {
        return this.columnInfoMap.get(javaColumnName);
    }

    public void addColumnMap(String javaColumnName, ColumnInfo columnInfo) {
        this.columnInfoMap.put(javaColumnName, columnInfo);
    }

    public ColumnInfo getDbColumnInfo(String dbColumnName) {
        String javaColumnName = this.tableColumnInfoMap.get(dbColumnName);
        return this.getColumnInfo(javaColumnName);
    }

    public void addTableColumnMap(String tableColumnName, String javaColumnName) {
        this.tableColumnInfoMap.put(tableColumnName, javaColumnName);
    }

    public IdColumnInfo getIdColumnInfo() {
        return idColumnInfo;
    }

    public void setIdColumnInfo(IdColumnInfo idColumnInfo) {
        this.idColumnInfo = idColumnInfo;
    }

    public List<ColumnInfo> getGenerateValueColumnInfoList() {
        return generateValueColumnInfoList;
    }

    public void addGenerateValueColumnInfo(ColumnInfo columnInfo) {
        this.generateValueColumnInfoList.add(columnInfo);
    }

    public List<ColumnInfo> getTableColumnInfoList() {
        return tableColumnInfoList;
    }

    public void addTableColumnInfo(ColumnInfo columnInfo) {
        this.tableColumnInfoList.add(columnInfo);
    }

    public ColumnInfo getLogicDeleteColumnInfo() {
        return logicDeleteColumnInfo;
    }

    public void setLogicDeleteColumnInfo(ColumnInfo logicDeleteColumnInfo) {
        this.logicDeleteColumnInfo = logicDeleteColumnInfo;
    }

    public ColumnInfo getLogicDeleteIdColumnInfo() {
        return logicDeleteIdColumnInfo;
    }

    public void setLogicDeleteIdColumnInfo(ColumnInfo logicDeleteIdColumnInfo) {
        this.logicDeleteIdColumnInfo = logicDeleteIdColumnInfo;
    }

    public ColumnInfo getVersionColumnInfo() {
        return versionColumnInfo;
    }

    public void setVersionColumnInfo(ColumnInfo versionColumnInfo) {
        this.versionColumnInfo = versionColumnInfo;
    }

    public List<RelationColumnInfo> getRelationColumnInfoList() {
        return relationColumnInfoList;
    }

    public Map<Type, Class<?>> getTypeParameterMap() {
        return typeParameterMap;
    }

    public static class Builder {

        private EntityInfo entityInfo;

        public Builder() {
            this.entityInfo = new EntityInfo();
        }

        public Builder setTableName(String tableName) {
            this.entityInfo.tableName = tableName;
            return this;
        }

        public Builder setClazz(Class<?> clazz) {
            this.entityInfo.clazz = clazz;
            this.entityInfo.clazzName = clazz.getName();
            return this;
        }

        public Builder setColumnInfoList(List<ColumnInfo> columnInfoList) {
            this.entityInfo.columnInfoList = columnInfoList;
            return this;
        }

        public Builder setTypeParameterMap(Map<Type, Class<?>> typeParameterMap) {
            this.entityInfo.typeParameterMap = typeParameterMap;
            return this;
        }

        public EntityInfo build() {
            return entityInfo;
        }
    }
}
