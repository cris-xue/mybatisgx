package com.lc.mybatisx.model;

import com.lc.mybatisx.annotation.Dynamic;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2021/7/9 17:14
 */
public class MethodNode {

    private Method method;

    private String name;

    private List<MethodParamNode> methodParamNodeList;

    private ReturnNode returnNode;

    private Dynamic dynamic;

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<MethodParamNode> getMethodParamNodeList() {
        return methodParamNodeList;
    }

    public void setMethodParamNodeList(List<MethodParamNode> methodParamNodeList) {
        this.methodParamNodeList = methodParamNodeList;
    }

    public ReturnNode getReturnNode() {
        return returnNode;
    }

    public void setReturnNode(ReturnNode returnNode) {
        this.returnNode = returnNode;
    }

    public Dynamic getDynamic() {
        return dynamic;
    }

    public void setDynamic(Dynamic dynamic) {
        this.dynamic = dynamic;
    }
}
