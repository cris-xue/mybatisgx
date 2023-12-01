package com.lc.mybatisx.model;

import java.util.List;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2021/7/9 17:16
 */
public class InterfaceNode {

    private String interfaceName;

    private Class<?> interfaceClass;

    private String tableName;

    private IdNode idNode;

    private EntityNode entityNode;

    private List<MethodNode> methodNodeList;

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public Class<?> getInterfaceClass() {
        return interfaceClass;
    }

    public void setInterfaceClass(Class<?> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public IdNode getIdNode() {
        return idNode;
    }

    public void setIdNode(IdNode idNode) {
        this.idNode = idNode;
    }

    public EntityNode getEntityNode() {
        return entityNode;
    }

    public void setEntityNode(EntityNode entityNode) {
        this.entityNode = entityNode;
    }

    public List<MethodNode> getMethodNodeList() {
        return methodNodeList;
    }

    public void setMethodNodeList(List<MethodNode> methodNodeList) {
        this.methodNodeList = methodNodeList;
    }
}
