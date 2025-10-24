package com.lc.mybatisx.model;

/**
 * https://blog.csdn.net/wangruoao/article/details/83147374/
 * https://pythonjishu.com/ywljunyfdqtftcc/
 * https://blog.csdn.net/WG102753/article/details/107989681
 * <code>
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
 * </code>
 *
 * @author ccxuef
 * @date 2025/8/9 18:58
 */
public class ResultMapInfo extends ColumnEntityRelation<ResultMapInfo> {

    /**
     * 查询方法对应的结果集id，为结果集子节点时为空
     */
    private String id;
    /**
     * 内嵌查询，内嵌查询id，对应的xml节点为association、collection
     */
    private NestedSelect nestedSelect;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNestedSelectId() {
        return nestedSelect.getId();
    }

    public NestedSelect getNestedSelect() {
        return nestedSelect;
    }

    public void setNestedSelect(NestedSelect nestedSelect) {
        this.nestedSelect = nestedSelect;
    }

    public String getEntityTableName() {
        return this.entityInfo.getTableName();
    }

    public String getEntityTableNameAlias() {
        return this.entityInfo.getTableNameAlias();
    }

    public String getMiddleTableName() {
        return this.getMiddleEntityInfo().getTableName();
    }

    public ColumnInfo getColumnInfo(String javaColumnName) {
        return this.entityInfo.getColumnInfo(javaColumnName);
    }

    public static class NestedSelect {

        private String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}
