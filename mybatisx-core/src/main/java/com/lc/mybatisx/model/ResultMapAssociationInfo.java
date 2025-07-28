package com.lc.mybatisx.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ：薛承城
 * @description：关联表信息，多对多中间关联表没有太大的以意义，完全是为了关系型数据库考虑。但有时候需要处理关联问题的开关，又有一定的作用，所以需要当作正常表来处理
 * @date ：2021/7/9 17:14
 */
public class ResultMapAssociationInfo {

    /**
     * 查询方法名
     */
    private String select;
    /**
     * 节点唯一标识，只有在不使用嵌套模式字段时才使用
     */
    private String id;
    /**
     * 映射到一个外部的标签中，然后通过 id 进行引入
     */
    private String resultMapId;
    /**
     * 关联结果集的层级
     */
    private Integer level;
    /**
     * 类型
     */
    private Class<?> type;
    /**
     * 类型名
     */
    private String typeName;
    /**
     * 容器类型
     */
    private Class<?> ofType;
    /**
     * 容器类型名
     */
    private String ofTypeName;
    /**
     * 关联字段对应的字段信息
     */
    private ColumnInfo columnInfo;
    /**
     * 字段信息列表
     */
    private List<ColumnInfo> columnInfoList;
    /**
     * 关联信息
     */
    private List<ResultMapAssociationInfo> resultMapAssociationInfoList = new ArrayList();

    public String getSelect() {
        return select;
    }

    public void setSelect(String select) {
        this.select = select;
    }

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

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
        this.typeName = type.getTypeName();
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public Class<?> getOfType() {
        return ofType;
    }

    public void setOfType(Class<?> ofType) {
        this.ofType = ofType;
    }

    public String getOfTypeName() {
        return ofTypeName;
    }

    public void setOfTypeName(String ofTypeName) {
        this.ofTypeName = ofTypeName;
    }

    public ColumnInfo getColumnInfo() {
        return columnInfo;
    }

    public void setColumnInfo(ColumnInfo columnInfo) {
        this.columnInfo = columnInfo;
    }

    public List<ColumnInfo> getColumnInfoList() {
        return columnInfoList;
    }

    public void setColumnInfoList(List<ColumnInfo> columnInfoList) {
        this.columnInfoList = columnInfoList;
    }

    public List<ResultMapAssociationInfo> getResultMapAssociationInfoList() {
        return resultMapAssociationInfoList;
    }

    public void setResultMapAssociationInfoList(List<ResultMapAssociationInfo> resultMapAssociationInfoList) {
        this.resultMapAssociationInfoList = resultMapAssociationInfoList;
    }

    public void addResultMapAssociationInfo(ResultMapAssociationInfo resultMapAssociationInfo) {
        this.resultMapAssociationInfoList.add(resultMapAssociationInfo);
    }
}
