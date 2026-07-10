package com.mybatisgx.model.handler;

import com.google.common.collect.Lists;
import com.mybatisgx.annotation.*;
import com.mybatisgx.api.MethodCommandType;
import com.mybatisgx.context.EntityInfoContextHolder;
import com.mybatisgx.dao.Dao;
import com.mybatisgx.dsl.method.MethodSyntaxProcessor;
import com.mybatisgx.exception.MybatisgxException;
import com.mybatisgx.ext.session.MybatisgxConfiguration;
import com.mybatisgx.model.*;
import com.mybatisgx.utils.MethodInfoUtils;
import com.mybatisgx.utils.TypeUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.ibatis.annotations.*;
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
    private TypeResolver typeResolver = new TypeResolver();
    private MethodSyntaxProcessor methodSyntaxProcessor = new MethodSyntaxProcessor();
    private final MybatisgxConfiguration configuration;

    public MethodInfoHandler(MybatisgxConfiguration configuration) {
        this.configuration = configuration;
    }

    public List<MethodInfo> execute(MapperInfo mapperInfo, Class<?> interfaceClass) {
        List<Method> methodList = this.getDaoMethodList(interfaceClass);
        Map<String, MethodInfo> methodInfoMap = this.processMethod(methodList, mapperInfo);
        List<MethodInfo> methodInfoList = new ArrayList(20);
        // 注册dao中的方法
        for (MethodInfo methodInfo : methodInfoMap.values()) {
            this.configuration.addMethodInfo(methodInfo);
            methodInfoList.add(methodInfo);
        }
        // 注册关联查询内嵌查询方法
        for (ResultMapInfo resultMapInfo : mapperInfo.getResultMapInfoList()) {
            ResultMapInfo.NestedSelect nestedSelect = resultMapInfo.getNestedSelect();
            if (nestedSelect == null) {
                continue;
            }
            MethodInfo methodInfo = new MethodInfo();
            methodInfo.setMapperInfo(mapperInfo);
            methodInfo.setMethodName(nestedSelect.getId());
            this.configuration.addMethodInfo(methodInfo);
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
            String namespaceMethodName = MethodInfoUtils.getNamespaceMethodName(mapperInfo.getNamespace(), methodName);
            if (this.configuration.hasStatement(namespaceMethodName)) {
                LOGGER.debug("方法{}已在mapper存在，无需处理该方法！", namespaceMethodName);
                continue;
            }

            CommandTypeContext commandTypeContext = this.getCommandType(mapperInfo, method);
            SqlCommandType sqlCommandType = commandTypeContext.getSqlCommandType();
            MethodParamContext methodParamContext = this.getMethodParam(mapperInfo, method, sqlCommandType);
            MethodReturnInfo methodReturnInfo = this.getMethodReturn(mapperInfo, method);

            MethodInfo methodInfo = new MethodInfo();
            methodInfo.setMapperInfo(mapperInfo);
            methodInfo.setMethod(method);
            methodInfo.setMethodName(methodName);
            methodInfo.setSqlCommandType(sqlCommandType);
            methodInfo.setMethodCommandType(commandTypeContext.getMethodCommandType());
            methodInfo.setDynamic(method.getAnnotation(Dynamic.class) != null);
            methodInfo.setBatch(method.getAnnotation(BatchOperation.class) != null);

            methodInfo.setEntityParamInfo(methodParamContext.getEntityParamInfo());
            methodInfo.setQueryEntityParamInfo(methodParamContext.getQueryEntityParamInfo());
            methodInfo.setMethodParamInfoList(methodParamContext.getMethodParamInfoList());
            methodInfo.setMethodReturnInfo(methodReturnInfo);

            // 预处理该方法是否需要值生成处理
            boolean isValueProcessor = this.isValueProcessor(methodInfo);
            methodInfo.setValueProcessor(isValueProcessor);

            methodInfoMap.put(methodName, methodInfo);
        }
        return methodInfoMap;
    }

    private CommandTypeContext getCommandType(MapperInfo mapperInfo, Method method) {
        Insert insert = method.getAnnotation(Insert.class);
        Delete delete = method.getAnnotation(Delete.class);
        Update update = method.getAnnotation(Update.class);
        Select select = method.getAnnotation(Select.class);
        SqlCommandType sqlCommandType = null;
        if (insert != null) {
            sqlCommandType = SqlCommandType.INSERT;
        }
        if (delete != null) {
            sqlCommandType = SqlCommandType.DELETE;
        }
        if (update != null) {
            sqlCommandType = SqlCommandType.UPDATE;
        }
        if (select != null) {
            sqlCommandType = SqlCommandType.SELECT;
        }
        if (sqlCommandType == null) {
            Statement statement = method.getAnnotation(Statement.class);
            String statementString = statement != null ? statement.value() : method.getName();
            sqlCommandType = this.methodSyntaxProcessor.getSqlCommandType(statementString);
        }
        MethodCommandType methodCommandType;
        if (sqlCommandType == SqlCommandType.DELETE && mapperInfo.getEntityInfo().getLogicDeleteColumnInfo() != null) {
            methodCommandType = MethodCommandType.LOGIC_DELETE;
        } else {
            methodCommandType = MethodCommandType.valueOf(sqlCommandType.name());
        }
        return new CommandTypeContext(sqlCommandType, methodCommandType);
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
            TypeCategory typeCategory = this.typeResolver.getCategory(methodParamType);

            MethodParamInfo methodParamInfo = new MethodParamInfo();
            methodParamInfo.setIndex(i);
            methodParamInfo.setType(methodParamType);
            methodParamInfo.setTypeName(methodParamType.getName());
            this.handleMethodParamName(methodParamInfo, parameter, parameterCount, typeCategory);
            this.handleMethodCollectionTypeParam(parameter, methodParamInfo);
            this.handleBatchOperation(method, parameter, methodParamInfo);

            methodParamInfo.setTypeCategory(typeCategory);
            if (typeCategory == TypeCategory.OBJECT && methodParamType != Map.class) {
                if (methodParamType.getAnnotation(IdClass.class) != null) {
                    IdColumnInfo idColumnInfo = mapperInfo.getEntityInfo().getIdColumnInfo();
                    if (methodParamType != idColumnInfo.getJavaType()) {
                        throw new MybatisgxException("方法参数复合主键和实体中的复合主键类型不一致");
                    }
                    methodParamInfo.setColumnInfoList(idColumnInfo.getComposites());
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
        TypeCategory typeCategory = this.typeResolver.getCategory(methodReturnType);

        MethodReturnInfo methodReturnInfo = new MethodReturnInfo();
        methodReturnInfo.setClassCategory(typeCategory);
        methodReturnInfo.setType(methodReturnType);
        methodReturnInfo.setTypeName(methodReturnType.getName());
        if (typeCategory == TypeCategory.OBJECT && methodReturnType != Map.class) {
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

    private boolean isValueProcessor(MethodInfo methodInfo) {
        MethodCommandType methodCommandType = methodInfo.getMethodCommandType();
        if (methodCommandType == MethodCommandType.SELECT || methodCommandType == MethodCommandType.DELETE) {
            return false;
        }
        // 逻辑删除可能没有实体参数，但是新增和修改必须有实体参数
        if (methodCommandType != MethodCommandType.LOGIC_DELETE) {
            if (methodInfo.getEntityParamInfo() == null) {
                return false;
            }
        }
        return true;
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
        if (!(parameterCount == 1 && typeCategory == TypeCategory.OBJECT && param == null)) {
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

    /**
     * 从 mgxsql SQL 文本推断 SqlCommandType
     *
     * @param sqlText SQL 文本
     * @return SqlCommandType，无法推断时返回 null
     */
    private static class CommandTypeContext {

        private SqlCommandType sqlCommandType;

        private MethodCommandType methodCommandType;

        public CommandTypeContext(SqlCommandType sqlCommandType, MethodCommandType methodCommandType) {
            this.sqlCommandType = sqlCommandType;
            this.methodCommandType = methodCommandType;
        }

        public SqlCommandType getSqlCommandType() {
            return sqlCommandType;
        }

        public MethodCommandType getMethodCommandType() {
            return methodCommandType;
        }
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
