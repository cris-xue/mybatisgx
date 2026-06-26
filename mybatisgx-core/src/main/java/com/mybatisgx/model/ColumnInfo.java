package com.mybatisgx.model;

import com.mybatisgx.annotation.*;
import com.mybatisgx.spi.ValueProcessor;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 字段信息信息
 *
 * @author ccxuef
 * @date 2025/8/9 15:54
 */
public class ColumnInfo {

    /**
     * 父字段信息
     */
    private ColumnInfo parent;
    /**
     * java字段信息
     */
    private Field field;
    /**
     * java字段信息
     */
    private FieldInfo fieldInfo;
    /**
     * 访问器
     */
    private LambdaAccessor lambdaAccessor;
    /**
     * 类型类别
     */
    private TypeCategory typeCategory;
    /**
     * Map、基础类型、业务类型
     */
    private Class<?> javaType;
    /**
     *
     */
    private String javaTypeName;
    /**
     *
     */
    private String javaColumnName;
    /**
     * java字段名访问路径
     */
    private List<String> javaColumnNamePathList;
    /**
     * java字段访问链
     */
    private List<ColumnInfo> javaColumnChain;
    /**
     * 容器类型，List、Set
     */
    private Class<?> collectionType;
    /**
     * 容器类型名
     */
    private String collectionTypeName;
    /**
     * 数据库类型名
     */
    private String dbTypeName;
    /**
     * 数据库列名
     */
    private String dbColumnName;
    /**
     * 数据库列名别名，用于查询
     */
    private String dbColumnNameAlias;
    /**
     * 类型处理器
     */
    private String typeHandler;
    /**
     * 复合字段列表
     */
    private List<ColumnInfo> composites;
    /**
     * java字段映射字段信息，userName={userName=1}
     */
    private Map<String, ColumnInfo> columnInfoMap = new LinkedHashMap();
    /**
     * 值处理列表，数组中的顺序就是执行值处理的顺序
     */
    private ValueProcessor[] valueProcessors;
    /**
     * 数据库字段注解
     */
    private Column column;
    /**
     * 实体字段注解
     */
    private Property property;
    /**
     * 非持久化字段
     */
    private Transient nonPersistent;
    /**
     * 查询字段注解
     */
    private QueryColumn queryColumn;
    /**
     * 是否是乐观锁字段
     */
    private Version version;
    /**
     * 是否是逻辑删除字段
     */
    private LogicDelete logicDelete;
    /**
     * 生成值注解
     */
    private GeneratedValue generatedValue;

    public ColumnInfo() {
        this.fieldInfo = new FieldInfo(this);
    }

    public ColumnInfo getParent() {
        return parent;
    }

    public void setParent(ColumnInfo parent) {
        this.parent = parent;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public FieldInfo getFieldInfo() {
        return fieldInfo;
    }

    public void setFieldInfo(FieldInfo fieldInfo) {
        this.fieldInfo = fieldInfo;
    }

    public LambdaAccessor getLambdaAccessor() {
        return lambdaAccessor;
    }

    public void setLambdaAccessor(LambdaAccessor lambdaAccessor) {
        this.lambdaAccessor = lambdaAccessor;
    }

    public TypeCategory getTypeCategory() {
        return typeCategory;
    }

    public void setTypeCategory(TypeCategory typeCategory) {
        this.typeCategory = typeCategory;
    }

    public Class<?> getJavaType() {
        return javaType;
    }

    public void setJavaType(Class<?> javaType) {
        this.javaType = javaType;
        if (javaType != null) {
            this.javaTypeName = javaType.getTypeName();
        }
    }

    public String getJavaTypeName() {
        return javaTypeName;
    }

    public void setJavaTypeName(String javaTypeName) {
        this.javaTypeName = javaTypeName;
    }

    public String getJavaColumnName() {
        return javaColumnName;
    }

    public void setJavaColumnName(String javaColumnName) {
        this.javaColumnName = javaColumnName;
    }

    public List<String> getJavaColumnNamePathList() {
        return javaColumnNamePathList;
    }

    public void setJavaColumnNamePathList(List<String> javaColumnNamePathList) {
        this.javaColumnNamePathList = javaColumnNamePathList;
    }

    public List<ColumnInfo> getJavaColumnChain() {
        return javaColumnChain;
    }

    public void setJavaColumnChain(List<ColumnInfo> javaColumnChain) {
        this.javaColumnChain = javaColumnChain;
    }

    public Class<?> getCollectionType() {
        return collectionType;
    }

    public void setCollectionType(Class<?> collectionType) {
        this.collectionType = collectionType;
        if (collectionType != null) {
            this.collectionTypeName = collectionType.getName();
        }
    }

    public String getCollectionTypeName() {
        return collectionTypeName;
    }

    public void setCollectionTypeName(String collectionTypeName) {
        this.collectionTypeName = collectionTypeName;
    }

    public String getDbTypeName() {
        return dbTypeName;
    }

    public void setDbTypeName(String dbTypeName) {
        this.dbTypeName = dbTypeName;
    }

    public String getDbColumnName() {
        return dbColumnName;
    }

    public void setDbColumnName(String dbColumnName) {
        this.dbColumnName = dbColumnName;
    }

    public String getDbColumnNameAlias() {
        return dbColumnNameAlias;
    }

    public String getTableColumnNameAlias(ColumnEntityRelation columnEntityRelation) {
        return String.format("%s_%s", columnEntityRelation.getTableNameAlias(), dbColumnNameAlias);
    }

    public void setDbColumnNameAlias(String dbColumnNameAlias) {
        this.dbColumnNameAlias = dbColumnNameAlias;
    }

    public String getTypeHandler() {
        return typeHandler;
    }

    public void setTypeHandler(String typeHandler) {
        this.typeHandler = typeHandler;
    }

    public List<ColumnInfo> getComposites() {
        return composites;
    }

    public void setComposites(List<ColumnInfo> composites) {
        this.composites = composites;
    }

    public ColumnInfo getColumnInfo(String javaColumnName) {
        return this.columnInfoMap.get(javaColumnName);
    }

    public void addColumnInfo(ColumnInfo columnInfo) {
        this.columnInfoMap.put(columnInfo.getJavaColumnName(), columnInfo);
    }

    public ValueProcessor[] getValueProcessors() {
        return valueProcessors;
    }

    public void setValueProcessors(ValueProcessor[] valueProcessors) {
        this.valueProcessors = valueProcessors;
    }

    public Column getColumn() {
        return column;
    }

    public void setColumn(Column column) {
        this.column = column;
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    public Transient getNonPersistent() {
        return nonPersistent;
    }

    public void setNonPersistent(Transient nonPersistent) {
        this.nonPersistent = nonPersistent;
    }

    public QueryColumn getQueryColumn() {
        return queryColumn;
    }

    public void setQueryColumn(QueryColumn queryColumn) {
        this.queryColumn = queryColumn;
    }

    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    public LogicDelete getLogicDelete() {
        return logicDelete;
    }

    public void setLogicDelete(LogicDelete logicDelete) {
        this.logicDelete = logicDelete;
    }

    public GeneratedValue getGenerateValue() {
        return generatedValue;
    }

    public void setGenerateValue(GeneratedValue generatedValue) {
        this.generatedValue = generatedValue;
    }

    public static class Builder {

        private ColumnInfo columnInfo = new ColumnInfo();

        public Builder columnInfo(ColumnInfo columnInfo) {
            this.columnInfo.setJavaTypeName(columnInfo.getJavaTypeName());
            this.columnInfo.setDbColumnName(columnInfo.getDbColumnName());
            this.columnInfo.setDbColumnNameAlias(columnInfo.getDbColumnNameAlias());
            return this;
        }

        public Builder javaColumnName(String javaColumnName) {
            this.columnInfo.setJavaColumnName(javaColumnName);
            return this;
        }

        public ColumnInfo build() {
            return columnInfo;
        }
    }
}
