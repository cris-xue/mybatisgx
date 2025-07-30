package com.lc.mybatisx.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ：薛承城
 * @description：实体关系查询 <select id="" resultMap=""></select>
 * @date ：2021/7/9 17:14
 */
public class EntityRelationSelectInfo {

    /**
     * 节点唯一标识，只有在不使用嵌套模式字段时才使用
     */
    private String id;
    /**
     * 映射到一个外部的标签中，然后通过 id 进行引入
     */
    private String resultMapId;
    /**
     * 关联字段对应的字段信息
     */
    private ColumnInfo columnInfo;
    /**
     * 实体信息
     */
    private EntityInfo entityInfo;
    /**
     * 关联信息
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

    public List<EntityRelationSelectInfo> getEntityRelationSelectInfoList() {
        return entityRelationSelectInfoList;
    }

    public void setEntityRelationSelectInfoList(List<EntityRelationSelectInfo> entityRelationSelectInfoList) {
        this.entityRelationSelectInfoList = entityRelationSelectInfoList;
    }

    public void addEntityRelationSelectInfo(EntityRelationSelectInfo entityRelationSelectInfo) {
        this.entityRelationSelectInfoList.add(entityRelationSelectInfo);
    }
}
