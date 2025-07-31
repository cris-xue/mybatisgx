package com.lc.mybatisx.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ：薛承城
 * @description：ResultMap关联信息
 * @code: <code>
 * <resultMap id="BaseResultMap" type="com.lc.mybatisx.model.User">
 * <id column="id" property="id" />
 * <result column="username" property="username" />
 * <result column="password" property="password" />
 * <result column="email" property="email" />
 * <result column="phone" property="phone" />
 * <result column="create_time" property="createTime" />
 * <result column="update_time" property="updateTime" />
 * <result column="is_delete" property="isDelete" />
 * <association property="userInfo" javaType="com.lc.mybatisx.model.UserInfo">
 * <id column="id" property="id" />
 * <result column="username" property="username" />
 * </association>
 * <association property="userInfo" select=""></association>
 * </resultMap>
 * </code>
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
    private List<ResultMapRelationInfo> resultMapRelationInfoList = new ArrayList();

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

    public List<ResultMapRelationInfo> getResultMapRelationInfoList() {
        return resultMapRelationInfoList;
    }

    public void setResultMapRelationInfoList(List<ResultMapRelationInfo> resultMapRelationInfoList) {
        this.resultMapRelationInfoList = resultMapRelationInfoList;
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
}
