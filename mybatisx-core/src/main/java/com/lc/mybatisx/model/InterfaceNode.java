package com.lc.mybatisx.model;

import java.util.List;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2021/7/9 17:16
 */
public class InterfaceNode {

    private String name;

    private Class<?> interfaceClass;

    private List<TypeParamNode> typeParamNodeList;

    private TypeParamNode idTypeParamNode;

    private EntityTypeParamNode entityTypeParamNode;

    private List<MethodNode> methodNodeList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public TypeParamNode getIdTypeParamNode() {
        return idTypeParamNode;
    }

    public void setIdTypeParamNode(TypeParamNode idTypeParamNode) {
        this.idTypeParamNode = idTypeParamNode;
    }

    public EntityTypeParamNode getEntityTypeParamNode() {
        return entityTypeParamNode;
    }

    public void setEntityTypeParamNode(EntityTypeParamNode entityTypeParamNode) {
        this.entityTypeParamNode = entityTypeParamNode;
    }

    public List<MethodNode> getMethodNodeList() {
        return methodNodeList;
    }

    public void setMethodNodeList(List<MethodNode> methodNodeList) {
        this.methodNodeList = methodNodeList;
    }

}
