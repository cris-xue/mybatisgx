package com.mybatisgx.model.handler;

import com.google.common.base.CaseFormat;
import com.mybatisgx.annotation.*;
import com.mybatisgx.context.EntityInfoContextHolder;
import com.mybatisgx.context.MethodInfoContextHolder;
import com.mybatisgx.dao.Dao;
import com.mybatisgx.dao.SimpleDao;
import com.mybatisgx.model.*;
import com.mybatisgx.utils.TypeUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.mapping.SqlCommandType;
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

    /*private static final List<Class<?>> SIMPLE_TYPE_LIST = new ArrayList<>();

    static {
        SIMPLE_TYPE_LIST.add(int.class);
        SIMPLE_TYPE_LIST.add(Integer.class);
        SIMPLE_TYPE_LIST.add(Long.class);
        SIMPLE_TYPE_LIST.add(String.class);
        SIMPLE_TYPE_LIST.add(Double.class);
        SIMPLE_TYPE_LIST.add(Boolean.class);
        SIMPLE_TYPE_LIST.add(Date.class);
        SIMPLE_TYPE_LIST.add(LocalDate.class);
        SIMPLE_TYPE_LIST.add(LocalDateTime.class);
    }*/

    private ColumnInfoHandler columnInfoHandler = new ColumnInfoHandler();
    private ColumnTypeHandler columnTypeHandler = new ColumnTypeHandler();
    private MethodNameAstHandler methodNameAstHandler = new MethodNameAstHandler();
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
        for (Type type : superInterfaces) {
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

            MethodParamContext methodParamContext = this.getMethodParam(mapperInfo, method);
            MethodReturnInfo methodReturnInfo = this.getMethodReturn(mapperInfo, method);

            MethodInfo methodInfo = new MethodInfo();
            methodInfo.setMethod(method);
            methodInfo.setMethodName(methodName);
            methodInfo.setDynamic(method.getAnnotation(Dynamic.class) != null);
            methodInfo.setBatch(method.getAnnotation(BatchOperation.class) != null);
            methodInfo.setEntityParamInfo(methodParamContext.getEntityParamInfo());
            methodInfo.setMethodParamInfoList(methodParamContext.getMethodParamInfoList());
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

            this.bindConditionParam(methodInfo, methodInfo.getConditionInfoList());
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
    private MethodParamContext getMethodParam(MapperInfo mapperInfo, Method method) {
        BatchOperation batchOperation = method.getAnnotation(BatchOperation.class);
        Parameter[] parameters = method.getParameters();
        MethodParamInfo entityParamInfo = null;
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

            ClassCategory classCategory = this.getBasicType(methodParamType);
            methodParamInfo.setClassCategory(classCategory);
            if (classCategory == ClassCategory.COMPLEX && methodParamType != Map.class) {
                IdClass idClass = methodParamType.getAnnotation(IdClass.class);
                if (idClass != null) {
                    Map<Type, Class<?>> typeParameterMap = mapperInfo.getEntityInfo().getTypeParameterMap();
                    List<ColumnInfo> columnInfoList = columnInfoHandler.getColumnInfoList(methodParamType, typeParameterMap);
                    methodParamInfo.setColumnInfoList(columnInfoList);
                }
                // 获取实体管理器中是否方法参数类型，如果不存在，使用字段处理器对方法参数类型进行字段处理
                Entity entity = methodParamType.getAnnotation(Entity.class);
                if (entity != null) {
                    EntityInfo entityInfo = EntityInfoContextHolder.get(methodParamType);
                    List<ColumnInfo> columnInfoList = entityInfo.getColumnInfoList();
                    methodParamInfo.setColumnInfoList(columnInfoList);
                    entityParamInfo = methodParamInfo;
                }
                QueryEntity queryEntity = methodParamType.getAnnotation(QueryEntity.class);
                if (queryEntity != null) {
                    Map<Type, Class<?>> typeParameterMap = mapperInfo.getEntityInfo().getTypeParameterMap();
                    List<ColumnInfo> columnInfoList = columnInfoHandler.getColumnInfoList(methodParamType, typeParameterMap);
                    methodParamInfo.setColumnInfoList(columnInfoList);
                    entityParamInfo = methodParamInfo;
                }
            }
            Class<?> collectionType = this.getCollectionType(parameter.getType());
            if (collectionType != null) {
                methodParamInfo.setCollectionType(collectionType);
                methodParamInfo.setCollectionTypeName(collectionType.getTypeName());
            }

            methodParamInfoList.add(methodParamInfo);
        }
        return new MethodParamContext(entityParamInfo, methodParamInfoList);
    }

    private MethodReturnInfo getMethodReturn(MapperInfo mapperInfo, Method method) {
        Class<?> methodReturnType = this.getMethodReturnType(mapperInfo, method);
        ClassCategory classCategory = this.getBasicType(methodReturnType);

        MethodReturnInfo methodReturnInfo = new MethodReturnInfo();
        methodReturnInfo.setClassCategory(classCategory);
        methodReturnInfo.setType(methodReturnType);
        methodReturnInfo.setTypeName(methodReturnType.getName());
        if (classCategory == ClassCategory.COMPLEX && methodReturnType != Map.class) {
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
     * 实体作为条件只支持查询方法，修改和删除不支持。
     * @param entityInfo
     * @param methodInfo
     * @param methodDeclaringClass
     */
    public void methodNameParse(EntityInfo entityInfo, MethodInfo methodInfo, Class<?> methodDeclaringClass) {
        methodNameAstHandler.execute(entityInfo, methodInfo, null, ConditionOriginType.METHOD_NAME, methodInfo.getMethodName());
        if (methodInfo.getSqlCommandType() != SqlCommandType.SELECT) {
            return;
        }
        if (methodDeclaringClass == SimpleDao.class) {
            String methodName = methodInfo.getMethodName();
            if ("findOne".equals(methodName) || "findList".equals(methodName)) {
                MethodParamInfo entityParamInfo = methodInfo.getEntityParamInfo();
                if (entityParamInfo != null) {
                    String entityCondition = this.entityCondition(methodInfo, entityParamInfo);
                    methodNameAstHandler.execute(entityInfo, methodInfo, entityParamInfo, ConditionOriginType.ENTITY_FIELD, entityCondition);
                }
            }
        } else {
            MethodParamInfo entityParamInfo = methodInfo.getEntityParamInfo();
            if (entityParamInfo != null) {
                String queryEntityCondition = this.entityCondition(methodInfo, entityParamInfo);
                methodNameAstHandler.execute(entityInfo, methodInfo, entityParamInfo, ConditionOriginType.ENTITY_FIELD, queryEntityCondition);
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
        List<String> columnConditionList = new ArrayList<>();
        for (ColumnInfo columnInfo : methodParamInfo.getColumnInfoList()) {
            if (TypeUtils.typeEquals(columnInfo, RelationColumnInfo.class)) {
                continue;
            }
            String javaColumnName = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, columnInfo.getJavaColumnName());
            columnConditionList.add(javaColumnName);
        }
        StringBuilder stringBuilder = new StringBuilder(methodInfo.getSqlCommandType().name().toLowerCase())
                .append("By")
                .append(StringUtils.join(columnConditionList, "And"));
        LOGGER.debug(stringBuilder.toString());
        return stringBuilder.toString();
    }

    /**
     * 绑定和条件和参数
     *
     * @param methodInfo
     * @param conditionInfoList
     */
    private void bindConditionParam(MethodInfo methodInfo, List<ConditionInfo> conditionInfoList) {
        for (ConditionInfo conditionInfo : conditionInfoList) {
            ConditionGroupInfo conditionGroupInfo = conditionInfo.getConditionGroupInfo();
            if (conditionGroupInfo != null) {
                this.bindConditionParam(methodInfo, conditionGroupInfo.getConditionInfoList());
            } else {
                // 处理查询条件和参数之间的关系，查询条件和参数之间是1对1关系，不要设计一对多关系，后续绑定参数很难处理
                String conditionColumnName = conditionInfo.getColumnName();
                MethodParamInfo entityParamInfo = methodInfo.getEntityParamInfo();
                if (entityParamInfo != null) {
                    // 如果存在条件实体，则把条件实体字段转换成参数名称
                    Param param = entityParamInfo.getParam();
                    ColumnInfo columnInfo = entityParamInfo.getColumnInfo(conditionColumnName);
                    List<ColumnInfo> composites = columnInfo.getComposites();
                    ClassCategory classCategory = ObjectUtils.isEmpty(composites) ? ClassCategory.SIMPLE : ClassCategory.COMPLEX;

                    MethodParamInfo methodParamInfo = new MethodParamInfo();
                    methodParamInfo.setClassCategory(classCategory);
                    methodParamInfo.setType(columnInfo.getJavaType());
                    methodParamInfo.setCollectionType(columnInfo.getCollectionType());
                    if (ObjectUtils.isNotEmpty(composites)) {
                        methodParamInfo.setColumnInfoList(composites);
                    }

                    List<String> paramValueCommonPathItemList = new ArrayList();
                    if (methodInfo.getMethodParamInfoList().size() == 1 && param == null) {
                        paramValueCommonPathItemList.add(columnInfo.getJavaColumnName());
                    } else {
                        paramValueCommonPathItemList.add(entityParamInfo.getArgName());
                        paramValueCommonPathItemList.add(columnInfo.getJavaColumnName());
                    }

                    // 校验条件是否可以关联到参数，如果无法关联，后续执行数据库操作会报错
                    if (methodParamInfo == null) {
                        throw new RuntimeException("查询条件没有对应的参数");
                    }
                    conditionInfo.setParamValueCommonPathItemList(paramValueCommonPathItemList);
                    conditionInfo.setMethodParamInfo(methodParamInfo);
                } else {
                    // 采用4种方式获取参数：conditionName -> conditionName.toLowerCase() -> argx -> paramx：【userName -> username -> arg0 -> param1】
                    MethodParamInfo methodParamInfo = methodInfo.getMethodParamInfo(conditionColumnName);
                    if (methodParamInfo == null) {
                        methodParamInfo = methodInfo.getMethodParamInfo(conditionColumnName.toLowerCase());
                    }
                    if (methodParamInfo == null) {
                        String argName = String.format("arg%1$s", conditionInfo.getIndex());
                        methodParamInfo = methodInfo.getMethodParamInfo(argName);
                    }
                    /*if (methodParamInfo == null) {
                        methodParamInfo = methodInfo.getMethodParamInfo("param" + (index + 1));
                    }*/

                    // 校验条件是否可以关联到参数，如果无法关联，后续执行数据库操作会报错
                    if (methodParamInfo == null) {
                        throw new RuntimeException("查询条件没有对应的参数");
                    }

                    Param param = methodParamInfo.getParam();
                    List<String> paramValueCommonPathItemList = new ArrayList();
                    if (methodInfo.getMethodParamInfoList().size() == 1) {
                        List<ColumnInfo> columnInfoList = methodParamInfo.getColumnInfoList();
                        if (ObjectUtils.isEmpty(columnInfoList)) {
                            // findById(Long id)   findById(@Param("id") Long id)
                            paramValueCommonPathItemList.add(methodParamInfo.getArgName());
                        } else {
                            // findById(ComplexId complexId)    findById(@Param("id") ComplexId complexId)
                            if (param == null) {

                            } else {
                                paramValueCommonPathItemList.add(methodParamInfo.getArgName());
                            }
                        }
                    }
                    if (methodInfo.getMethodParamInfoList().size() > 1) {
                        List<ColumnInfo> columnInfoList = methodParamInfo.getColumnInfoList();
                        if (ObjectUtils.isEmpty(columnInfoList)) {
                            // findByIdAndName(Long id, String name)   findByIdAndName(@Param("id") Long id, String name)
                            paramValueCommonPathItemList.add(methodParamInfo.getArgName());
                        } else {
                            // findByIdAndName(ComplexId complexId, String name)    findByIdAndName(@Param("id") ComplexId complexId, String name)
                            paramValueCommonPathItemList.add(methodParamInfo.getArgName());
                        }
                    }

                    conditionInfo.setParamValueCommonPathItemList(paramValueCommonPathItemList);
                    conditionInfo.setMethodParamInfo(methodParamInfo);
                }
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
        Type actualType = TypeUtils.getGenericType(type);
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

    public ClassCategory getBasicType(Type type) {
        return columnTypeHandler.getClassCategory(type);
    }

    private static class MethodParamContext {

        private MethodParamInfo entityParamInfo;

        private List<MethodParamInfo> methodParamInfoList;

        public MethodParamContext(MethodParamInfo entityParamInfo, List<MethodParamInfo> methodParamInfoList) {
            this.entityParamInfo = entityParamInfo;
            this.methodParamInfoList = methodParamInfoList;
        }

        public MethodParamInfo getEntityParamInfo() {
            return entityParamInfo;
        }

        public void setEntityParamInfo(MethodParamInfo entityParamInfo) {
            this.entityParamInfo = entityParamInfo;
        }

        public List<MethodParamInfo> getMethodParamInfoList() {
            return methodParamInfoList;
        }

        public void setMethodParamInfoList(List<MethodParamInfo> methodParamInfoList) {
            this.methodParamInfoList = methodParamInfoList;
        }
    }
}
