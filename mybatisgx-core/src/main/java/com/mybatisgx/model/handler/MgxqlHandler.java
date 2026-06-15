package com.mybatisgx.model.handler;

import com.google.common.collect.Lists;
import com.mybatisgx.annotation.Property;
import com.mybatisgx.annotation.Statement;
import com.mybatisgx.api.MethodCommandType;
import com.mybatisgx.dsl.method.MethodSyntaxProcessor;
import com.mybatisgx.dsl.method.model.BaseStatement;
import com.mybatisgx.dsl.mgxql.MgxqlSyntaxProcessor;
import com.mybatisgx.dsl.mgxql.model.*;
import com.mybatisgx.dsl.mgxql.model.MgxqlSourceType;
import com.mybatisgx.dsl.mgxql.model.SelectItemType;
import com.mybatisgx.exception.MybatisgxException;
import com.mybatisgx.model.*;
import com.mybatisgx.utils.FieldNameUtils;
import com.mybatisgx.utils.TypeUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.mapping.SqlCommandType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * mgx查询语言处理器
 *
 * @author：薛承城
 */
public class MgxqlHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(MgxqlHandler.class);

    private MethodSyntaxProcessor methodSyntaxProcessor = new MethodSyntaxProcessor();
    private MgxqlSyntaxProcessor mgxqlSyntaxProcessor = new MgxqlSyntaxProcessor();
    private EntityRelationTreeHandler entityRelationTreeHandler = new EntityRelationTreeHandler();
    private ResultMapInfoHandler resultMapInfoHandler = new ResultMapInfoHandler();

    public void execute(MapperInfo mapperInfo) {
        for (MethodInfo methodInfo : mapperInfo.getMethodInfoList()) {
            this.methodConditionParse(methodInfo);

            // 查询方法的结果集处理
            if (methodInfo.getMethodCommandType() == MethodCommandType.SELECT) {
                SelectStatement selectStatement = (SelectStatement) methodInfo.getMgxqlStatement();
                for (SelectItem selectItem : selectStatement.getSelectItems()) {
                    if (selectItem.getType() == SelectItemType.COLUMN_ALL) {
                        this.entityRelationTreeHandler.execute(mapperInfo, methodInfo);
                        String resultMapId = resultMapInfoHandler.execute(mapperInfo, methodInfo);
                        methodInfo.setResultMapId(resultMapId);
                    }
                }
            }

            MgxqlStatement mgxqlStatement = methodInfo.getMgxqlStatement();
            WhereExpression conditionExpression = mgxqlStatement != null ? mgxqlStatement.getWhereClause().getRootExpression() : null;
            this.bindConditionParam(mapperInfo, methodInfo, conditionExpression);
        }
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

        // 解析Statement表达式（MGXQL路径）
        Statement statement = methodInfo.getMethod().getAnnotation(Statement.class);
        if (statement != null) {
            String mgxql = statement.value();
            MgxqlStatement mgxqlStatement = this.mgxqlSyntaxProcessor.executeAndCheck(entityInfo, methodInfo, null, MgxqlSourceType.MANUAL, mgxql);
            methodInfo.setMgxqlStatement(mgxqlStatement);
            return;
        }

        // 把实体字段转换成mgxql
        if (methodInfo.getSqlCommandType() == SqlCommandType.SELECT) {
            if (methodInfo.getEntityParamInfo() != null || methodInfo.getQueryEntityParamInfo() != null) {
                String methodName = this.entityToMethodName(methodInfo);
                this.methodNameParse(entityInfo, methodInfo, MgxqlSourceType.ENTITY, methodName);
                return;
            }
        }

        // 把方法名语法糖转成mgxql
        this.methodNameParse(entityInfo, methodInfo, MgxqlSourceType.METHOD_NAME);
    }

    private void methodNameParse(EntityInfo entityInfo, MethodInfo methodInfo, MgxqlSourceType mgxqlSourceType) {
        // 把方法名语法糖转成mgxql
        this.methodNameParse(entityInfo, methodInfo, mgxqlSourceType, methodInfo.getMethodName());
    }

    private void methodNameParse(EntityInfo entityInfo, MethodInfo methodInfo, MgxqlSourceType mgxqlSourceType, String methodName) {
        // 把方法名语法糖转成mgxql
        BaseStatement baseStatement = this.methodSyntaxProcessor.execute(methodInfo, methodName);
        if (baseStatement != null) {
            String mgxql = baseStatement.toMgxql();
            MgxqlStatement mgxqlStatement = this.mgxqlSyntaxProcessor.executeAndCheck(entityInfo, methodInfo, null, mgxqlSourceType, mgxql);
            methodInfo.setMgxqlStatement(mgxqlStatement);
        }
    }

    /**
     * 把实体字段转换成mgxql
     *
     * @param methodInfo
     * @return
     */
    private String entityToMethodName(MethodInfo methodInfo) {
        EntityInfo entityInfo = null;
        if (methodInfo.getEntityParamInfo() != null && methodInfo.getQueryEntityParamInfo() == null) {
            entityInfo = methodInfo.getEntityParamInfo().getEntityInfo();
        }
        if (methodInfo.getEntityParamInfo() == null && methodInfo.getQueryEntityParamInfo() != null) {
            entityInfo = methodInfo.getQueryEntityParamInfo().getEntityInfo();
        }

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
            String javaColumnName = columnInfo.getJavaColumnName();
            javaColumnName = FieldNameUtils.lowerCamelToUpperCamel(javaColumnName);
            Property property = columnInfo.getProperty();
            if (property != null) {
                String propertyName = property.name();
                propertyName = FieldNameUtils.lowerCamelToUpperCamel(propertyName);
                if (!javaColumnName.startsWith(propertyName)) {
                    throw new MybatisgxException(
                            "查询字段 '%s' 的 @Property(name=\"%s\") 配置非法：字段名必须以 '%s' 为前缀，否则会导致方法名 DSL 解析歧义。",
                            columnInfo.getJavaColumnName(),
                            property.name(),
                            property.name()
                    );
                }
                javaColumnName = javaColumnName.replace(propertyName, "");
                javaColumnName = String.format("%s%s", propertyName, javaColumnName);
            }
            columnConditionList.add(javaColumnName);
        }
        StringBuilder stringBuilder = new StringBuilder()
                .append("findBy")
                .append(StringUtils.join(columnConditionList, "And"));
        LOGGER.debug(stringBuilder.toString());
        return stringBuilder.toString();
    }

    /**
     * 绑定和条件和参数
     *
     * @param methodInfo
     * @param conditionExpression
     */
    private void bindConditionParam(MapperInfo mapperInfo, MethodInfo methodInfo, WhereExpression conditionExpression) {
        if (methodInfo.getSqlCommandType() == SqlCommandType.INSERT) {
            return;
        }

        if (conditionExpression == null) {
            if (methodInfo.getSqlCommandType() == SqlCommandType.DELETE || methodInfo.getSqlCommandType() == SqlCommandType.UPDATE) {
                throw new MybatisgxException("%s.%s方法禁止无条件执行！", mapperInfo.getNamespace(), methodInfo.getMethodName());
            }
            LOGGER.warn("{}.{}方法无查询条件，可能触发全表扫描", mapperInfo.getNamespace(), methodInfo.getMethodName());
            return;
        }
        for (WhereConditionNode conditionNode : conditionExpression.getNodes()) {
            WhereExpression subExpression = conditionNode.getSubExpression();
            if (subExpression != null) {
                this.bindConditionParam(mapperInfo, methodInfo, subExpression);
            } else {
                if (conditionNode.getOperator().isNullComparisonOperator()) {
                    continue;
                }
                // 处理查询条件和参数之间的关系，查询条件和参数之间是1对1关系，不要设计一对多关系，后续绑定参数很难处理
                // 条件优先级是    方法简单参数有@Param注解(id条件支持复合类型) > 方法简单参数有@Param注解全小写(id条件支持复合类型) > 实体字段 > 方法简单参数无@Param注解
                String paramValuePath = StringUtils.join(conditionNode.getParamValuePath(), ".");
                // String conditionColumnName = conditionInfo.getColumnName();
                MethodParamInfo methodParamInfo = this.getSimpleTypeConditionParam(methodInfo, conditionNode, paramValuePath);
                if (methodParamInfo == null) {
                    methodParamInfo = this.getSimpleTypeConditionParam(methodInfo, conditionNode, paramValuePath.toLowerCase());
                }
                if (methodParamInfo == null) {
                    MethodParamInfo entityParamInfo = methodInfo.getEntityParamInfo();
                    if (entityParamInfo != null) {
                        methodParamInfo = this.getEntityTypeConditionParam(methodInfo, conditionNode, entityParamInfo);
                    }
                    MethodParamInfo queryEntityParamInfo = methodInfo.getQueryEntityParamInfo();
                    if (queryEntityParamInfo != null) {
                        methodParamInfo = this.getEntityTypeConditionParam(methodInfo, conditionNode, queryEntityParamInfo);
                    }
                }
                if (methodParamInfo == null) {
                    String argName = String.format("arg%1$s", conditionNode.getIndex());
                    methodParamInfo = this.getSimpleTypeConditionParam(methodInfo, conditionNode, argName);
                }
                // 校验条件是否可以关联到参数，如果无法关联，后续执行数据库操作会报错
                if (methodParamInfo == null) {
                    throw new MybatisgxException("%s方法条件没有对应的参数", methodInfo.getMethodName());
                }
                conditionNode.setMethodParamInfo(methodParamInfo);
            }
        }
    }

    private MethodParamInfo getEntityTypeConditionParam(MethodInfo methodInfo, WhereConditionNode conditionNode, MethodParamInfo entityParamInfo) {
        // 如果存在条件实体，则把条件实体字段转换成参数名称
        // String conditionColumnName = conditionInfo.getColumnName();
        String paramValuePath = StringUtils.join(conditionNode.getParamValuePath(), ".");
        ColumnInfo columnInfo = entityParamInfo.getEntityInfo().getColumnInfo(paramValuePath);
        if (columnInfo == null) {
            return null;
        }

        MethodParamInfo methodParamInfo = new MethodParamInfo();
        methodParamInfo.setTypeCategory(columnInfo.getTypeCategory());
        methodParamInfo.setType(columnInfo.getJavaType());
        methodParamInfo.setCollectionType(columnInfo.getCollectionType());
        List<ColumnInfo> composites = columnInfo.getComposites();
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

    private MethodParamInfo getSimpleTypeConditionParam(MethodInfo methodInfo, WhereConditionNode conditionNode, String paramName) {
        // 采用3种方式获取参数：conditionName -> conditionName.toLowerCase() -> argx：【userName -> username -> arg0】
        MethodParamInfo methodParamInfo = methodInfo.getMethodParamInfo(paramName);
        // 校验条件是否可以关联到参数，如果无法关联，后续执行数据库操作会报错
        if (methodParamInfo == null) {
            return null;
        }
        if (methodParamInfo.getTypeCategory() == TypeCategory.OBJECT && TypeUtils.typeEquals(conditionNode.getColumnInfo(), ColumnInfo.class)) {
            throw new MybatisgxException("%s查询条件不能关联到复杂类型参数%s", methodInfo.getMethodName(), methodParamInfo.getArgName());
        }
        if (methodInfo.getBatch()) {
            // 简单类型批量操作需要重写参数节点   【int deleteBatchById(@BatchData List<ID> ids, @BatchSize int batchSize);】
            List<String> argValueCommonPathItemList = Lists.newArrayList(methodParamInfo.getBatchItemName());
            methodParamInfo.setArgValueCommonPathItemList(argValueCommonPathItemList);
        }
        return methodParamInfo;
    }
}
