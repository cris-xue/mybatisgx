package com.lc.mybatisx.model;

/**
 * @author ：薛承城
 * @description：基础类型参数【int、boolean、long、Integer、String、Long】
 * @date ：2021/7/9 17:13
 */
public class BasicMethodParamInfo {

    /**
     * 类型，Integer、User
     */
    private Class<?> type;
    /**
     * 类型名称：java.lang.Long、java.lang.Object
     */
    private String typeName;
    /**
     * 参数名，userName
     */
    private String paramName;

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

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }
}
