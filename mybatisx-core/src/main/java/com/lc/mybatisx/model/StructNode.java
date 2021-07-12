package com.lc.mybatisx.model;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2021/7/9 17:34
 */
public class StructNode {

    private Boolean isBasicType;

    private Class<?> type;

    private List<FieldNode> fieldNodeList;

    private List<Annotation> annotationList;

    public Boolean getBasicType() {
        return isBasicType;
    }

    public void setBasicType(Boolean basicType) {
        isBasicType = basicType;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public List<FieldNode> getFieldNodeList() {
        return fieldNodeList;
    }

    public void setFieldNodeList(List<FieldNode> fieldNodeList) {
        this.fieldNodeList = fieldNodeList;
    }

    public List<Annotation> getAnnotationList() {
        return annotationList;
    }

    public void setAnnotationList(List<Annotation> annotationList) {
        this.annotationList = annotationList;
    }
}
