package com.lc.mybatisx.model;

import java.util.List;

/**
 * https://blog.csdn.net/wangruoao/article/details/83147374/
 * https://pythonjishu.com/ywljunyfdqtftcc/
 * https://blog.csdn.net/WG102753/article/details/107989681
 * <resultMap id="blogResult" type="Blog">
 * <id property="id" column="id" />
 * <result property="title" column="title"/>
 * <association property="author" javaType="Author" resultSet="authors" column="author_id" foreignColumn="id">
 * <id property="id" column="id"/>
 * <result property="username" column="username"/>
 * <result property="password" column="password"/>
 * <result property="email" column="email"/>
 * <result property="bio" column="bio"/>
 * </association>
 * </resultMap>
 */
public class ResultMapInfo {

    /**
     * resultMap id
     */
    private String id;
    /**
     * 实体信息
     */
    private EntityInfo entityInfo;
    /**
     * resultMap关系信息列表
     */
    private List<ResultMapRelationInfo> resultMapRelationInfoList;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ColumnInfo getColumnInfo(String javaColumnName) {
        return this.entityInfo.getColumnInfo(javaColumnName);
    }

    public String getEntityClazzName() {
        return this.entityInfo.getClazzName();
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
}
