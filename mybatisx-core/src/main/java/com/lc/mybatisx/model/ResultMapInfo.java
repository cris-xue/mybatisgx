package com.lc.mybatisx.model;

import java.util.List;

/**
 * https://blog.csdn.net/wangruoao/article/details/83147374/
 * https://pythonjishu.com/ywljunyfdqtftcc/
 * https://blog.csdn.net/WG102753/article/details/107989681
 * <code>
 *     <resultMap id="blogResult" type="Blog">
 *     <id property="id" column="id" />
 *     <result property="title" column="title"/>
 *     <association property="author" javaType="Author" resultSet="authors" column="author_id" foreignColumn="id">
 *         <id property="id" column="id"/>
 *         <result property="username" column="username"/>
 *         <result property="password" column="password"/>
 *         <result property="email" column="email"/>
 *         <result property="bio" column="bio"/>
 *     </association>
 * </resultMap>
 * </code>
 * @author ccxuef
 * @date 2025/8/9 18:58
 */
public class ResultMapInfo extends ColumnEntityRelation {

    /**
     * 查询方法对应的结果集id，为结果集子节点时为空
     */
    private String id;
    /**
     * 结果集子查询节点依赖的方法名，为根节点时为空
     */
    private String select;
    /**
     * 内嵌查询id，如果存在表示采用内嵌查询的方式，对应的xml节点为association、collection
     */
    private String nestedSelectId;
    /**
     * resultMap关系信息列表，如果存在表示采用的select join的方式，对应的xml节点为association、collection
     */
    private List<ResultMapInfo> resultMapInfoList;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSelect() {
        return select;
    }

    public void setSelect(String select) {
        this.select = select;
    }

    public String getNestedSelectId() {
        return nestedSelectId;
    }

    public void setNestedSelectId(String nestedSelectId) {
        this.nestedSelectId = nestedSelectId;
    }

    public List<ResultMapInfo> getResultMapInfoList() {
        return resultMapInfoList;
    }

    public void setResultMapInfoList(List<ResultMapInfo> resultMapInfoList) {
        this.resultMapInfoList = resultMapInfoList;
    }

    public ColumnInfo getColumnInfo(String javaColumnName) {
        return this.entityInfo.getColumnInfo(javaColumnName);
    }

    public String getEntityClazzName() {
        return this.entityInfo.getClazzName();
    }

    public List<ColumnInfo> getTableColumnInfoList() {
        return this.entityInfo.getTableColumnInfoList();
    }
}
