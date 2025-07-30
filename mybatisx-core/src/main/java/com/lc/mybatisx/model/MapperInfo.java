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
     * id类型【SimpleDao的id类型，不能和实体中的id合并，在启动阶段需要使用两者进行对比，如果不一致表示写错了，需要提示】
     */
    private Class<?> idClass;
    /**
     * 实体类型【SimpleDao的entity类型，不能和实体中的entity合并，在启动阶段需要使用两者进行对比，如果不一致表示写错了，需要提示】
     */
    private Class<?> entityClass;
    /**
     * 命名空间
     */
    private String namespace;
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
    private Map<Class<?>, ResultMapInfo> resultMapInfoMap = new LinkedHashMap();
    /**
     * 实体关联查询方法
     */
    private Map<String, EntityRelationSelectInfo> entityRelationSelectInfoMap = new LinkedHashMap();

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
        return this.entityInfo.getTableName();
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
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
        this.entityRelationInfoMap.put(entityInfo.getClazz(), entityRelationInfo);
    }

    public List<ResultMapInfo> getResultMapInfoList() {
        return new ArrayList<>(resultMapInfoMap.values());
    }

    public ResultMapInfo getResultMapInfo(Class<?> clazz) {
        return resultMapInfoMap.get(clazz);
    }

    public void addResultMapInfo(ResultMapInfo resultMapInfo) {
        this.resultMapInfoMap.put(resultMapInfo.getEntityInfo().getClazz(), resultMapInfo);
    }

    public void addResultMapInfoList(List<ResultMapInfo> resultMapInfoList) {
        resultMapInfoList.forEach(resultMapInfo -> this.addResultMapInfo(resultMapInfo));
    }

    public Map<String, EntityRelationSelectInfo> getEntityRelationSelectInfoMap() {
        return entityRelationSelectInfoMap;
    }

    public List<EntityRelationSelectInfo> getEntityRelationSelectInfoList() {
        return new ArrayList(entityRelationSelectInfoMap.values());
    }

    public void setEntityRelationSelectInfoList(List<EntityRelationSelectInfo> entityRelationSelectInfoList) {
        entityRelationSelectInfoList.forEach(entityRelationSelectInfo ->
                this.entityRelationSelectInfoMap.put(entityRelationSelectInfo.getId(), entityRelationSelectInfo)
        );
    }
}
