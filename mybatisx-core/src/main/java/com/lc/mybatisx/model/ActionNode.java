package com.lc.mybatisx.model;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2021/7/9 17:14
 */
public class ActionNode {

    private Method method;

    private String name;

    private List<MethodParamNode> methodParamNodeList;

    private ResultNode resultNode;

    private Boolean isDynamic;

    private List<Annotation> annotationList;

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

    public ResultNode getResultNode() {
        return resultNode;
    }

    public void setResultNode(ResultNode resultNode) {
        this.resultNode = resultNode;
    }

    public Boolean getDynamic() {
        return isDynamic;
    }

    public void setDynamic(Boolean dynamic) {
        isDynamic = dynamic;
    }

    public List<Annotation> getAnnotationList() {
        return annotationList;
    }

    public void setAnnotationList(List<Annotation> annotationList) {
        this.annotationList = annotationList;
    }
}
