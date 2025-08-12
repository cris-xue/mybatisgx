package com.lc.mybatisx.model;

import com.lc.mybatisx.annotation.JoinTable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ：薛承城
 * @description：实体关系查询，只支持左链接
 * @date ：2021/7/9 17:14
 */
public class EntityRelationSelectInfo extends ColumnEntityRelation {

    /**
     * 节点唯一标识，只有在不使用嵌套模式字段时才使用
     */
    private String id;
    /**
     * 关联查询结果映射id
     */
    private String resultMapId;
    /**
     * 是否存在中间表
     */
    private Boolean isExistMiddleTable = false;
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

    public Boolean getExistMiddleTable() {
        return isExistMiddleTable;
    }

    public void setExistMiddleTable(Boolean existMiddleTable) {
        isExistMiddleTable = existMiddleTable;
    }

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

    public String getEntityTableName() {
        return this.entityInfo.getTableName();
    }

    public String getMiddleTableName() {
        ColumnRelationInfo columnRelationInfo = this.columnInfo.getColumnRelationInfo();
        JoinTable joinTable;
        if (this.getMappedBy()) {
            String mappedBy = columnRelationInfo.getMappedBy();
            joinTable = this.entityInfo.getColumnInfo(mappedBy).getColumnRelationInfo().getJoinTable();
        } else {
            joinTable = columnRelationInfo.getJoinTable();
        }
        return joinTable.name();
    }
}
