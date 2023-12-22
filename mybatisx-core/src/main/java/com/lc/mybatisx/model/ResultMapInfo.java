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

    private String id;

    private String type;

    private List<ColumnInfo> columnInfoList;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<ColumnInfo> getColumnInfoList() {
        return columnInfoList;
    }

    public void setColumnInfoList(List<ColumnInfo> columnInfoList) {
        this.columnInfoList = columnInfoList;
    }
}
