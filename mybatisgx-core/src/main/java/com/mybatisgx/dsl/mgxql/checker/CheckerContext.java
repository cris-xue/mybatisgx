package com.mybatisgx.dsl.mgxql.checker;

import com.mybatisgx.model.EntityInfo;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * MGXQL校验上下文，持有实体元数据并收集校验错误
 *
 * @author 薛承城
 * @date 2025/11/17 10:19
 */
public class CheckerContext {

    /**
     * 主实体信息
     */
    private EntityInfo primaryEntityInfo;

    /**
     * 别名 → EntityInfo 映射
     */
    private Map<String, EntityInfo> aliasEntityInfoMap = new LinkedHashMap<>();

    /**
     * 实体类名 → EntityInfo 映射（用于通过实体名查找）
     */
    private Map<String, EntityInfo> entityNameInfoMap = new LinkedHashMap<>();

    /**
     * 收集的错误信息
     */
    private List<String> errors = new ArrayList<>();

    public CheckerContext(EntityInfo primaryEntityInfo) {
        this.primaryEntityInfo = primaryEntityInfo;
    }

    public EntityInfo getPrimaryEntityInfo() {
        return primaryEntityInfo;
    }

    public void setPrimaryEntityInfo(EntityInfo primaryEntityInfo) {
        this.primaryEntityInfo = primaryEntityInfo;
    }

    public Map<String, EntityInfo> getAliasEntityInfoMap() {
        return aliasEntityInfoMap;
    }

    public void registerAlias(String alias, EntityInfo entityInfo) {
        this.aliasEntityInfoMap.put(alias, entityInfo);
    }

    public EntityInfo getEntityInfoByAlias(String alias) {
        return this.aliasEntityInfoMap.get(alias);
    }

    public Map<String, EntityInfo> getEntityNameInfoMap() {
        return entityNameInfoMap;
    }

    public void registerEntityName(String entityName, EntityInfo entityInfo) {
        this.entityNameInfoMap.put(entityName, entityInfo);
    }

    public EntityInfo getEntityInfoByName(String entityName) {
        return this.entityNameInfoMap.get(entityName);
    }

    public List<String> getErrors() {
        return errors;
    }

    public void addError(String error) {
        this.errors.add(error);
    }

    public boolean hasErrors() {
        return !this.errors.isEmpty();
    }
}
