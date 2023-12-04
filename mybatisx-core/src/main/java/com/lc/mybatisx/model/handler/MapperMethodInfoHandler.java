package com.lc.mybatisx.model.handler;

import com.lc.mybatisx.annotation.Dynamic;
import com.lc.mybatisx.model.MapperInfo;
import com.lc.mybatisx.model.MethodInfo;
import com.lc.mybatisx.model.MethodParamInfo;
import com.lc.mybatisx.model.MethodReturnInfo;
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
public class MapperMethodInfoHandler {

    private StructNodeHandler structNodeHandler = new StructNodeHandler();

    public List<MethodInfo> execute(MapperInfo mapperInfo, Class<?> interfaceClass) {
        Method[] methods = interfaceClass.getMethods();

        List<MethodInfo> methodNodeList = new ArrayList<>();
        for (Method method : methods) {
            List<MethodParamInfo> methodParamInfoList = getMethodParam(mapperInfo, method);
            MethodReturnInfo methodReturnInfo = getMethodReturn(mapperInfo, method);

            MethodInfo methodInfo = new MethodInfo();
            methodInfo.setMethodName(method.getName());
            methodInfo.setMethodReturnInfo(methodReturnInfo);
            methodInfo.setDynamic(method.getAnnotation(Dynamic.class) != null);
            methodInfo.setSingleParam(methodParamInfoList.size() == 1);
            if (methodInfo.getSingleParam()) {
                methodInfo.setMethodParamInfo(methodParamInfoList.get(0));
            } else {
                methodInfo.setMethodParamInfoList(methodParamInfoList);
            }

            methodNodeList.add(methodInfo);
        }

        return methodNodeList;
    }

    private List<MethodParamInfo> getMethodParam(MapperInfo mapperInfo, Method method) {
        Parameter[] parameters = method.getParameters();
        List<MethodParamInfo> methodParamInfoList = new ArrayList<>();

        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            Class<?> clazz = getMethodParamType(mapperInfo, parameter);
            Boolean basicType = structNodeHandler.isBasicType(clazz);

            MethodParamInfo methodParamInfo = new MethodParamInfo();
            methodParamInfo.setBasicType(basicType);
            methodParamInfo.setTypeName(clazz.getName());
            methodParamInfo.setType(clazz);
            methodParamInfo.setContainerType(getContainerType(parameter.getType()));
            if (!basicType) {
                methodParamInfo.setFieldNodeList(structNodeHandler.parseField(clazz));
            }
            Param param = parameter.getAnnotation(Param.class);
            if (param != null) {
                methodParamInfo.setParamName(param.value());
                methodParamInfo.setParam(param);
            }
            Annotation[] annotations = parameter.getAnnotations();
            if (ArrayUtils.isNotEmpty(annotations)) {
                methodParamInfo.setAnnotations(parameter.getAnnotations());
            }

            methodParamInfoList.add(methodParamInfo);
        }

        return methodParamInfoList;
    }

    private MethodReturnInfo getMethodReturn(MapperInfo mapperInfo, Method method) {
        Class<?> clazz = getMethodReturnType(mapperInfo, method);

        MethodReturnInfo methodReturnInfo = new MethodReturnInfo();
        methodReturnInfo.setType(clazz);
        Boolean basicType = structNodeHandler.isBasicType(clazz);
        methodReturnInfo.setBasicType(basicType);
        methodReturnInfo.setContainerType(getContainerType(clazz));
        if (!basicType) {
            methodReturnInfo.setFieldNodeList(structNodeHandler.parseField(clazz));
        }
        return methodReturnInfo;
    }

    private Class<?> getMethodParamType(MapperInfo mapperInfo, Parameter parameter) {
        Type type = parameter.getParameterizedType();
        return getMethodType(mapperInfo, type);
    }

    private Class<?> getMethodReturnType(MapperInfo mapperInfo, Method method) {
        Type type = method.getGenericReturnType();
        return getMethodType(mapperInfo, type);
    }

    private Class<?> getMethodType(MapperInfo mapperInfo, Type type) {
        Type actualType = GenericUtils.getGenericType(type);
        String actualTypeName = actualType.getTypeName();
        if ("ID".equals(actualTypeName)) {
            return mapperInfo.getIdClass();
        } else if ("ENTITY".equals(actualTypeName)) {
            return mapperInfo.getEntityClass();
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
