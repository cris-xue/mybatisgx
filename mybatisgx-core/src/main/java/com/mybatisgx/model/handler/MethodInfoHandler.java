package com.mybatisgx.model.handler;

import com.google.common.base.CaseFormat;
import com.google.common.collect.Lists;
import com.mybatisgx.annotation.*;
import com.mybatisgx.context.EntityInfoContextHolder;
import com.mybatisgx.context.MethodInfoContextHolder;
import com.mybatisgx.dao.Dao;
import com.mybatisgx.exception.MethodNotConditionException;
import com.mybatisgx.exception.MybatisgxException;
import com.mybatisgx.ext.session.MybatisgxConfiguration;
import com.mybatisgx.model.*;
import com.mybatisgx.utils.TypeUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.mapping.SqlCommandType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.*;
import java.util.*;

/**
 * @author ：薛承城
 * @description：方法处理器
 * @date ：2023/12/1
 */
public class MethodInfoHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodInfoHandler.class);

    private ColumnInfoHandler columnInfoHandler = new ColumnInfoHandler();
    private ColumnTypeHandler columnTypeHandler = new ColumnTypeHandler();
    private MybatisgxSyntaxProcessor mybatisgxSyntaxProcessor = new MybatisgxSyntaxProcessor();
    private EntityRelationTreeHandler entityRelationTreeHandler = new EntityRelationTreeHandler();
    private ResultMapInfoHandler resultMapInfoHandler = new ResultMapInfoHandler();
    private MybatisgxConfiguration configuration;

    public MethodInfoHandler(MybatisgxConfiguration configuration) {
        this.configuration = configuration;
    }

    public List<MethodInfo> execute(MapperInfo mapperInfo, Class<?> interfaceClass) {
        List<Method> methodList = this.getDaoMethodList(interfaceClass);
        Map<String, MethodInfo> methodInfoMap = this.processMethod(methodList, mapperInfo);
        List<MethodInfo> methodInfoList = new ArrayList(20);
        for (MethodInfo methodInfo : methodInfoMap.values()) {
            String namespaceMethodName = this.getNamespaceMethodName(mapperInfo, methodInfo.getMethodName());
            MethodInfoContextHolder.set(namespaceMethodName, methodInfo);
            methodInfoList.add(methodInfo);
        }
        return methodInfoList;
    }

    private List<Method> getDaoMethodList(Class<?> daoClass) {
        List<Method> totalMethodList = Lists.newArrayList(daoClass.getDeclaredMethods());
        for (Type type : daoClass.getGenericInterfaces()) {
            if (type instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) type;
                Class<?> superInterface = (Class<?>) parameterizedType.getRawType();
                if (ClassUtils.isAssignable(superInterface, Dao.class)) {
                    List<Method> methodList = this.getDaoMethodList(superInterface);
                    if (ObjectUtils.isNotEmpty(methodList)) {
                        totalMethodList.addAll(methodList);
                    }
                }
            }
        }
        return totalMethodList;
    }

    private Map<String, MethodInfo> processMethod(List<Method> methodList, MapperInfo mapperInfo) {
        Map<String, MethodInfo> methodInfoMap = new LinkedHashMap<>();
        for (Method method : methodList) {
            // 忽略default方法，default本质上已经有实现，不需要自动处理，这里也主要是为了支持批量操作的重载，默认可以不传参数。
            if (this.isIgnoredMethod(method)) {
                continue;
            }
            String methodName = method.getName();
            if (methodInfoMap.containsKey(methodName)) {
                throw new MybatisgxException("dao接口方法无法重载，请修改方法名: %s", methodName);
            }
            String namespaceMethodName = this.getNamespaceMethodName(mapperInfo, methodName);
            if (this.configuration.hasStatement(namespaceMethodName)) {
                LOGGER.debug("方法{}已在mapper存在，无需处理该方法！", namespaceMethodName);
                continue;
            }

            SqlCommandType sqlCommandType = this.mybatisgxSyntaxProcessor.getSqlCommandType(methodName);
            MethodParamContext methodParamContext = this.getMethodParam(mapperInfo, method, sqlCommandType);
            MethodReturnInfo methodReturnInfo = this.getMethodReturn(mapperInfo, method);

            MethodInfo methodInfo = new MethodInfo();
            methodInfo.setMapperInfo(mapperInfo);
            methodInfo.setMethod(method);
            methodInfo.setMethodName(methodName);
            methodInfo.setSqlCommandType(sqlCommandType);
            methodInfo.setDynamic(method.getAnnotation(Dynamic.class) != null);
            methodInfo.setBatch(method.getAnnotation(BatchOperation.class) != null);
            methodInfo.setEntityParamInfo(methodParamContext.getEntityParamInfo());
            methodInfo.setQueryEntityParamInfo(methodParamContext.getQueryEntityParamInfo());
            methodInfo.setMethodParamInfoList(methodParamContext.getMethodParamInfoList());
            methodInfo.setMethodReturnInfo(methodReturnInfo);

            // 条件解析
            this.methodConditionParse(methodInfo);

            SelectItemInfo selectItemInfo = methodInfo.getSelectItemInfo();
            if (methodInfo.getSqlCommandType() == SqlCommandType.SELECT
                    && selectItemInfo.getSelectItemType() == SelectItemType.COLUMN) {
                this.entityRelationTreeHandler.execute(mapperInfo, methodInfo);
                String resultMapId = resultMapInfoHandler.execute(mapperInfo, methodInfo);
                methodInfo.setResultMapId(resultMapId);
            }

            this.bindConditionParam(mapperInfo, methodInfo, methodInfo.getConditionInfoList());
            methodInfoMap.put(methodName, methodInfo);
        }
        return methodInfoMap;
    }

    /**
     * findByIds(List)
     *
     * @param mapperInfo
     * @param method
     * @param sqlCommandType
     * @return
     */
    private MethodParamContext getMethodParam(MapperInfo mapperInfo, Method method, SqlCommandType sqlCommandType) {
        Parameter[] parameters = method.getParameters();
        int parameterCount = parameters.length;
        MethodParamInfo entityParamInfo = null;
        MethodParamInfo queryEntityParamInfo = null;
        List<MethodParamInfo> methodParamInfoList = new ArrayList<>();
        for (int i = 0; i < parameterCount; i++) {
            Parameter parameter = parameters[i];
            Class<?> methodParamType = this.getMethodParamType(mapperInfo, parameter);
            TypeCategory typeCategory = this.getClassCategory(methodParamType);

            MethodParamInfo methodParamInfo = new MethodParamInfo();
            methodParamInfo.setIndex(i);
            methodParamInfo.setType(methodParamType);
            methodParamInfo.setTypeName(methodParamType.getName());
            this.handleMethodParamName(methodParamInfo, parameter, parameterCount, typeCategory);
            this.handleMethodCollectionTypeParam(parameter, methodParamInfo);
            this.handleBatchOperation(method, parameter, methodParamInfo);

            methodParamInfo.setClassCategory(typeCategory);
            if (typeCategory == TypeCategory.COMPLEX && methodParamType != Map.class) {
                if (methodParamType.getAnnotation(IdClass.class) != null) {
                    Map<Type, Class<?>> typeParameterMap = mapperInfo.getEntityInfo().getTypeParameterMap();
                    List<ColumnInfo> columnInfoList = columnInfoHandler.getColumnInfoList(methodParamType, typeParameterMap);
                    methodParamInfo.setColumnInfoList(columnInfoList);
                }
                // 获取实体管理器中是否方法参数类型，如果不存在，使用字段处理器对方法参数类型进行字段处理
                if (methodParamType.getAnnotation(Entity.class) != null) {
                    methodParamInfo.setEntityInfo(mapperInfo.getEntityInfo());
                    if (entityParamInfo == null) {
                        entityParamInfo = methodParamInfo;
                    } else {
                        throw new MybatisgxException("%s 方法实体参数存在多个", method.getName());
                    }
                }
                if (methodParamType.getAnnotation(QueryEntity.class) != null) {
                    // 如果继承CurdDao的时候MapperInfo中会缺失查询实体信息，这里不需要校验查询实体是否和实体对应，最终会在validatorMethodParamEntity方法中校验
                    methodParamInfo.setEntityInfo(EntityInfoContextHolder.get(methodParamType));
                    if (queryEntityParamInfo == null) {
                        queryEntityParamInfo = methodParamInfo;
                    } else {
                        throw new MybatisgxException("%s 方法查询实体参数存在多个", method.getName());
                    }
                }
            }

            methodParamInfoList.add(methodParamInfo);
        }
        this.validatorMethodParamEntity(mapperInfo, method, sqlCommandType, entityParamInfo, queryEntityParamInfo);
        return new MethodParamContext(entityParamInfo, queryEntityParamInfo, methodParamInfoList);
    }

    private void handleBatchOperation(Method method, Parameter parameter, MethodParamInfo methodParamInfo) {
        BatchOperation batchOperation = method.getAnnotation(BatchOperation.class);
        BatchData batchData = parameter.getAnnotation(BatchData.class);
        if (batchOperation != null && batchData != null) {
            methodParamInfo.setBatchData(true);
            methodParamInfo.setBatchItemName(batchData.value());
        }
        BatchSize batchSize = parameter.getAnnotation(BatchSize.class);
        if (batchOperation != null && batchSize != null) {
            methodParamInfo.setBatchSize(true);
        }
    }

    private void handleMethodCollectionTypeParam(Parameter parameter, MethodParamInfo methodParamInfo) {
        Class<?> collectionType = this.getCollectionType(parameter.getType());
        if (collectionType != null) {
            methodParamInfo.setCollectionType(collectionType);
            methodParamInfo.setCollectionTypeName(collectionType.getTypeName());
        }
    }

    /**
     * 实体参数定义如下：
     * 新增方法：只允许存在操作实体参数
     * 删除方法：允许存在操作实体参数和查询实体参数，但只能存在一个，两个同时存在报错
     * 修改方法：允许存在操作实体参数和查询实体参数，两个实体参数可以同时存在，操作实体参数可以单独存在，但查询实体参数不能单独存在
     * 查询方法：允许存在操作实体参数和查询实体参数，但只能存在一个，两个同时存在报错
     *
     * @param mapperInfo
     * @param method
     * @param sqlCommandType
     * @param entityParamInfo
     * @param queryEntityParamInfo
     */
    private void validatorMethodParamEntity(MapperInfo mapperInfo, Method method, SqlCommandType sqlCommandType, MethodParamInfo entityParamInfo, MethodParamInfo queryEntityParamInfo) {
        if (sqlCommandType == SqlCommandType.INSERT) {
            if (entityParamInfo == null) {
                throw new MybatisgxException("%s 方法实体参数不存在", method.getName());
            }
            if (queryEntityParamInfo != null) {
                throw new MybatisgxException("%s 方法查询实体参数不允许存在", method.getName());
            }
        }
        if (sqlCommandType == SqlCommandType.UPDATE) {
            if (entityParamInfo == null) {
                if (queryEntityParamInfo == null) {
                    throw new MybatisgxException("%s 方法实体参数不存在", method.getName());
                }
                if (queryEntityParamInfo != null) {
                    throw new MybatisgxException("%s 方法查询实体参数不允许单独存在", method.getName());
                }
            }
        }
        if (sqlCommandType == SqlCommandType.DELETE || sqlCommandType == SqlCommandType.SELECT) {
            if (entityParamInfo != null && queryEntityParamInfo != null) {
                throw new MybatisgxException("%s 方法实体参数和查询实体参数不允许同时存在", method.getName());
            }
        }
        if (entityParamInfo != null && entityParamInfo.getType() != mapperInfo.getEntityClass()) {
            throw new MybatisgxException("%s 方法实体参数和mapper定义的实体参数类型不一致", method.getName());
        }
        if (queryEntityParamInfo != null && queryEntityParamInfo.getType().getAnnotation(QueryEntity.class).value() != mapperInfo.getEntityClass()) {
            throw new MybatisgxException("%s 方法查询实体参数和mapper定义的实体参数类型不一致", method.getName());
        }
    }

    private MethodReturnInfo getMethodReturn(MapperInfo mapperInfo, Method method) {
        Class<?> methodReturnType = this.getMethodReturnType(mapperInfo, method);
        TypeCategory typeCategory = this.getClassCategory(methodReturnType);

        MethodReturnInfo methodReturnInfo = new MethodReturnInfo();
        methodReturnInfo.setClassCategory(typeCategory);
        methodReturnInfo.setType(methodReturnType);
        methodReturnInfo.setTypeName(methodReturnType.getName());
        if (typeCategory == TypeCategory.COMPLEX && methodReturnType != Map.class) {
            List<ColumnInfo> columnInfoList = Collections.emptyList();
            if (methodReturnType == mapperInfo.getEntityClass()) {
                columnInfoList = mapperInfo.getEntityInfo().getColumnInfoList();
            }
            if (methodReturnType != mapperInfo.getEntityClass()) {
                Map<Type, Class<?>> typeParameterMap = mapperInfo.getEntityInfo().getTypeParameterMap();
                columnInfoList = columnInfoHandler.getColumnInfoList(methodReturnType, typeParameterMap);
            }
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
     * 条件实体只有非SimpleDao、SelectDao、CurdDao方法才会处理，SelectDao只有findOne、findList、findPage会特殊处理。
     * 查询实体作为条件支持查询、修改、删除。
     * 解析优先级：Statement > 实体条件 > 方法名
     *
     * @param methodInfo
     */
    public void methodConditionParse(MethodInfo methodInfo) {
        if (methodInfo.getSqlCommandType() == SqlCommandType.INSERT) {
            return;
        }
        EntityInfo entityInfo = methodInfo.getMapperInfo().getEntityInfo();

        // 解析Statement表达式
        if (methodInfo.getSqlCommandType() == SqlCommandType.SELECT) {
            Statement statement = methodInfo.getMethod().getAnnotation(Statement.class);
            if (statement != null) {
                methodInfo.setStatementExpression(statement.value());
                this.mybatisgxSyntaxProcessor.execute(entityInfo, methodInfo, null, ConditionOriginType.STATEMENT_METHOD_NAME, methodInfo.getStatementExpression());
                return;
            }
        }

        // 解析查询实体生成的表达式，一个实体只允许定义一个查询实体
        if (methodInfo.getSqlCommandType() == SqlCommandType.SELECT) {
            if (methodInfo.getEntityParamInfo() != null && methodInfo.getQueryEntityParamInfo() == null) {
                entityInfo = methodInfo.getEntityParamInfo().getEntityInfo();
            }
            if (methodInfo.getEntityParamInfo() == null && methodInfo.getQueryEntityParamInfo() != null) {
                entityInfo = methodInfo.getQueryEntityParamInfo().getEntityInfo();
            }
            if (methodInfo.getEntityParamInfo() != null || methodInfo.getQueryEntityParamInfo() != null) {
                String entityCondition = this.getEntityCondition(methodInfo, entityInfo);
                this.mybatisgxSyntaxProcessor.execute(entityInfo, methodInfo, null, ConditionOriginType.ENTITY_FIELD, entityCondition);
                return;
            }
        }

        // 解析方法名
        this.mybatisgxSyntaxProcessor.execute(entityInfo, methodInfo, null, ConditionOriginType.METHOD_NAME, methodInfo.getMethodName());
    }

    /**
     * 把实体字段转换成方法名
     *
     * @param methodInfo
     * @param entityInfo
     * @return
     */
    private String getEntityCondition(MethodInfo methodInfo, EntityInfo entityInfo) {
        List<String> columnConditionList = new ArrayList<>();
        for (ColumnInfo columnInfo : entityInfo.getColumnInfoList()) {
            if (TypeUtils.typeEquals(columnInfo, RelationColumnInfo.class)) {
                RelationColumnInfo relationColumnInfo = (RelationColumnInfo) columnInfo;
                if (relationColumnInfo.getRelationType() == RelationType.MANY_TO_MANY) {
                    continue;
                }
                if (relationColumnInfo.getMappedByRelationColumnInfo() != null) {
                    continue;
                }
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
    private void bindConditionParam(MapperInfo mapperInfo, MethodInfo methodInfo, List<ConditionInfo> conditionInfoList) {
        if (methodInfo.getSqlCommandType() != SqlCommandType.INSERT && ObjectUtils.isEmpty(conditionInfoList)) {
            throw new MethodNotConditionException("%s.%s方法无条件", mapperInfo.getNamespace(), methodInfo.getMethodName());
        }
        for (ConditionInfo conditionInfo : conditionInfoList) {
            List<ConditionInfo> childConditionInfoList = conditionInfo.getConditionInfoList();
            if (ObjectUtils.isNotEmpty(childConditionInfoList)) {
                this.bindConditionParam(mapperInfo, methodInfo, childConditionInfoList);
            } else {
                if (conditionInfo.getComparisonOperator().isNullComparisonOperator()) {
                    continue;
                }
                // 处理查询条件和参数之间的关系，查询条件和参数之间是1对1关系，不要设计一对多关系，后续绑定参数很难处理
                // 条件优先级是    方法简单参数有@Param注解(id条件支持复合类型) > 方法简单参数有@Param注解全小写(id条件支持复合类型) > 实体字段 > 方法简单参数无@Param注解
                String conditionColumnName = conditionInfo.getColumnName();
                MethodParamInfo methodParamInfo = this.getSimpleTypeConditionParam(methodInfo, conditionInfo, conditionColumnName);
                if (methodParamInfo == null) {
                    methodParamInfo = this.getSimpleTypeConditionParam(methodInfo, conditionInfo, conditionColumnName.toLowerCase());
                }
                if (methodParamInfo == null) {
                    MethodParamInfo entityParamInfo = methodInfo.getEntityParamInfo();
                    if (entityParamInfo != null) {
                        methodParamInfo = this.getEntityTypeConditionParam(methodInfo, conditionInfo, entityParamInfo);
                    }
                    MethodParamInfo queryEntityParamInfo = methodInfo.getQueryEntityParamInfo();
                    if (queryEntityParamInfo != null) {
                        methodParamInfo = this.getEntityTypeConditionParam(methodInfo, conditionInfo, queryEntityParamInfo);
                    }
                }
                if (methodParamInfo == null) {
                    String argName = String.format("arg%1$s", conditionInfo.getIndex());
                    methodParamInfo = this.getSimpleTypeConditionParam(methodInfo, conditionInfo, argName);
                }
                // 校验条件是否可以关联到参数，如果无法关联，后续执行数据库操作会报错
                if (methodParamInfo == null) {
                    throw new MybatisgxException("%s方法条件没有对应的参数", methodInfo.getMethodName());
                }
                conditionInfo.setMethodParamInfo(methodParamInfo);
            }
        }
    }

    private MethodParamInfo getEntityTypeConditionParam(MethodInfo methodInfo, ConditionInfo conditionInfo, MethodParamInfo entityParamInfo) {
        // 如果存在条件实体，则把条件实体字段转换成参数名称
        String conditionColumnName = conditionInfo.getColumnName();
        ColumnInfo columnInfo = entityParamInfo.getEntityInfo().getColumnInfo(conditionColumnName);
        if (columnInfo == null) {
            return null;
        }

        List<ColumnInfo> composites = columnInfo.getComposites();
        TypeCategory typeCategory = ObjectUtils.isEmpty(composites) ? TypeCategory.SIMPLE : TypeCategory.COMPLEX;

        MethodParamInfo methodParamInfo = new MethodParamInfo();
        methodParamInfo.setClassCategory(typeCategory);
        methodParamInfo.setType(columnInfo.getJavaType());
        methodParamInfo.setCollectionType(columnInfo.getCollectionType());
        if (ObjectUtils.isNotEmpty(composites)) {
            methodParamInfo.setColumnInfoList(composites);
        }

        int paramCount = methodInfo.getMethodParamInfoList().size();
        Param param = entityParamInfo.getParam();
        List<String> paramValueCommonPathItemList = new ArrayList<>();
        if (paramCount == 1 && param == null) {
            // mybatis在[单参数、复合类型、无注解]情况下为了获取参数方便，不会对参数进行包装，所以不会生成argx这种参数
            paramValueCommonPathItemList.add(columnInfo.getJavaColumnName());
        } else {
            if (methodInfo.getBatch()) {
                // 批量操作条件
                paramValueCommonPathItemList.add(entityParamInfo.getBatchItemName());
                paramValueCommonPathItemList.add(columnInfo.getJavaColumnName());
            } else {
                paramValueCommonPathItemList.add(entityParamInfo.getArgName());
                paramValueCommonPathItemList.add(columnInfo.getJavaColumnName());
            }
        }
        methodParamInfo.setArgValueCommonPathItemList(paramValueCommonPathItemList);
        methodParamInfo.setWrapper(true);
        return methodParamInfo;
    }

    private MethodParamInfo getSimpleTypeConditionParam(MethodInfo methodInfo, ConditionInfo conditionInfo, String paramName) {
        // 采用3种方式获取参数：conditionName -> conditionName.toLowerCase() -> argx：【userName -> username -> arg0】
        MethodParamInfo methodParamInfo = methodInfo.getMethodParamInfo(paramName);
        // 校验条件是否可以关联到参数，如果无法关联，后续执行数据库操作会报错
        if (methodParamInfo == null) {
            return null;
        }
        if (methodParamInfo.getClassCategory() == TypeCategory.COMPLEX && TypeUtils.typeEquals(conditionInfo.getColumnInfo(), ColumnInfo.class)) {
            throw new MybatisgxException("%s查询条件不能关联到复杂类型参数%s", methodInfo.getMethodName(), methodParamInfo.getArgName());
        }
        if (methodInfo.getBatch()) {
            // 简单类型批量操作需要重写参数节点   【int deleteBatchById(@BatchData List<ID> ids, @BatchSize int batchSize);】
            List<String> argValueCommonPathItemList = Lists.newArrayList(methodParamInfo.getBatchItemName());
            methodParamInfo.setArgValueCommonPathItemList(argValueCommonPathItemList);
        }
        return methodParamInfo;
    }

    private Class<?> getMethodParamType(MapperInfo mapperInfo, Parameter parameter) {
        if (TypeUtils.isAssignable(parameter.getType(), Map.class)) {
            return Map.class;
        }
        Type type = parameter.getParameterizedType();
        return getMethodType(mapperInfo, type);
    }

    /**
     * mybatis在[单参数、复合类型、无注解]情况下为了获取参数方便，不会对参数进行包装，所以不会生成argx这种参数
     *
     * @param methodParamInfo
     * @param parameter
     * @param parameterCount
     * @param typeCategory
     */
    private void handleMethodParamName(MethodParamInfo methodParamInfo, Parameter parameter, int parameterCount, TypeCategory typeCategory) {
        String argName = parameter.getName();
        Param param = parameter.getAnnotation(Param.class);
        if (param != null) {
            argName = param.value();
        }
        methodParamInfo.setArgName(argName);
        methodParamInfo.setParam(param);
        if (!(parameterCount == 1 && typeCategory == TypeCategory.COMPLEX && param == null)) {
            methodParamInfo.setArgValueCommonPathItemList(Lists.newArrayList(argName));
            methodParamInfo.setWrapper(true);
        }
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
        } else if ("QUERY_ENTITY".equals(actualTypeName)) {
            return mapperInfo.getQueryEntityClass();
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

    private String getNamespaceMethodName(MapperInfo mapperInfo, String methodName) {
        return String.format("%s.%s", mapperInfo.getNamespace(), methodName);
    }

    /**
     * 忽略方法
     *
     * @param method
     * @return
     */
    private boolean isIgnoredMethod(Method method) {
        int modifiers = method.getModifiers();
        if (Modifier.isStatic(modifiers)) {
            return true;
        }
        if (Modifier.isPrivate(modifiers)) {
            return true;
        }
        if (method.isDefault()) {
            return true;
        }
        if (method.isSynthetic()) {
            return true;
        }
        if (method.isBridge()) {
            return true;
        }
        return false;
    }

    public TypeCategory getClassCategory(Type type) {
        return columnTypeHandler.getClassCategory(type);
    }

    private static class MethodParamContext {

        private MethodParamInfo entityParamInfo;

        private MethodParamInfo queryEntityParamInfo;

        private List<MethodParamInfo> methodParamInfoList;

        public MethodParamContext(MethodParamInfo entityParamInfo, MethodParamInfo queryEntityParamInfo, List<MethodParamInfo> methodParamInfoList) {
            this.entityParamInfo = entityParamInfo;
            this.queryEntityParamInfo = queryEntityParamInfo;
            this.methodParamInfoList = methodParamInfoList;
        }

        public MethodParamInfo getEntityParamInfo() {
            return entityParamInfo;
        }

        public MethodParamInfo getQueryEntityParamInfo() {
            return queryEntityParamInfo;
        }

        public List<MethodParamInfo> getMethodParamInfoList() {
            return methodParamInfoList;
        }
    }
}
