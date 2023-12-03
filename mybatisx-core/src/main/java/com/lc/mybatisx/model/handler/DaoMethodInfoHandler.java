package com.lc.mybatisx.model.handler;

import com.lc.mybatisx.annotation.Dynamic;
import com.lc.mybatisx.model.InterfaceNode;
import com.lc.mybatisx.model.MethodNode;
import com.lc.mybatisx.model.MethodParamNode;
import com.lc.mybatisx.model.ReturnNode;
import com.lc.mybatisx.utils.GenericUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.util.TypeUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author ：薛承城
 * @description：用于解析mybatis接口方法
 * @date ：2023/12/1
 */
public class DaoMethodInfoHandler {

    private StructNodeHandler structNodeHandler = new StructNodeHandler();

    public List<MethodNode> execute(Class<?> interfaceClass) {
        Method[] methods = interfaceClass.getMethods();

        List<MethodNode> methodNodeList = new ArrayList<>();
        for (Method method : methods) {
            List<MethodParamNode> methodParamNodeList = getMethodParam(null, method);
            ReturnNode returnNode = getMethodReturn(null, method);

            MethodNode methodNode = new MethodNode();
            methodNode.setMethod(method);
            methodNode.setMethodName(method.getName());
            methodNode.setReturnNode(returnNode);
            methodNode.setDynamic(method.getAnnotation(Dynamic.class) != null);
            methodNode.setSingleParam(methodParamNodeList.size() == 1);
            if (methodNode.getSingleParam()) {
                methodNode.setMethodParamNode(methodParamNodeList.get(0));
            } else {
                methodNode.setMethodParamNodeList(methodParamNodeList);
            }

            methodNodeList.add(methodNode);
        }

        return methodNodeList;
    }

    private List<MethodParamNode> getMethodParam(InterfaceNode interfaceNode, Method method) {
        Parameter[] parameters = method.getParameters();
        List<MethodParamNode> methodParamNodeList = new ArrayList<>();

        for (int i = 0; i < parameters.length; i++) {
            MethodParamNode methodParamNode = new MethodParamNode();

            Parameter parameter = parameters[i];
            Class<?> clazz = getMethodParamType(interfaceNode, parameter);

            Boolean basicType = structNodeHandler.isBasicType(clazz);
            methodParamNode.setBasicType(basicType);
            methodParamNode.setTypeName(clazz.getName());
            methodParamNode.setType(clazz);
            methodParamNode.setContainerType(getContainerType(parameter.getType()));
            if (!basicType) {
                methodParamNode.setFieldNodeList(structNodeHandler.parseField(clazz));
            }
            Param param = parameter.getAnnotation(Param.class);
            if (param != null) {
                methodParamNode.setParamName(param.value());
                methodParamNode.setParam(param);
            }
            Annotation[] annotations = parameter.getAnnotations();
            if (ArrayUtils.isNotEmpty(annotations)) {
                methodParamNode.setAnnotations(parameter.getAnnotations());
            }

            methodParamNodeList.add(methodParamNode);
        }

        return methodParamNodeList;
    }

    private ReturnNode getMethodReturn(InterfaceNode interfaceNode, Method method) {
        Class<?> clazz = getMethodReturnType(interfaceNode, method);

        ReturnNode returnNode = new ReturnNode();
        returnNode.setType(clazz);
        Boolean basicType = structNodeHandler.isBasicType(clazz);
        returnNode.setBasicType(basicType);
        returnNode.setContainerType(getContainerType(clazz));
        if (!basicType) {
            returnNode.setFieldNodeList(structNodeHandler.parseField(clazz));
        }
        return returnNode;
    }

    private Class<?> getMethodParamType(InterfaceNode interfaceNode, Parameter parameter) {
        Type type = parameter.getParameterizedType();
        return getMethodType(interfaceNode, type);
    }

    private Class<?> getMethodReturnType(InterfaceNode interfaceNode, Method method) {
        Type type = method.getGenericReturnType();
        return getMethodType(interfaceNode, type);
    }

    private Class<?> getMethodType(InterfaceNode interfaceNode, Type type) {
        Type actualType = GenericUtils.getGenericType(type);
        String actualTypeName = actualType.getTypeName();

        // IdNode idTypeParamNode = interfaceNode.getIdNode();
        /*String idTypeParamName = idTypeParamNode.getName();

        // EntityNode entityTypeParamNode = interfaceNode.getEntityNode();
        String entityTypeParamName = entityTypeParamNode.getName();

        if (actualTypeName.equals(idTypeParamName)) {
            return idTypeParamNode.getType();
        } else if (actualTypeName.equals(entityTypeParamName)) {
            return entityTypeParamNode.getType();
        } else {
            return (Class<?>) actualType;
        }*/
        return null;
    }

    private Class<?> getContainerType(Type type) {
        if (TypeUtils.isAssignable(Collection.class, type)) {
            return Collection.class;
        } else if (TypeUtils.isAssignable(Map.class, type)) {
            return Map.class;
        }
        return null;
    }

}
