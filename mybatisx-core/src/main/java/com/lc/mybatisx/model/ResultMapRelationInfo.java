package com.lc.mybatisx.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ：薛承城
 * @description：ResultMap关联信息
 * @date ：2021/7/9 17:14
 */
public class ResultMapRelationInfo {

    /**
     * 查询方法名
     */
    private String select;
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
    private List<ResultMapRelationInfo> resultMapAssociationInfoList = new ArrayList();

    public String getSelect() {
        return select;
    }

    public void setSelect(String select) {
        this.select = select;
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

    public List<ResultMapRelationInfo> getResultMapAssociationInfoList() {
        return resultMapAssociationInfoList;
    }

    public void setResultMapAssociationInfoList(List<ResultMapRelationInfo> resultMapAssociationInfoList) {
        this.resultMapAssociationInfoList = resultMapAssociationInfoList;
    }
}
