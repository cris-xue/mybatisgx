package com.lc.mybatisx.model;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2021/7/9 17:13
 */
public class MethodParamNode extends StructNode {

    private String name;

    private List<Annotation> paramAnnotationList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Annotation> getParamAnnotationList() {
        return paramAnnotationList;
    }

    public void setParamAnnotationList(List<Annotation> paramAnnotationList) {
        this.paramAnnotationList = paramAnnotationList;
    }
}
