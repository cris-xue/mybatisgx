package com.lc.mybatisx.model.handler;

import com.google.common.base.CaseFormat;
import com.lc.mybatisx.annotation.Dynamic;
import com.lc.mybatisx.model.*;
import com.lc.mybatisx.utils.GenericUtils;
import org.apache.commons.lang3.ObjectUtils;
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
    private MethodNameAstHandler methodNameAstHandler = new MethodNameAstHandler();
    private ResultMapInfoHandler resultMapInfoHandler = new ResultMapInfoHandler();

    public List<MethodInfo> execute(MapperInfo mapperInfo, Class<?> interfaceClass) {
        Method[] methods = interfaceClass.getMethods();

        List<MethodInfo> methodInfoList = new ArrayList<>();
        for (Method method : methods) {
            String methodName = method.getName();
            List<MethodParamInfo> methodParamInfoList = getMethodParam(mapperInfo, method);
            MethodReturnInfo methodReturnInfo = getMethodReturn(mapperInfo, method);

            MethodInfo methodInfo = new MethodInfo();
            methodInfo.setMethod(method);
            methodInfo.setMethodName(methodName);
            methodInfo.setDynamic(method.getAnnotation(Dynamic.class) != null);
            // methodInfo.setConditionInfoList();
            methodInfo.setMethodParamInfoList(methodParamInfoList);
            methodInfo.setMethodReturnInfo(methodReturnInfo);

            // 方法名解析
            methodNameParse(mapperInfo.getTableInfo(), methodInfo);

            ResultMapInfo resultMapInfo = resultMapInfoHandler.execute(methodInfo, methodReturnInfo);
            methodInfo.setResultMapInfo(resultMapInfo);

            handleConditionParamInfo(methodInfo);
            // check(resultMapInfo, methodInfo);

            methodInfoList.add(methodInfo);
        }

        return methodInfoList;
    }

    /**
     * findByIds(List)
     *
     * @param mapperInfo
     * @param method
     * @return
     */
    private List<MethodParamInfo> getMethodParam(MapperInfo mapperInfo, Method method) {
        Parameter[] parameters = method.getParameters();
        List<MethodParamInfo> methodParamInfoList = new ArrayList<>();
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            Class<?> clazz = getMethodParamType(mapperInfo, parameter);

            MethodParamInfo methodParamInfo = new MethodParamInfo();
            methodParamInfo.setType(clazz);
            methodParamInfo.setTypeName(clazz.getName());

            String paramName = parameter.getName();
            Param param = parameter.getAnnotation(Param.class);
            if (param != null) {
                paramName = param.value();
            }
            methodParamInfo.setParamName(paramName);

            Boolean basicType = isBasicType(clazz);
            methodParamInfo.setBasicType(basicType);
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

    public void methodNameParse(TableInfo tableInfo, MethodInfo methodInfo) {
        /*if (simpleMethodList.contains(methodInfo.getMethodName())) {
            return;
        }*/
        methodNameAstHandler.execute(tableInfo, methodInfo);
    }

    /**
     * 处理查询条件和方法参数的关联
     *
     * @param methodInfo
     */
    public void handleConditionParamInfo(MethodInfo methodInfo) {
        List<ConditionInfo> conditionInfoList = methodInfo.getConditionInfoList();
        for (int i = 0; i < conditionInfoList.size(); i++) {
            ConditionInfo conditionInfo = conditionInfoList.get(i);

            // 处理查询条件和参数之间的关系，需要对特殊操作符进行处理，如between
            Integer index = conditionInfo.getIndex();
            String javaColumnName = conditionInfo.getJavaColumnName();
            String op = conditionInfo.getOp();

            MethodParamInfo methodParamInfo = methodInfo.getMethodParamInfo("arg" + index);
            if (methodParamInfo == null) {
                methodParamInfo = methodInfo.getMethodParamInfo(javaColumnName);
            }
            if (methodParamInfo == null) {
                methodParamInfo = methodInfo.getMethodParamInfo(javaColumnName.toLowerCase());
            }
            if (methodParamInfo == null) {
                throw new RuntimeException("查询条件没有对应的参数");
            }
            conditionInfo.addMethodParamInfo(methodParamInfo);
        }
    }

    /**
     * 检查方法名信息和方法信息参数是否匹配
     */
    public void check(ResultMapInfo resultMapInfo, MethodInfo methodInfo) {
        List<ConditionInfo> conditionInfoList = methodInfo.getConditionInfoList();
        if (ObjectUtils.isEmpty(conditionInfoList)) {
            return;
        }
        Map<String, MethodParamInfo> methodParamInfoMap = methodInfo.getMethodParamInfoMap();

        if (conditionInfoList.size() != methodParamInfoMap.size()) {
            throw new RuntimeException("方法名中的查询条件和方法参数中中查询条件个数不匹配");
        }

        for (int i = 0; i < conditionInfoList.size(); i++) {
            ConditionInfo conditionInfo = conditionInfoList.get(i);
            MethodParamInfo methodParamInfo = methodParamInfoMap.get(i);

            String javaColumnName = conditionInfo.getJavaColumnName();
            javaColumnName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, javaColumnName);
            ColumnInfo columnInfo = resultMapInfo.getColumnInfoMap().get(javaColumnName);
            if (columnInfo == null) {
                throw new RuntimeException("方法名中的字段在实体类中不存在: " + javaColumnName);
            }
            conditionInfo.setDbColumnName(columnInfo.getDbColumnName());
            conditionInfo.setJavaColumnName(methodParamInfo.getParamName());
        }
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
