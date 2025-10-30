package com.lc.mybatisx.model.handler;

import com.google.common.base.CaseFormat;
import com.lc.mybatisx.annotation.*;
import com.lc.mybatisx.context.EntityInfoContextHolder;
import com.lc.mybatisx.context.MethodInfoContextHolder;
import com.lc.mybatisx.dao.Dao;
import com.lc.mybatisx.dao.SimpleDao;
import com.lc.mybatisx.model.*;
import com.lc.mybatisx.utils.GenericUtils;
import com.lc.mybatisx.utils.TypeUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

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

    private ColumnInfoHandler columnInfoHandler = new ColumnInfoHandler();
    private MethodNameAstHandler methodNameAstHandler = new MethodNameAstHandler();
    private QueryConditionAstHandler queryConditionAstHandler = new QueryConditionAstHandler();
    private EntityRelationTreeHandler entityRelationTreeHandler = new EntityRelationTreeHandler();
    private ResultMapInfoHandler resultMapInfoHandler = new ResultMapInfoHandler();

    public List<MethodInfo> execute(MapperInfo mapperInfo, Class<?> interfaceClass) {
        Map<String, MethodInfo> methodInfoMap = new LinkedHashMap<>();
        this.daoClass(interfaceClass, mapperInfo, methodInfoMap);
        List<MethodInfo> methodInfoList = new ArrayList(20);
        methodInfoMap.forEach((methodName, methodInfo) -> {
            String namespaceMethodName = String.format("%s.%s", mapperInfo.getNamespace(), methodName);
            MethodInfoContextHolder.set(namespaceMethodName, methodInfo);
            methodInfoList.add(methodInfo);
        });
        return methodInfoList;
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

            List<MethodParamInfo> methodParamInfoList = this.getMethodParamList(mapperInfo, method);
            MethodReturnInfo methodReturnInfo = this.getMethodReturn(mapperInfo, method);

            MethodInfo methodInfo = new MethodInfo();
            methodInfo.setMethod(method);
            methodInfo.setMethodName(methodName);
            methodInfo.setDynamic(method.getAnnotation(Dynamic.class) != null);
            methodInfo.setBatch(method.getAnnotation(BatchOperation.class) != null);
            methodInfo.setMethodParamInfoList(methodParamInfoList);
            methodInfo.setMethodReturnInfo(methodReturnInfo);

            // 方法名解析
            this.methodNameParse(mapperInfo.getEntityInfo(), methodInfo, methodDeclaringClass);
            // 方法条件解析
            ConditionGroup conditionGroup = method.getAnnotation(ConditionGroup.class);
            if (conditionGroup != null) {
                methodInfo.setConditionGroupExpression(conditionGroup.value());
                this.queryConditionParse(mapperInfo.getEntityInfo(), methodInfo);
            }

            this.entityRelationTreeHandler.execute(mapperInfo, methodInfo);
            String resultMapId = resultMapInfoHandler.execute(mapperInfo, methodInfo);
            methodInfo.setResultMapId(resultMapId);

            this.handleConditionParamInfo(methodInfo);
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
    private List<MethodParamInfo> getMethodParamList(MapperInfo mapperInfo, Method method) {
        // TODO 方法参数处理，批量操作的参数处理需要单独逻辑
        BatchOperation batchOperation = method.getAnnotation(BatchOperation.class);
        Parameter[] parameters = method.getParameters();
        List<MethodParamInfo> methodParamInfoList = new ArrayList<>();
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            Class<?> methodParamType = this.getMethodParamType(mapperInfo, parameter);

            MethodParamInfo methodParamInfo = new MethodParamInfo();
            methodParamInfo.setIndex(i);
            methodParamInfo.setType(methodParamType);
            methodParamInfo.setTypeName(methodParamType.getName());
            this.getMethodParamName(methodParamInfo, parameter);

            BatchData batchData = parameter.getAnnotation(BatchData.class);
            if (batchOperation != null && batchData != null) {
                methodParamInfo.setBatchData(true);
                methodParamInfo.setBatchItemName(batchData.value());
            }
            BatchSize batchSize = parameter.getAnnotation(BatchSize.class);
            if (batchOperation != null && batchSize != null) {
                methodParamInfo.setBatchSize(true);
            }

            Boolean basicType = this.getBasicType(methodParamType);
            methodParamInfo.setBasicType(basicType);
            if (!basicType && methodParamType != Map.class) {
                // 获取实体管理器中是否方法参数类型，如果不存在，使用字段处理器对方法参数类型进行字段处理
                EntityInfo entityInfo = EntityInfoContextHolder.get(methodParamType);
                List<ColumnInfo> columnInfoList;
                if (entityInfo != null) {
                    columnInfoList = entityInfo.getColumnInfoList();
                } else {
                    Map<Type, Class<?>> typeParameterMap = mapperInfo.getEntityInfo().getTypeParameterMap();
                    columnInfoList = columnInfoHandler.getColumnInfoList(methodParamType, typeParameterMap);
                }
                methodParamInfo.setColumnInfoList(columnInfoList);
            }
            Class<?> collectionType = this.getCollectionType(parameter.getType());
            if (collectionType != null) {
                methodParamInfo.setCollectionType(collectionType);
                methodParamInfo.setCollectionTypeName(collectionType.getTypeName());
            }

            methodParamInfoList.add(methodParamInfo);
        }
        return methodParamInfoList;
    }

    private MethodReturnInfo getMethodReturn(MapperInfo mapperInfo, Method method) {
        Class<?> methodReturnType = this.getMethodReturnType(mapperInfo, method);
        Boolean basicType = this.getBasicType(methodReturnType);

        MethodReturnInfo methodReturnInfo = new MethodReturnInfo();
        methodReturnInfo.setBasicType(basicType);
        methodReturnInfo.setType(methodReturnType);
        methodReturnInfo.setTypeName(methodReturnType.getName());
        if (!basicType && methodReturnType != Map.class) {
            Map<Type, Class<?>> typeParameterMap = mapperInfo.getEntityInfo().getTypeParameterMap();
            List<ColumnInfo> columnInfoList = columnInfoHandler.getColumnInfoList(methodReturnType, typeParameterMap);
            methodReturnInfo.setColumnInfoList(columnInfoList);
        }
        Class<?> collectionType = this.getCollectionType(methodReturnType);
        if (collectionType != null) {
            methodReturnInfo.setCollectionType(collectionType);
            methodReturnInfo.setCollectionTypeName(collectionType.getTypeName());
        }

        return methodReturnInfo;
    }

    /**
     * 条件实体只有非SimpleDao方法才会处理，SimpleDao只有findOne、findList会特殊处理。
     * 一个方法只允许定义一个条件实体，如果定义多个，只取最后一个条件实体。条件实体会把所有的方法名条件覆盖掉。
     * @param entityInfo
     * @param methodInfo
     * @param methodDeclaringClass
     */
    public void methodNameParse(EntityInfo entityInfo, MethodInfo methodInfo, Class<?> methodDeclaringClass) {
        methodNameAstHandler.execute(entityInfo, methodInfo, null, ConditionOriginType.METHOD_NAME, methodInfo.getMethodName());
        if (methodDeclaringClass == SimpleDao.class) {
            String methodName = methodInfo.getMethodName();
            if ("findOne".equals(methodName) || "findList".equals(methodName)) {
                List<MethodParamInfo> methodParamInfoList = methodInfo.getMethodParamInfoList();
                for (MethodParamInfo methodParamInfo : methodParamInfoList) {
                    Entity entity = methodParamInfo.getType().getAnnotation(Entity.class);
                    if (entity != null) {
                        String entityCondition = this.entityCondition(methodInfo, methodParamInfo);
                        methodNameAstHandler.execute(entityInfo, methodInfo, methodParamInfo, ConditionOriginType.ENTITY_FIELD, entityCondition);
                    }
                }
            }
        } else {
            List<MethodParamInfo> methodParamInfoList = methodInfo.getMethodParamInfoList();
            for (MethodParamInfo methodParamInfo : methodParamInfoList) {
                ConditionEntity isConditionEntity = methodParamInfo.getType().getAnnotation(ConditionEntity.class);
                if (isConditionEntity != null) {
                    String queryEntityCondition = this.entityCondition(methodInfo, methodParamInfo);
                    methodNameAstHandler.execute(entityInfo, methodInfo, methodParamInfo, ConditionOriginType.ENTITY_FIELD, queryEntityCondition);
                }
            }
        }
    }

    public void queryConditionParse(EntityInfo entityInfo, MethodInfo methodInfo) {
        this.methodNameAstHandler.execute(entityInfo, methodInfo, null, ConditionOriginType.METHOD_NAME, methodInfo.getConditionGroupExpression());
    }

    /**
     * 把实体字段转换成方法名
     *
     * @param methodInfo
     * @param methodParamInfo
     * @return
     */
    private String entityCondition(MethodInfo methodInfo, MethodParamInfo methodParamInfo) {
        StringBuilder stringBuilder = new StringBuilder(methodInfo.getSqlCommandType().name().toLowerCase()).append("By");
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
        this.bindConditionParam(methodInfo, conditionInfoList);
    }

    /**
     * 绑定和条件和参数
     *
     * @param methodInfo
     * @param conditionInfoList
     */
    private void bindConditionParam(MethodInfo methodInfo, List<ConditionInfo> conditionInfoList) {
        for (ConditionInfo conditionInfo : conditionInfoList) {
            // 条件来自于实体字段，就无法和参数进行绑定
            if (conditionInfo.getConditionOriginType() == ConditionOriginType.ENTITY_FIELD) {
                continue;
            }
            ConditionGroupInfo conditionGroupInfo = conditionInfo.getConditionGroupInfo();
            if (conditionGroupInfo != null) {
                this.bindConditionParam(methodInfo, conditionGroupInfo.getConditionInfoList());
            } else {
                // 处理查询条件和参数之间的关系，查询条件和参数之间是1对1关系，不要设计一对多关系，后续绑定参数很难处理
                Integer index = conditionInfo.getIndex();
                String conditionColumnName = conditionInfo.getColumnName();

                // 采用4种方式获取参数：argx -> conditionName -> conditionName.toLowerCase() -> paramx：【arg0 -> userName -> username -> param1】
                MethodParamInfo methodParamInfo = methodInfo.getMethodParamInfo("arg" + index);
                if (methodParamInfo == null) {
                    methodParamInfo = methodInfo.getMethodParamInfo(conditionColumnName);
                }
                if (methodParamInfo == null) {
                    methodParamInfo = methodInfo.getMethodParamInfo(conditionColumnName.toLowerCase());
                }
                if (methodParamInfo == null) {
                    methodParamInfo = methodInfo.getMethodParamInfo("param" + (index + 1));
                }
                if (methodParamInfo == null) {
                    // TODO 这里还需校验条件实体的参数    暂时先不校验
                    // throw new RuntimeException("查询条件没有对应的参数");
                    continue;
                }
                conditionInfo.setMethodParamInfo(methodParamInfo);
            }
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

            /*String javaColumnName = conditionInfo.getJavaColumnName();
            javaColumnName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, javaColumnName);
            ColumnInfo columnInfo = resultMapInfo.getColumnInfo(javaColumnName);
            if (columnInfo == null) {
                throw new RuntimeException("方法名中的字段在实体类中不存在: " + javaColumnName);
            }
            conditionInfo.setDbColumnName(columnInfo.getDbColumnName());
            conditionInfo.setJavaColumnName(methodParamInfo.getParamName());*/
        }
    }

    private Class<?> getMethodParamType(MapperInfo mapperInfo, Parameter parameter) {
        if (TypeUtils.isAssignable(parameter.getType(), Map.class)) {
            return Map.class;
        }
        Type type = parameter.getParameterizedType();
        return getMethodType(mapperInfo, type);
    }

    private void getMethodParamName(MethodParamInfo methodParamInfo, Parameter parameter) {
        String argName = parameter.getName();
        Param param = parameter.getAnnotation(Param.class);
        if (param != null) {
            argName = param.value();
        }
        methodParamInfo.setArgName(argName);
        methodParamInfo.setParamName("param" + (methodParamInfo.getIndex() + 1));
        methodParamInfo.setParam(param);
    }

    private Class<?> getMethodReturnType(MapperInfo mapperInfo, Method method) {
        if (TypeUtils.isAssignable(method.getReturnType(), Map.class)) {
            return Map.class;
        }
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
