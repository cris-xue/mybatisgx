package com.lc.mybatisx.model;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2021/7/9 17:16
 */
public class MapperNode {

    private List<Class<?>> superInterfaceClassList;

    private Class<?> interfaceClass;

    private List<TypeParamNode> typeParamNodeList;
    /**
     * 实体节点
     */
    private TypeParamNode entityTypeParamNode;

    private TypeParamNode primaryKeyTypeParamNode;

    private List<ActionNode> actionNodeList;

    private Boolean isLogicDelete;

    private List<Annotation> annotationList;

    public List<Class<?>> getSuperInterfaceClassList() {
        return superInterfaceClassList;
    }

    public void setSuperInterfaceClassList(List<Class<?>> superInterfaceClassList) {
        this.superInterfaceClassList = superInterfaceClassList;
    }

    public Class<?> getInterfaceClass() {
        return interfaceClass;
    }

    public void setInterfaceClass(Class<?> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    public List<TypeParamNode> getTypeParamNodeList() {
        return typeParamNodeList;
    }

    public void setTypeParamNodeList(List<TypeParamNode> typeParamNodeList) {
        this.typeParamNodeList = typeParamNodeList;
    }

    public TypeParamNode getEntityTypeParamNode() {
        return entityTypeParamNode;
    }

    public void setEntityTypeParamNode(TypeParamNode entityTypeParamNode) {
        this.entityTypeParamNode = entityTypeParamNode;
    }

    public TypeParamNode getPrimaryKeyTypeParamNode() {
        return primaryKeyTypeParamNode;
    }

    public void setPrimaryKeyTypeParamNode(TypeParamNode primaryKeyTypeParamNode) {
        this.primaryKeyTypeParamNode = primaryKeyTypeParamNode;
    }

    public List<ActionNode> getActionNodeList() {
        return actionNodeList;
    }

    public void setActionNodeList(List<ActionNode> actionNodeList) {
        this.actionNodeList = actionNodeList;
    }

    public Boolean getLogicDelete() {
        return isLogicDelete;
    }

    public void setLogicDelete(Boolean logicDelete) {
        isLogicDelete = logicDelete;
    }

    public List<Annotation> getAnnotationList() {
        return annotationList;
    }

    public void setAnnotationList(List<Annotation> annotationList) {
        this.annotationList = annotationList;
    }

}
