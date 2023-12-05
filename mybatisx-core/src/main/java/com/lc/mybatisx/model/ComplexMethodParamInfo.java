package com.lc.mybatisx.model;

import java.util.List;

/**
 * @author ：薛承城
 * @description：复杂参数【User、Role】
 * @date ：2021/7/9 17:13
 */
public class ComplexMethodParamInfo {

    /**
     * 类型，User
     */
    private Class<?> type;
    /**
     * 类型名称：java.lang.Object
     */
    private String typeName;
    /**
     * 参数名，userName
     */
    private List<String> paramNameList;

    private List<ColumnInfo> columnInfoList;

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

    public List<String> getParamNameList() {
        return paramNameList;
    }

    public void setParamNameList(List<String> paramNameList) {
        this.paramNameList = paramNameList;
    }
}
