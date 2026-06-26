package com.mybatisgx.model.handler;

import com.google.common.collect.Lists;
import com.mybatisgx.annotation.Property;
import com.mybatisgx.annotation.QueryColumn;
import com.mybatisgx.annotation.Statement;
import com.mybatisgx.api.MethodCommandType;
import com.mybatisgx.dsl.method.MethodSyntaxProcessor;
import com.mybatisgx.dsl.method.model.BaseStatement;
import com.mybatisgx.dsl.mgxql.MgxqlSyntaxProcessor;
import com.mybatisgx.dsl.mgxql.model.MgxqlSourceType;
import com.mybatisgx.dsl.mgxql.model.SelectItemType;
import com.mybatisgx.dsl.mgxql.model.*;
import com.mybatisgx.dsl.mgxql.model.expression.ConditionColumnExpression;
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
            WhereExpression conditionExpression = mgxqlStatement != null && mgxqlStatement.getWhereClause() != null ? mgxqlStatement.getWhereClause().getRootExpression() : null;
            this.bindConditionParam(mapperInfo, methodInfo, conditionExpression);

            if (mgxqlStatement instanceof SelectStatement) {
                HavingExpression havingExpression = ((SelectStatement) mgxqlStatement).getHavingExpression();
                if (havingExpression != null) {
                    this.bindHavingParam(methodInfo, havingExpression);
                }
            }
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
            QueryColumn queryColumn = columnInfo.getQueryColumn();
            if (queryColumn != null && queryColumn.ignore()) {
                continue;
            }
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
                String comparisonOperator = javaColumnName.replace(propertyName, "");
                javaColumnName = String.format("%s%s%s%s", "$", propertyName, "$", comparisonOperator);
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
                    BoundParam boundParam = new BoundParam(ParamKind.NULL_TYPE);
                    boundParam.setOperator(conditionNode.getOperator());
                    boundParam.setNotOperator(conditionNode.getNotOperator());
                    if (conditionNode.getColumnInfo() != null) {
                        BoundParamEntry entry = new BoundParamEntry();
                        entry.setSqlExpression(new ConditionColumnExpression(conditionNode.getColumnInfo().getDbColumnName(), conditionNode.getColumnInfo().getTypeHandler()));
                        boundParam.addEntry(entry);
                    }
                    conditionNode.setBoundParam(boundParam);
                    continue;
                }
                String paramValuePath = StringUtils.join(conditionNode.getParamValuePath(), ".");
                MethodParamInfo methodParamInfo = this.bindParam(methodInfo, conditionNode, paramValuePath);
                // 校验条件是否可以关联到参数，如果无法关联，后续执行数据库操作会报错
                if (methodParamInfo == null) {
                    throw new MybatisgxException("%s方法条件没有对应的参数", methodInfo.getMethodName());
                }
                conditionNode.setMethodParamInfo(methodParamInfo);
                conditionNode.setBoundParam(this.buildBoundParam(conditionNode, methodParamInfo));
            }
        }
    }

    /**
     * 统一参数绑定优先级链：
     * ① 方法简单参数有 @Param 注解(paramValuePath, id 条件支持复合类型) —— 仅非 ENTITY 来源
     * ② 方法简单参数有 @Param 注解(paramValuePath 全小写)              —— 仅非 ENTITY 来源
     * ③ 实体字段：queryEntity 优先(含后缀解析) → entity，首个命中即 early return —— 所有来源统一
     * ④ 方法简单参数无 @Param 注解(arg{index})
     * <p>①② 仅对非 ENTITY 来源生效：ENTITY 条件派生自 query 实体字段，paramValuePath 为基础字段名，
     *    @Param 按基础名匹配会与后缀解析冲突（如 idIn 误绑 @Param("id") 而非 query.idIn）。
     * <p>③ 内 queryEntity 优先于 entity：query 实体是 entity 字段超集，
     * 且 UPDATE 双参数时 queryEntity 为 WHERE 条件源、entity 为 SET 值源。
     */
    private MethodParamInfo bindParam(MethodInfo methodInfo, WhereConditionNode conditionNode, String paramValuePath) {
        MgxqlSourceType sourceType = methodInfo.getMgxqlStatement() != null ? methodInfo.getMgxqlStatement().getMgxqlSourceType() : null;
        // ①② @Param 仅对非 ENTITY 来源生效：ENTITY 条件派生自 query 实体字段，paramValuePath 为基础字段名，
        //    @Param 按基础名匹配会与后缀解析冲突（如 idIn 误绑 @Param("id") 而非 query.idIn）。
        if (sourceType != MgxqlSourceType.ENTITY) {
            MethodParamInfo methodParamInfo = this.getSimpleTypeConditionParam(methodInfo, conditionNode, paramValuePath);
            if (methodParamInfo != null) {
                return methodParamInfo;
            }
            methodParamInfo = this.getSimpleTypeConditionParam(methodInfo, conditionNode, paramValuePath.toLowerCase());
            if (methodParamInfo != null) {
                return methodParamInfo;
            }
        }
        // ③ 实体字段：queryEntity 优先(含后缀解析) → entity，首个命中即 early return
        MethodParamInfo queryEntityParamInfo = methodInfo.getQueryEntityParamInfo();
        if (queryEntityParamInfo != null) {
            MethodParamInfo methodParamInfo = this.getEntityTypeConditionParam(methodInfo, conditionNode, queryEntityParamInfo);
            if (methodParamInfo != null) {
                return methodParamInfo;
            }
        }
        MethodParamInfo entityParamInfo = methodInfo.getEntityParamInfo();
        if (entityParamInfo != null) {
            MethodParamInfo methodParamInfo = this.getEntityTypeConditionParam(methodInfo, conditionNode, entityParamInfo);
            if (methodParamInfo != null) {
                return methodParamInfo;
            }
        }
        // ④ 方法简单参数无 @Param 注解(arg{index})
        String argName = String.format("arg%1$s", conditionNode.getIndex());
        return this.getSimpleTypeConditionParam(methodInfo, conditionNode, argName);
    }

    private MethodParamInfo getEntityTypeConditionParam(MethodInfo methodInfo, WhereConditionNode conditionNode, MethodParamInfo entityParamInfo) {
        String paramValuePath = StringUtils.join(conditionNode.getParamValuePath(), ".");
        ColumnInfo baseColumnInfo = entityParamInfo.getEntityInfo().getColumnInfo(paramValuePath);
        if (baseColumnInfo == null) {
            return null;
        }

        String paramFieldName = baseColumnInfo.getJavaColumnName();
        com.mybatisgx.dsl.mgxql.model.ComparisonOperator operator = conditionNode.getOperator();
        if (operator != null && !operator.isNullComparisonOperator()) {
            String suffix = getQueryEntityFieldSuffix(operator);
            if (suffix != null) {
                String queryEntityFieldName = paramValuePath + suffix;
                ColumnInfo queryEntityField = entityParamInfo.getEntityInfo().getColumnInfo(queryEntityFieldName);
                if (queryEntityField != null) {
                    paramFieldName = queryEntityField.getJavaColumnName();
                }
            }
        }

        MethodParamInfo methodParamInfo = new MethodParamInfo();
        methodParamInfo.setTypeCategory(baseColumnInfo.getTypeCategory());
        methodParamInfo.setType(baseColumnInfo.getJavaType());
        methodParamInfo.setCollectionType(baseColumnInfo.getCollectionType());
        List<ColumnInfo> composites = baseColumnInfo.getComposites();
        if (ObjectUtils.isNotEmpty(composites)) {
            methodParamInfo.setColumnInfoList(composites);
        }

        int paramCount = methodInfo.getMethodParamInfoList().size();
        Param param = entityParamInfo.getParam();
        List<String> paramValueCommonPathItemList = new ArrayList<>();
        if (paramCount == 1 && param == null) {
            paramValueCommonPathItemList.add(paramFieldName);
        } else {
            if (methodInfo.getBatch()) {
                paramValueCommonPathItemList.add(entityParamInfo.getBatchItemName());
                paramValueCommonPathItemList.add(paramFieldName);
            } else {
                paramValueCommonPathItemList.add(entityParamInfo.getArgName());
                paramValueCommonPathItemList.add(paramFieldName);
            }
        }
        methodParamInfo.setArgValueCommonPathItemList(paramValueCommonPathItemList);
        methodParamInfo.setWrapper(true);
        return methodParamInfo;
    }

    private String getQueryEntityFieldSuffix(com.mybatisgx.dsl.mgxql.model.ComparisonOperator operator) {
        switch (operator) {
            case BETWEEN:
                return "Between";
            case IN:
                return "In";
            case LIKE:
                return "Like";
            case STARTING_WITH:
                return "StartingWith";
            case ENDING_WITH:
                return "EndingWith";
            case EQ:
            case EQUAL:
                return "Eq";
            case NOT:
                return "Not";
            case LT:
                return "Lt";
            case LT_EQ:
                return "Lteq";
            case GT:
                return "Gt";
            case GT_EQ:
                return "Gteq";
            default:
                return null;
        }
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

    /**
     * 根据条件节点和已解析的方法参数信息构建统一绑定结果
     */
    private BoundParam buildBoundParam(WhereConditionNode conditionNode, MethodParamInfo methodParamInfo) {
        BoundParam boundParam = new BoundParam();
        boundParam.setOperator(conditionNode.getOperator());
        boundParam.setNotOperator(conditionNode.getNotOperator());

        ColumnInfo columnInfo = conditionNode.getColumnInfo();
        List<ColumnInfo> composites = methodParamInfo.getColumnInfoList();
        List<String> prefixPath = methodParamInfo.getArgValueCommonPathItemList();

        if (ObjectUtils.isNotEmpty(composites)) {
            boundParam.setKind(ParamKind.COMPOSITE);
            for (int i = 0; i < composites.size(); i++) {
                ColumnInfo composite = composites.get(i);
                BoundParamEntry entry = new BoundParamEntry();
                entry.setSqlExpression(new ConditionColumnExpression(composite.getDbColumnName(), composite.getTypeHandler()));
                List<String> fullPath = new ArrayList<>(prefixPath);
                fullPath.add(composite.getJavaColumnName());
                entry.setParamPath(fullPath);
                if (i > 0) {
                    entry.setLogicOperator(com.mybatisgx.dsl.mgxql.model.LogicOperator.AND);
                }
                boundParam.addEntry(entry);
            }
        } else {
            boundParam.setKind(ParamKind.SIMPLE);
            BoundParamEntry entry = new BoundParamEntry();
            if (columnInfo != null) {
                entry.setSqlExpression(new ConditionColumnExpression(columnInfo.getDbColumnName(), columnInfo.getTypeHandler()));
                entry.setTypeHandler(columnInfo.getTypeHandler());
            }
            entry.setParamPath(prefixPath != null ? new ArrayList<>(prefixPath) : new ArrayList<>());
            boundParam.addEntry(entry);
        }

        com.mybatisgx.dsl.mgxql.model.ComparisonOperator operator = conditionNode.getOperator();
        if (operator == com.mybatisgx.dsl.mgxql.model.ComparisonOperator.IN || operator == com.mybatisgx.dsl.mgxql.model.ComparisonOperator.BETWEEN) {
            CollectionInfo collectionInfo = new CollectionInfo();
            collectionInfo.setItemName("item");
            boundParam.setCollectionInfo(collectionInfo);
        }

        return boundParam;
    }

    /**
     * 绑定HAVING条件的参数
     */
    private void bindHavingParam(MethodInfo methodInfo, HavingExpression expression) {
        if (expression == null || expression.getNodes() == null) {
            return;
        }
        for (HavingConditionNode node : expression.getNodes()) {
            if (node.isNested()) {
                this.bindHavingParam(methodInfo, node.getSubExpression());
                continue;
            }
            if (node.getParamValuePath() == null) {
                BoundParam boundParam = new BoundParam(ParamKind.SIMPLE);
                boundParam.setOperator(node.getOperator());
                BoundParamEntry entry = new BoundParamEntry();
                entry.setSqlExpression(node.getLeftSide());
                entry.setLiteralValue(node.getLiteralValue());
                boundParam.addEntry(entry);
                node.setBoundParam(boundParam);
                continue;
            }
            String paramName = StringUtils.join(node.getParamValuePath(), ".");
            MethodParamInfo methodParamInfo = methodInfo.getMethodParamInfo(paramName);
            if (methodParamInfo == null) {
                methodParamInfo = methodInfo.getMethodParamInfo(paramName.toLowerCase());
            }
            if (methodParamInfo == null) {
                throw new MybatisgxException("HAVING条件参数 '%s' 没有对应的方法参数", paramName);
            }
            BoundParam boundParam = new BoundParam(ParamKind.SIMPLE);
            boundParam.setOperator(node.getOperator());
            BoundParamEntry entry = new BoundParamEntry();
            entry.setSqlExpression(node.getLeftSide());
            List<String> paramPath = methodParamInfo.getArgValueCommonPathItemList();
            entry.setParamPath(paramPath != null ? new ArrayList<>(paramPath) : new ArrayList<>(node.getParamValuePath()));
            boundParam.addEntry(entry);
            node.setBoundParam(boundParam);
        }
    }
}
