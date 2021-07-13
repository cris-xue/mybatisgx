package com.lc.mybatisx.model;

import java.util.List;

/**
 * @author ：薛承城
 * @description：一句话描述
 * @date ：2021/7/9 17:16
 */
public class MapperNode {

    private Class<?> interfaceClass;

    private List<TypeParamNode> typeParamNodeList;

    private List<ActionNode> actionNodeList;

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

    public List<ActionNode> getActionNodeList() {
        return actionNodeList;
    }

    public void setActionNodeList(List<ActionNode> actionNodeList) {
        this.actionNodeList = actionNodeList;
    }

}
