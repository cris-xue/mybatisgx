package com.lc.mybatisx.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2021/7/9 17:13
 */
public class MapperInfo {

    /**
     * id类型
     */
    private Class<?> idClass;
    /**
     * 实体类型
     */
    private Class<?> entityClass;
    /**
     * 表名称
     */
    private String tableName;
    /**
     * 命名空间
     */
    private String namespace;
    /**
     * 表信息
     */
    @Deprecated
    private TableInfo tableInfo;
    /**
     * 实体信息
     */
    private EntityInfo entityInfo;
    /**
     * 方法信息列表
     */
    private List<MethodInfo> methodInfoList;
    /**
     * 实体关系信息，使用map是因为自定义方法可能使用非实体类
     */
    private Map<Class<?>, EntityRelationInfo> entityRelationInfoMap = new LinkedHashMap();
    /**
     * 结果集信息列表
     */
    @Deprecated
    private Map<Class<?>, ResultMapInfo> resultMapInfoMap = new LinkedHashMap();
    /**
     * 子查询结果集
     */
    private Map<Class<?>, ResultMapInfo> subResultMapInfoMap = new LinkedHashMap();
    /**
     * join查询结果集
     */
    private Map<Class<?>, ResultMapInfo> joinResultMapInfoMap = new LinkedHashMap();

    public Class<?> getIdClass() {
        return idClass;
    }

    public void setIdClass(Class<?> idClass) {
        this.idClass = idClass;
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(Class<?> entityClass) {
        this.entityClass = entityClass;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public TableInfo getTableInfo() {
        return tableInfo;
    }

    public void setTableInfo(TableInfo tableInfo) {
        this.tableInfo = tableInfo;
    }

    public EntityInfo getEntityInfo() {
        return entityInfo;
    }

    public void setEntityInfo(EntityInfo entityInfo) {
        this.entityInfo = entityInfo;
    }

    public List<MethodInfo> getMethodInfoList() {
        return methodInfoList;
    }

    public void setMethodInfoList(List<MethodInfo> methodInfoList) {
        this.methodInfoList = methodInfoList;
    }

    public List<EntityRelationInfo> getEntityRelationInfoList() {
        return new ArrayList<>(entityRelationInfoMap.values());
    }

    public EntityRelationInfo getEntityRelationInfo(Class<?> clazz) {
        return entityRelationInfoMap.get(clazz);
    }

    public void addEntityRelationInfo(EntityRelationInfo entityRelationInfo) {
        EntityInfo entityInfo = entityRelationInfo.getEntityInfo();
        this.entityRelationInfoMap.put(entityInfo.getTableEntityClass(), entityRelationInfo);
    }

    public List<ResultMapInfo> getResultMapInfoList() {
        return new ArrayList<>(resultMapInfoMap.values());
    }

    public ResultMapInfo getResultMapInfo(Class<?> clazz) {
        return resultMapInfoMap.get(clazz);
    }

    public void addResultMapInfo(ResultMapInfo resultMapInfo) {
        this.resultMapInfoMap.put(resultMapInfo.getType(), resultMapInfo);
    }

    public void addResultMapInfoList(List<ResultMapInfo> resultMapInfoList) {
        resultMapInfoList.forEach(resultMapInfo -> this.addResultMapInfo(resultMapInfo));
    }

    public Map<Class<?>, ResultMapInfo> getSubResultMapInfoMap() {
        return subResultMapInfoMap;
    }

    public void setSubResultMapInfoMap(Map<Class<?>, ResultMapInfo> subResultMapInfoMap) {
        this.subResultMapInfoMap = subResultMapInfoMap;
    }

    public Map<Class<?>, ResultMapInfo> getJoinResultMapInfoMap() {
        return joinResultMapInfoMap;
    }

    public void setJoinResultMapInfoMap(Map<Class<?>, ResultMapInfo> joinResultMapInfoMap) {
        this.joinResultMapInfoMap = joinResultMapInfoMap;
    }
}
