package com.lc.mybatisx.model;

import com.lc.mybatisx.annotation.LogicDelete;

import java.util.List;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2021/7/9 17:16
 */
public class MapperNode {

    private Class<?> interfaceClass;

    private TypeParamNode entityTypeParamNode;

    private TypeParamNode primaryKeyTypeParamNode;

    private List<ActionNode> actionNodeList;

    private LogicDelete logicDelete;

    public Class<?> getInterfaceClass() {
        return interfaceClass;
    }

    public void setInterfaceClass(Class<?> interfaceClass) {
        this.interfaceClass = interfaceClass;
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

    public LogicDelete getLogicDelete() {
        return logicDelete;
    }

    public void setLogicDelete(LogicDelete logicDelete) {
        this.logicDelete = logicDelete;
    }
}
