package com.lc.mybatisx.model;

import java.util.ArrayList;
import java.util.List;

public class MethodNameQueryInfo {

    private String action;
    private List<String> javaColumnNameList = new ArrayList<>();

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public List<String> getJavaColumnNameList() {
        return javaColumnNameList;
    }

    public void setJavaColumnNameList(List<String> javaColumnNameList) {
        this.javaColumnNameList = javaColumnNameList;
    }

    public void addJavaColumnName(String javaColumnName) {
        javaColumnNameList.add(javaColumnName);
    }

}
