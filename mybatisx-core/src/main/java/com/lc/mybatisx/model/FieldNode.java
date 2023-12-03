package com.lc.mybatisx.model;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2021/7/9 17:39
 */
// ColumnInfo
public class FieldNode {

    private String name;

    private Class<?> type;

    private String typeName;

    private String dbFieldName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getDbFieldName() {
        return dbFieldName;
    }

    public void setDbFieldName(String dbFieldName) {
        this.dbFieldName = dbFieldName;
    }
}
