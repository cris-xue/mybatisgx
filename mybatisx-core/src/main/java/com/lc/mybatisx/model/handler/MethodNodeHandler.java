package com.lc.mybatisx.model.handler;

import com.lc.mybatisx.annotation.Dynamic;
import com.lc.mybatisx.model.*;
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
public class MethodNodeHandler {

    private StructNodeHandler structNodeHandler = new StructNodeHandler();

    public List<MethodNode> execute(InterfaceNode interfaceNode) {
        Class<?> interfaceClass = interfaceNode.getInterfaceClass();
        Method[] methods = interfaceClass.getMethods();

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

    private List<MethodParamNode> parseMethodParam(InterfaceNode interfaceNode, Method method) {
        Parameter[] parameters = method.getParameters();
        List<MethodParamNode> methodParamNodeList = new ArrayList<>();

        for (int i = 0; i < parameters.length; i++) {
            MethodParamNode methodParamNode = new MethodParamNode();

            Parameter parameter = parameters[i];
            Class<?> clazz = getMethodParamType(interfaceNode, parameter);

            Boolean basicType = structNodeHandler.isBasicType(clazz);
            methodParamNode.setBasicType(basicType);
            methodParamNode.setType(clazz);
            methodParamNode.setContainerType(getContainerType(parameter.getType()));
            if (!basicType) {
                methodParamNode.setFieldNodeList(structNodeHandler.parseField(clazz));
            }
            Param param = parameter.getAnnotation(Param.class);
            if (param != null) {
                methodParamNode.setName(param.value());
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

    private ReturnNode parseMethodReturn(InterfaceNode interfaceNode, Method method) {
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

        TypeParamNode idTypeParamNode = interfaceNode.getIdTypeParamNode();
        String idTypeParamName = idTypeParamNode.getName();

        TypeParamNode entityTypeParamNode = interfaceNode.getEntityTypeParamNode();
        String entityTypeParamName = entityTypeParamNode.getName();

        if (actualTypeName.equals(idTypeParamName)) {
            return idTypeParamNode.getType();
        } else if (actualTypeName.equals(entityTypeParamName)) {
            return entityTypeParamNode.getType();
        } else {
            return (Class<?>) actualType;
        }
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
