package com.mybatisgx.model;

import com.mybatisgx.annotation.JoinTable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ：薛承城
 * @description：实体关系查询，只支持左链接
 * @date ：2021/7/9 17:14
 */
public class EntityRelationSelectInfo extends ColumnEntityRelation<EntityRelationSelectInfo> {

    /**
     * 节点唯一标识，只有在不使用嵌套模式字段时才使用
     */
    private String id;
    /**
     * 关联查询结果映射id
     */
    private String resultMapId;
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

    public List<EntityRelationSelectInfo> getEntityRelationSelectInfoList() {
        return entityRelationSelectInfoList;
    }

    public void setEntityRelationSelectInfoList(List<EntityRelationSelectInfo> entityRelationSelectInfoList) {
        this.entityRelationSelectInfoList = entityRelationSelectInfoList;
        super.setComposites(this.entityRelationSelectInfoList);
    }

    public void addEntityRelationSelectInfo(EntityRelationSelectInfo entityRelationSelectInfo) {
        this.entityRelationSelectInfoList.add(entityRelationSelectInfo);
        super.setComposites(this.entityRelationSelectInfoList);
    }

    public ColumnInfo getColumnInfo(String javaColumnName) {
        return this.entityInfo.getColumnInfo(javaColumnName);
    }

    public String getEntityTableName() {
        return this.entityInfo.getTableName();
    }

    public String getMiddleTableName() {
        RelationColumnInfo relationColumnInfo = (RelationColumnInfo) this.columnInfo;
        RelationColumnInfo mappedByRelationColumnInfo = relationColumnInfo.getMappedByRelationColumnInfo();
        JoinTable joinTable;
        if (mappedByRelationColumnInfo != null) {
            joinTable = mappedByRelationColumnInfo.getJoinTable();
        } else {
            joinTable = relationColumnInfo.getJoinTable();
        }
        return joinTable.name();
    }
}
