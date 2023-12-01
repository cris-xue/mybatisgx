package com.lc.mybatisx.model;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2021/7/9 17:14
 */
public class MethodNode {

    private Method method;

    private String methodName;

    private ReturnNode returnNode;
    /**
     * 是否动态参数
     */
    private Boolean isDynamic;
    /**
     * 是否单参数
     */
    private Boolean isSingleParam;

    private MethodParamNode methodParamNode;

    private List<MethodParamNode> methodParamNodeList;

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public ReturnNode getReturnNode() {
        return returnNode;
    }

    public void setReturnNode(ReturnNode returnNode) {
        this.returnNode = returnNode;
    }

    public Boolean getDynamic() {
        return isDynamic;
    }

    public void setDynamic(Boolean dynamic) {
        isDynamic = dynamic;
    }

    public Boolean getSingleParam() {
        return isSingleParam;
    }

    public void setSingleParam(Boolean singleParam) {
        isSingleParam = singleParam;
    }

    public MethodParamNode getMethodParamNode() {
        return methodParamNode;
    }

    public void setMethodParamNode(MethodParamNode methodParamNode) {
        this.methodParamNode = methodParamNode;
    }

    public List<MethodParamNode> getMethodParamNodeList() {
        return methodParamNodeList;
    }

    public void setMethodParamNodeList(List<MethodParamNode> methodParamNodeList) {
        this.methodParamNodeList = methodParamNodeList;
    }
}
