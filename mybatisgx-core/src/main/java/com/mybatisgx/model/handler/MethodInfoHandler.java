package com.mybatisgx.model.handler;

import com.google.common.base.CaseFormat;
import com.google.common.collect.Lists;
import com.mybatisgx.annotation.*;
import com.mybatisgx.context.EntityInfoContextHolder;
import com.mybatisgx.context.MethodInfoContextHolder;
import com.mybatisgx.dao.Dao;
import com.mybatisgx.dao.SelectDao;
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
        methodInfoMap.forEach((methodName, methodInfo) -> {
            String namespaceMethodName = this.getNamespaceMethodName(mapperInfo, methodName);
            MethodInfoContextHolder.set(namespaceMethodName, methodInfo);
            methodInfoList.add(methodInfo);
        });
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
            String methodName = method.getName();
            if (methodInfoMap.containsKey(methodName)) {
                throw new MybatisgxException("dao接口方法无法重载，请修改方法名: %s", methodName);
            }
            String namespaceMethodName = this.getNamespaceMethodName(mapperInfo, methodName);
            if (this.configuration.hasStatement(namespaceMethodName)) {
                LOGGER.debug("方法{}已在mapper存在，无需处理该方法！", namespaceMethodName);
                continue;
            }

            MethodParamContext methodParamContext = this.getMethodParam(mapperInfo, method);
            MethodReturnInfo methodReturnInfo = this.getMethodReturn(mapperInfo, method);

            MethodInfo methodInfo = new MethodInfo();
            methodInfo.setEntityInfo(mapperInfo.getEntityInfo());
            methodInfo.setMethod(method);
            methodInfo.setMethodName(methodName);
            methodInfo.setDynamic(method.getAnnotation(Dynamic.class) != null);
            methodInfo.setBatch(method.getAnnotation(BatchOperation.class) != null);
            methodInfo.setEntityParamInfo(methodParamContext.getEntityParamInfo());
            methodInfo.setMethodParamInfoList(methodParamContext.getMethodParamInfoList());
            methodInfo.setMethodReturnInfo(methodReturnInfo);

            // 方法名解析
            Class<?> methodInterfaceClass = method.getDeclaringClass();
            this.methodNameParse(mapperInfo.getEntityInfo(), methodInfo, methodInterfaceClass);
            // 方法条件解析
            Sql sql = method.getAnnotation(Sql.class);
            if (sql != null) {
                methodInfo.setSyntaxExpression(sql.value());
                this.queryConditionParse(mapperInfo.getEntityInfo(), methodInfo);
            }

            SelectItemInfo selectItemInfo = methodInfo.getSelectItemInfo();
            if (methodInfo.getSqlCommandType() == SqlCommandType.SELECT
                    && selectItemInfo.getSelectItemType() == SelectItemType.COLUMN) {
                this.entityRelationTreeHandler.execute(mapperInfo, methodInfo);
                String resultMapId = resultMapInfoHandler.execute(mapperInfo, methodInfo);
                methodInfo.setResultMapId(resultMapId);
            }

            this.bindConditionParam(mapperInfo, methodInfo, methodInfo.getConditionInfoList());
            // check(resultMapInfo, methodInfo);

            methodInfoMap.put(methodName, methodInfo);
        }
        return methodInfoMap;
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
        int parameterCount = parameters.length;
        MethodParamInfo entityParamInfo = null;
        List<MethodParamInfo> methodParamInfoList = new ArrayList<>();
        for (int i = 0; i < parameterCount; i++) {
            Parameter parameter = parameters[i];
            Class<?> methodParamType = this.getMethodParamType(mapperInfo, parameter);
            ClassCategory classCategory = this.getClassCategory(methodParamType);

            MethodParamInfo methodParamInfo = new MethodParamInfo();
            methodParamInfo.setIndex(i);
            methodParamInfo.setType(methodParamType);
            methodParamInfo.setTypeName(methodParamType.getName());
            this.processMethodParamName(methodParamInfo, parameter, parameterCount, classCategory);

            BatchData batchData = parameter.getAnnotation(BatchData.class);
            if (batchOperation != null && batchData != null) {
                methodParamInfo.setBatchData(true);
                methodParamInfo.setBatchItemName(batchData.value());
            }
            BatchSize batchSize = parameter.getAnnotation(BatchSize.class);
            if (batchOperation != null && batchSize != null) {
                methodParamInfo.setBatchSize(true);
            }

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
                    if (entityParamInfo == null) {
                        EntityInfo entityInfo = EntityInfoContextHolder.get(methodParamType);
                        methodParamInfo.setColumnInfoList(entityInfo.getColumnInfoList());
                        entityParamInfo = methodParamInfo;
                    } else {
                        throw new MybatisgxException("%s 方法实体参数类型或查询实体参数类型存在多个", method.getName());
                    }
                }
                QueryEntity queryEntity = methodParamType.getAnnotation(QueryEntity.class);
                if (queryEntity != null) {
                    if (entityParamInfo == null) {
                        EntityInfo entityInfo = EntityInfoContextHolder.get(methodParamType);
                        methodParamInfo.setColumnInfoList(entityInfo.getColumnInfoList());
                        entityParamInfo = methodParamInfo;
                    } else {
                        throw new MybatisgxException("%s 方法实体参数类型或查询实体参数类型存在多个", method.getName());
                    }
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
        ClassCategory classCategory = this.getClassCategory(methodReturnType);

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
     * 一个方法只允许定义一个实体参数（领域实体+查询实体），如果定义多个，会报错。
     * 实体作为条件只支持查询方法，修改和删除不支持。
     * @param entityInfo
     * @param methodInfo
     * @param methodInterfaceClass
     */
    public void methodNameParse(EntityInfo entityInfo, MethodInfo methodInfo, Class<?> methodInterfaceClass) {
        mybatisgxSyntaxProcessor.execute(entityInfo, methodInfo, null, ConditionOriginType.METHOD_NAME, methodInfo.getMethodName());
        if (methodInfo.getSqlCommandType() != SqlCommandType.SELECT) {
            return;
        }
        if (methodInterfaceClass == SelectDao.class) {
            String methodName = methodInfo.getMethodName();
            if (Arrays.asList("findOne", "findList", "findPage").contains(methodName)) {
                MethodParamInfo entityParamInfo = methodInfo.getEntityParamInfo();
                if (entityParamInfo != null) {
                    String entityCondition = this.entityCondition(methodInfo, entityParamInfo);
                    mybatisgxSyntaxProcessor.execute(entityInfo, methodInfo, entityParamInfo, ConditionOriginType.ENTITY_FIELD, entityCondition);
                }
            }
        } else {
            MethodParamInfo entityParamInfo = methodInfo.getEntityParamInfo();
            if (entityParamInfo != null) {
                String queryEntityCondition = this.entityCondition(methodInfo, entityParamInfo);
                mybatisgxSyntaxProcessor.execute(entityInfo, methodInfo, entityParamInfo, ConditionOriginType.ENTITY_FIELD, queryEntityCondition);
            }
        }
    }

    public void queryConditionParse(EntityInfo entityInfo, MethodInfo methodInfo) {
        mybatisgxSyntaxProcessor.execute(entityInfo, methodInfo, null, ConditionOriginType.METHOD_NAME, methodInfo.getSyntaxExpression());
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
        ColumnInfo columnInfo = entityParamInfo.getColumnInfo(conditionColumnName);
        if (columnInfo == null) {
            return null;
        }

        List<ColumnInfo> composites = columnInfo.getComposites();
        ClassCategory classCategory = ObjectUtils.isEmpty(composites) ? ClassCategory.SIMPLE : ClassCategory.COMPLEX;

        MethodParamInfo methodParamInfo = new MethodParamInfo();
        methodParamInfo.setClassCategory(classCategory);
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
        if (methodParamInfo.getClassCategory() == ClassCategory.COMPLEX && TypeUtils.typeEquals(conditionInfo.getColumnInfo(), ColumnInfo.class)) {
            throw new RuntimeException("查询条件不能关联到复杂类型参数" + methodParamInfo.getArgName());
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
     * @param methodParamInfo
     * @param parameter
     * @param parameterCount
     * @param classCategory
     */
    private void processMethodParamName(MethodParamInfo methodParamInfo, Parameter parameter, int parameterCount, ClassCategory classCategory) {
        String argName = parameter.getName();
        Param param = parameter.getAnnotation(Param.class);
        if (param != null) {
            argName = param.value();
        }
        methodParamInfo.setArgName(argName);
        methodParamInfo.setParam(param);
        if (!(parameterCount == 1 && classCategory == ClassCategory.COMPLEX && param == null)) {
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

    public ClassCategory getClassCategory(Type type) {
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

    private static class ConditionParamContext {

        private MethodParamInfo methodParamInfo;
        /**
         * 条件值来源于方法参数或者方法参数实体，需要提前计算出公共取值路径，模板渲染的时候就不再需要多重逻辑判断
         */
        private List<String> paramValueCommonPathItemList;

        public ConditionParamContext(MethodParamInfo methodParamInfo, List<String> paramValueCommonPathItemList) {
            this.methodParamInfo = methodParamInfo;
            this.paramValueCommonPathItemList = paramValueCommonPathItemList;
        }

        public MethodParamInfo getMethodParamInfo() {
            return methodParamInfo;
        }

        public void setMethodParamInfo(MethodParamInfo methodParamInfo) {
            this.methodParamInfo = methodParamInfo;
        }

        public List<String> getParamValueCommonPathItemList() {
            return paramValueCommonPathItemList;
        }

        public void setParamValueCommonPathItemList(List<String> paramValueCommonPathItemList) {
            this.paramValueCommonPathItemList = paramValueCommonPathItemList;
        }
    }
}
