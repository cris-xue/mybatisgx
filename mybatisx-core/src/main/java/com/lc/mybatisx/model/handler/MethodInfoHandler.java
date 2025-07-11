package com.lc.mybatisx.model.handler;

import com.google.common.base.CaseFormat;
import com.lc.mybatisx.annotation.ConditionEntity;
import com.lc.mybatisx.annotation.Dynamic;
import com.lc.mybatisx.annotation.Entity;
import com.lc.mybatisx.dao.Dao;
import com.lc.mybatisx.dao.SimpleDao;
import com.lc.mybatisx.model.*;
import com.lc.mybatisx.utils.GenericUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ：薛承城
 * @description：用于解析mybatis接口方法
 * @date ：2023/12/1
 */
public class MethodInfoHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodInfoHandler.class);

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
        Map<String, MethodInfo> methodInfoMap = new LinkedHashMap<>();

        this.daoClass(interfaceClass, mapperInfo, methodInfoMap);

        /*Method[] declaredMethods = interfaceClass.getDeclaredMethods();
        processMethod(declaredMethods, mapperInfo, methodInfoMap);*/
        /*for (Method method : declaredMethods) {
            String methodName = method.getName();
            List<MethodParamInfo> methodParamInfoList = getMethodParam(mapperInfo, method);
            MethodReturnInfo methodReturnInfo = getMethodReturn(mapperInfo, method);

            MethodInfo methodInfo = new MethodInfo();
            methodInfo.setMethod(method);
            methodInfo.setMethodName(methodName);
            methodInfo.setDynamic(method.getAnnotation(Dynamic.class) != null);
            methodInfo.setMethodParamInfoList(methodParamInfoList);
            methodInfo.setMethodReturnInfo(methodReturnInfo);

            // 方法名解析
            methodNameParse(mapperInfo.getEntityInfo(), methodInfo);

            ResultMapInfo resultMapInfo = resultMapInfoHandler.execute(methodInfo, methodReturnInfo);
            methodInfo.setResultMapInfo(resultMapInfo);

            handleConditionParamInfo(methodInfo);
            // check(resultMapInfo, methodInfo);

            methodInfoList.add(methodInfo);
        }*/

        /*Method[] methods = interfaceClass.getMethods();
        processMethod(methods, mapperInfo, methodInfoMap);*/

        return methodInfoMap.values().stream().collect(Collectors.toList());
    }

    private void daoClass(Class<?> daoClass, MapperInfo mapperInfo, Map<String, MethodInfo> methodInfoMap) {
        Method[] declaredMethods = daoClass.getDeclaredMethods();
        processMethod(declaredMethods, mapperInfo, methodInfoMap);

        Type[] superInterfaces = daoClass.getGenericInterfaces();
        for (int i = 0; i < superInterfaces.length; i++) {
            Type type = superInterfaces[i];
            if (type instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) type;
                Class<?> superInterface = (Class<?>) parameterizedType.getRawType();
                if (ClassUtils.isAssignable(superInterface, Dao.class)) {
                    this.daoClass(superInterface, mapperInfo, methodInfoMap);
                }
            }
        }
    }

    private void processMethod(Method[] methods, MapperInfo mapperInfo, Map<String, MethodInfo> methodInfoMap) {
        for (Method method : methods) {
            Class<?> methodDeclaringClass = method.getDeclaringClass();
            String methodName = method.getName();
            if (methodInfoMap.containsKey(methodName)) {
                LOGGER.error("方法名{}已存在，请修改方法名！", methodName);
                continue;
            }

            List<MethodParamInfo> methodParamInfoList = getMethodParam(mapperInfo, method);
            MethodReturnInfo methodReturnInfo = getMethodReturn(mapperInfo, method);

            MethodInfo methodInfo = new MethodInfo();
            methodInfo.setMethod(method);
            methodInfo.setMethodName(methodName);
            methodInfo.setDynamic(method.getAnnotation(Dynamic.class) != null);
            methodInfo.setMethodParamInfoList(methodParamInfoList);
            methodInfo.setMethodReturnInfo(methodReturnInfo);

            // 方法名解析
            methodNameParse(mapperInfo.getEntityInfo(), methodInfo, methodDeclaringClass);

            String resultMapId = resultMapInfoHandler.execute(mapperInfo, methodInfo);
            methodInfo.setResultMapId(resultMapId);

            handleConditionParamInfo(methodInfo);
            // check(resultMapInfo, methodInfo);

            methodInfoMap.put(methodName, methodInfo);
        }
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

            Boolean basicType = getBasicType(clazz);
            methodParamInfo.setBasicType(basicType);
            if (!basicType) {
                List<ColumnInfo> columnInfoList = columnInfoHandler.getColumnInfoList(clazz);
                methodParamInfo.setColumnInfoList(columnInfoList);
            }
            Class<?> collectionType = getCollectionType(parameter.getType());
            if (collectionType != null) {
                methodParamInfo.setCollectionType(true);
                methodParamInfo.setCollectionTypeName(collectionType.getTypeName());
            }

            methodParamInfoList.add(methodParamInfo);
        }

        return methodParamInfoList;
    }

    private MethodReturnInfo getMethodReturn(MapperInfo mapperInfo, Method method) {
        Class<?> clazz = getMethodReturnType(mapperInfo, method);
        Boolean basicType = getBasicType(clazz);

        MethodReturnInfo methodReturnInfo = new MethodReturnInfo();
        methodReturnInfo.setBasicType(basicType);
        methodReturnInfo.setType(clazz);
        methodReturnInfo.setTypeName(clazz.getName());
        if (!basicType) {
            List<ColumnInfo> columnInfoList = columnInfoHandler.getColumnInfoList(clazz);
            methodReturnInfo.setColumnInfoList(columnInfoList);
        }
        Class<?> collectionType = getCollectionType(clazz);
        if (containerType != null) {
            methodReturnInfo.setIsContainerType(true);
            methodReturnInfo.setContainerType(containerType);
            methodReturnInfo.setContainerTypeName(containerType.getTypeName());
        }

        return methodReturnInfo;
    }

    public void methodNameParse(EntityInfo entityInfo, MethodInfo methodInfo, Class<?> methodDeclaringClass) {
        methodNameAstHandler.execute(entityInfo, methodInfo);

        // 条件实体只有非SimpleDao方法才会处理，SimpleDao只有findList会特殊处理
        if (methodDeclaringClass == SimpleDao.class) {
            String methodName = methodInfo.getMethodName();
            if ("findOne".equals(methodName) || "findList".equals(methodName)) {
                List<MethodParamInfo> methodParamInfoList = methodInfo.getMethodParamInfoList();
                for (MethodParamInfo methodParamInfo : methodParamInfoList) {
                    Entity entity = methodParamInfo.getType().getAnnotation(Entity.class);
                    if (entity != null) {
                        String entityCondition = entityCondition(methodInfo, methodParamInfo);
                        methodNameAstHandler.execute(entityInfo, methodInfo, true, entityCondition);
                    }
                }
            }
        } else {
            List<MethodParamInfo> methodParamInfoList = methodInfo.getMethodParamInfoList();
            for (MethodParamInfo methodParamInfo : methodParamInfoList) {
                ConditionEntity isConditionEntity = methodParamInfo.getType().getAnnotation(ConditionEntity.class);
                if (isConditionEntity != null) {
                    String entityCondition = entityCondition(methodInfo, methodParamInfo);
                    methodNameAstHandler.execute(entityInfo, methodInfo, true, entityCondition);
                }
            }
        }
    }

    private String entityCondition(MethodInfo methodInfo, MethodParamInfo methodParamInfo) {
        StringBuilder stringBuilder = new StringBuilder(methodInfo.getAction()).append("By");
        List<ColumnInfo> columnInfoList = methodParamInfo.getColumnInfoList();
        columnInfoList.forEach(columnInfo -> {
            String javaColumnName = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, columnInfo.getJavaColumnName());
            if (!stringBuilder.toString().endsWith("By")) {
                stringBuilder.append("And");
            }
            stringBuilder.append(javaColumnName);
        });
        LOGGER.debug(stringBuilder.toString());
        return stringBuilder.toString();
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
                // TODO 这里还需校验条件实体的参数    暂时先不校验
                // throw new RuntimeException("查询条件没有对应的参数");
                continue;
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

    private Class<?> getCollectionType(Type type) {
        if (TypeUtils.isAssignable(type, Collection.class)) {
            return Collection.class;
        }
        return null;
    }

    public Boolean getBasicType(Type type) {
        for (Class<?> basicType : basicTypeList) {
            if (type == basicType) {
                return true;
            }
        }
        return false;
    }
}
