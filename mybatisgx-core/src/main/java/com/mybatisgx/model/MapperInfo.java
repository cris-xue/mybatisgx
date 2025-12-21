package com.mybatisgx.model;

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
     * 查询实体类型【SimpleDao的queryEntity类型，不能和实体中的queryEntity合并，在启动阶段需要使用两者进行对比，如果不一致表示写错了，需要提示】
     */
    private Class<?> queryEntityClass;
    /**
     * dao类型
     */
    private Class<?> daoClass;
    /**
     * 命名空间
     */
    private String namespace;
    /**
     * 实体信息
     */
    private EntityInfo entityInfo;
    /**
     * 查询实体信息
     */
    private EntityInfo queryEntityInfo;
    /**
     * 方法信息列表
     */
    private List<MethodInfo> methodInfoList;
    /**
     * 实体关系信息，使用map是因为自定义方法可能使用非实体类
     */
    private Map<Class<?>, EntityRelationTree> entityRelationTreeMap = new LinkedHashMap();
    /**
     * 结果集信息列表
     */
    private Map<String, ResultMapInfo> resultMapInfoMap = new LinkedHashMap();

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

    public Class<?> getQueryEntityClass() {
        return queryEntityClass;
    }

    public void setQueryEntityClass(Class<?> queryEntityClass) {
        this.queryEntityClass = queryEntityClass;
    }

    public Class<?> getDaoClass() {
        return daoClass;
    }

    public void setDaoClass(Class<?> daoClass) {
        this.daoClass = daoClass;
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

    public EntityInfo getQueryEntityInfo() {
        return queryEntityInfo;
    }

    public void setQueryEntityInfo(EntityInfo queryEntityInfo) {
        this.queryEntityInfo = queryEntityInfo;
    }

    public List<MethodInfo> getMethodInfoList() {
        return methodInfoList;
    }

    public void setMethodInfoList(List<MethodInfo> methodInfoList) {
        this.methodInfoList = methodInfoList;
    }

    public List<EntityRelationTree> getEntityRelationTreeList() {
        return new ArrayList<>(entityRelationTreeMap.values());
    }

    public EntityRelationTree getEntityRelationTree(Class<?> clazz) {
        return entityRelationTreeMap.get(clazz);
    }

    public void addEntityRelationTree(EntityRelationTree entityRelationTree) {
        EntityInfo entityInfo = entityRelationTree.getEntityInfo();
        this.entityRelationTreeMap.put(entityInfo.getClazz(), entityRelationTree);
    }

    public List<ResultMapInfo> getResultMapInfoList() {
        return new ArrayList<>(resultMapInfoMap.values());
    }

    public ResultMapInfo getResultMapInfo(String resultMapId) {
        return resultMapInfoMap.get(resultMapId);
    }

    public void setResultMapInfoList(List<ResultMapInfo> resultMapInfoList) {
        for (ResultMapInfo resultMapInfo : resultMapInfoList) {
            ResultMapInfo existResultMapInfo = this.resultMapInfoMap.get(resultMapInfo.getId());
            if (existResultMapInfo == null) {
                this.resultMapInfoMap.put(resultMapInfo.getId(), resultMapInfo);
            }
        }
    }
}
