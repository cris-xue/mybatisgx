package com.lc.mybatisx.model;

import com.lc.mybatisx.annotation.JoinTable;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ：薛承城
 * @description：实体关系查询，只支持左链接
 * @date ：2021/7/9 17:14
 */
public class EntityRelationSelectInfo {

    /**
     * 节点唯一标识，只有在不使用嵌套模式字段时才使用
     */
    private String id;
    /**
     * 关联查询结果映射id
     */
    private String resultMapId;
    /**
     * 关联字段的信息，比如user关联role。当前类是role，该字段就表示user中的userList
     */
    private ColumnInfo columnInfo;
    /**
     * 关联字段对应的实体信息。
     */
    private EntityInfo entityInfo;
    /**
     * 是否存在中间表
     */
    private Boolean isExistMiddleTable = false;
    /**
     * 主外键映射
     */
    private Map<String, List<ForeignKeyColumnInfo>> foreignKeyColumnInfoMap = new HashMap();
    /**
     * 直接关联查询
     */
    private List<EntityRelationSelectInfo> entityRelationSelectInfoList = new ArrayList();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getResultMapId() {
        return resultMapId;
    }

    public void setResultMapId(String resultMapId) {
        this.resultMapId = resultMapId;
    }

    public ColumnInfo getColumnInfo() {
        return columnInfo;
    }

    public void setColumnInfo(ColumnInfo columnInfo) {
        this.columnInfo = columnInfo;
    }

    public EntityInfo getEntityInfo() {
        return entityInfo;
    }

    public void setEntityInfo(EntityInfo entityInfo) {
        this.entityInfo = entityInfo;
    }

    public Boolean getExistMiddleTable() {
        return isExistMiddleTable;
    }

    public void setExistMiddleTable(Boolean existMiddleTable) {
        isExistMiddleTable = existMiddleTable;
    }

    /*public List<ForeignKeyColumnInfo> getForeignKeyColumnInfoList(String entityTableName) {
        return foreignKeyColumnInfoMap.get(entityTableName);
    }*/

    /*public void addForeignKeyColumnInfo(String entityTableName, List<ForeignKeyColumnInfo> foreignKeyColumnInfoList) {
        this.foreignKeyColumnInfoMap.put(entityTableName, foreignKeyColumnInfoList);
    }*/

    public List<EntityRelationSelectInfo> getEntityRelationSelectInfoList() {
        return entityRelationSelectInfoList;
    }

    public void setEntityRelationSelectInfoList(List<EntityRelationSelectInfo> entityRelationSelectInfoList) {
        this.entityRelationSelectInfoList = entityRelationSelectInfoList;
    }

    public void addEntityRelationSelectInfo(EntityRelationSelectInfo entityRelationSelectInfo) {
        this.entityRelationSelectInfoList.add(entityRelationSelectInfo);
    }

    public ColumnInfo getColumnInfo(String javaColumnName) {
        return this.entityInfo.getColumnInfo(javaColumnName);
    }

    public Class<?> getEntityClazz() {
        return this.entityInfo.getClazz();
    }

    public String getEntityClazzName() {
        return this.entityInfo.getClazzName();
    }

    public List<ColumnInfo> getTableColumnInfoList() {
        return this.entityInfo.getTableColumnInfoList();
    }

    public String getEntityTableName() {
        return this.entityInfo.getTableName();
    }

    public String getMiddleTableName() {
        ColumnRelationInfo columnRelationInfo = this.columnInfo.getColumnRelationInfo();
        String mappedBy = columnRelationInfo.getMappedBy();
        if (StringUtils.isBlank(mappedBy)) {
            JoinTable joinTable = columnRelationInfo.getJoinTable();
            return joinTable.name();
        } else {
            JoinTable joinTable = this.entityInfo.getColumnInfo(mappedBy).getColumnRelationInfo().getJoinTable();
            return joinTable.name();
        }
    }
}
