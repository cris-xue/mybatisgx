package com.lc.mybatisx.model.handler;

import com.lc.mybatisx.annotation.Dynamic;
import com.lc.mybatisx.model.*;
import com.lc.mybatisx.utils.GenericUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.util.TypeUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.*;

/**
 * @author ：薛承城
 * @description：用于解析mybatis接口方法
 * @date ：2023/12/1
 */
public class MethodInfoHandler {

    private static final List<Class<?>> basicTypeList = new ArrayList<>();

    static {
        basicTypeList.add(int.class);
        basicTypeList.add(Integer.class);
        basicTypeList.add(Long.class);
        basicTypeList.add(String.class);
    }

    private static List<String> simpleMethodList = Arrays.asList(
            "insert",
            "insertSelective",
            "deleteById",
            "updateById",
            "updateByIdSelective",
            "findById",
            "findAll",
            "findList"
    );

    private ColumnInfoHandler columnInfoHandler = new ColumnInfoHandler();
    private MethodNameInfoHandler methodNameInfoHandler = new MethodNameInfoHandler();

    public List<MethodInfo> execute(MapperInfo mapperInfo, Class<?> interfaceClass) {
        Method[] methods = interfaceClass.getMethods();

        List<MethodInfo> methodNodeList = new ArrayList<>();
        for (Method method : methods) {
            String methodName = method.getName();
            List<MethodParamInfo> methodParamInfoList = getMethodParam(mapperInfo, method);
            Boolean isSingleParam = methodParamInfoList.size() == 1;
            MethodReturnInfo methodReturnInfo = getMethodReturn(mapperInfo, method);

            MethodInfo methodInfo = new MethodInfo();
            methodInfo.setMethodName(methodName);
            methodInfo.setMethodNameQueryInfo(getMethodNameQueryInfo(methodName));
            methodInfo.setMethodReturnInfo(methodReturnInfo);
            methodInfo.setDynamic(method.getAnnotation(Dynamic.class) != null);
            methodInfo.setSingleParam(isSingleParam);
            if (isSingleParam) {
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
            Boolean basicType = isBasicType(clazz);

            MethodParamInfo methodParamInfo = new MethodParamInfo();
            methodParamInfo.setBasicType(basicType);
            methodParamInfo.setType(clazz);
            methodParamInfo.setTypeName(clazz.getName());
            Param param = parameter.getAnnotation(Param.class);
            if (param != null) {
                methodParamInfo.setParamName(param.value());
            }
            if (!basicType) {
                List<ColumnInfo> columnInfoList = columnInfoHandler.getColumnInfoList(clazz);
                methodParamInfo.setColumnInfoList(columnInfoList);
            }
            Class<?> containerType = getContainerType(parameter.getType());
            if (containerType != null) {
                methodParamInfo.setIsContainerType(true);
                methodParamInfo.setContainerType(containerType);
                methodParamInfo.setContainerTypeName(containerType.getTypeName());
            }

            methodParamInfoList.add(methodParamInfo);
        }

        return methodParamInfoList;
    }

    private MethodReturnInfo getMethodReturn(MapperInfo mapperInfo, Method method) {
        Class<?> clazz = getMethodReturnType(mapperInfo, method);
        Boolean basicType = isBasicType(clazz);

        MethodReturnInfo methodReturnInfo = new MethodReturnInfo();
        methodReturnInfo.setBasicType(basicType);
        methodReturnInfo.setType(clazz);
        methodReturnInfo.setTypeName(clazz.getName());
        if (!basicType) {
            List<ColumnInfo> columnInfoList = columnInfoHandler.getColumnInfoList(clazz);
            methodReturnInfo.setColumnInfoList(columnInfoList);
        }
        Class<?> containerType = getContainerType(clazz);
        if (containerType != null) {
            methodReturnInfo.setIsContainerType(true);
            methodReturnInfo.setContainerType(containerType);
            methodReturnInfo.setContainerTypeName(containerType.getTypeName());
        }

        return methodReturnInfo;
    }

    public MethodNameQueryInfo getMethodNameQueryInfo(String methodName) {
        if (simpleMethodList.contains(methodName)) {
            return null;
        }
        methodNameInfoHandler.execute(methodName);
        return null;
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

    public Boolean isBasicType(Type type) {
        for (Class<?> bt : basicTypeList) {
            if (type == bt) {
                return true;
            }
        }
        return false;
    }

}
