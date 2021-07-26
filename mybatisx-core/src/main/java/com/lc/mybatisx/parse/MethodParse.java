package com.lc.mybatisx.parse;

import com.lc.mybatisx.annotation.Dynamic;
import com.lc.mybatisx.model.InterfaceNode;
import com.lc.mybatisx.model.MethodNode;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MethodParse {

    public List<MethodNode> excute(Class<?> daoInterface, InterfaceNode interfaceNode) {
        Method[] methods = daoInterface.getMethods();

        List<MethodNode> methodNodeList = new ArrayList<>();
        for (Method method : methods) {
            MethodNode methodNode = new MethodNode();

            methodNode.setMethod(method);
            methodNode.setName(method.getName());
            methodNode.setMethodParamNodeList(parseMethodParam(interfaceNode, method));
            methodNode.setReturnNode(parseMethodReturn(interfaceNode, method));
            methodNode.setDynamic(method.getAnnotation(Dynamic.class));

            methodNodeList.add(methodNode);
        }

        return methodNodeList;
    }

    class MethodParamParse {

    }

    class MethodReturnParse {

    }

}
