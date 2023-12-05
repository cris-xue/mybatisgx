package com.lc.mybatisx.model;

import java.util.ArrayList;
import java.util.List;

public class MethodNameInfo {

    private String action;

    private List<MethodNameWhereInfo> methodNameWhereInfoList = new ArrayList<>();

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public List<MethodNameWhereInfo> getMethodNameWhereInfoList() {
        return methodNameWhereInfoList;
    }

    public void setMethodNameWhereInfoList(List<MethodNameWhereInfo> methodNameWhereInfoList) {
        this.methodNameWhereInfoList = methodNameWhereInfoList;
    }

    public void addMethodNameWhereInfo(MethodNameWhereInfo methodNameWhereInfo) {
        methodNameWhereInfoList.add(methodNameWhereInfo);
    }

}
