package com.mybatisgx.template.select;

import com.mybatisgx.dsl.mgxql.model.FieldReference;
import com.mybatisgx.dsl.mgxql.model.FromClause;
import com.mybatisgx.dsl.mgxql.model.FromEntity;
import com.mybatisgx.dsl.mgxql.model.JoinEntity;
import com.mybatisgx.dsl.mgxql.model.JoinType;
import com.mybatisgx.dsl.mgxql.model.SelectItemType;
import com.mybatisgx.dsl.mgxql.model.SelectStatement;
import com.mybatisgx.model.ColumnEntityRelation;
import com.mybatisgx.model.ColumnInfo;
import com.mybatisgx.model.EntityInfo;
import com.mybatisgx.model.ForeignKeyInfo;
import com.mybatisgx.model.RelationColumnInfo;
import com.mybatisgx.model.RelationType;
import com.mybatisgx.template.MybatisgxSqlBuilder;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.select.TableFunction;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * MGXQL 专用查询列渲染处理器。
 * <p>
 * 与注解驱动的 {@link SelectColumnSqlTemplateHandler} 区分：本处理器读取 MGXQL
 * {@link SelectStatement#fromClause} 中已绑定的 {@link FromEntity#getEntityInfo()} 与
 * {@link JoinEntity#getRelationColumnInfo()}，按 FromClause 渲染 FROM/LEFT JOIN/ON，
 * 并按 {@link com.mybatisgx.dsl.mgxql.model.SelectItem} 投影渲染 SELECT 列。
 * <p>
 * 列别名统一使用注解 {@link ColumnEntityRelation#getTableNameAlias()}，与
 * {@code MgxqlResultMapInfoHandler} 自动生成的 result map 列别名天然对齐。
 *
 * @author 薛承城
 * @date 2026/6/27
 */
public class MgxqlSelectColumnTemplateHandler {

    private static final Logger logger = LoggerFactory.getLogger(MgxqlSelectColumnTemplateHandler.class);

    private SelectColumnSqlTemplateHandler selectColumnSqlTemplateHandler = new SelectColumnSqlTemplateHandler();

    /**
     * 构建 MGXQL 查询 SQL。
     *
     * @param selectStatement MGXQL SELECT 语句（fromClause 必须存在）
     * @param rootRelation     返回类型对应的注解实体关系树根节点
     * @return 渲染完成的 PlainSelect
     */
    public PlainSelect buildSelectSql(SelectStatement selectStatement, ColumnEntityRelation rootRelation) {
        FromClause fromClause = selectStatement.getFromClause();
        Map<String, ColumnEntityRelation> aliasTreeNodeMap;
        String fromTableName;
        String fromTableAlias;
        if (rootRelation != null) {
            aliasTreeNodeMap = this.buildAliasTreeNodeMap(fromClause, rootRelation);
            fromTableName = rootRelation.getTableName();
            fromTableAlias = rootRelation.getTableNameAlias();
        } else {
            // 无注解树（聚合等场景未构建树）：以主实体 entityInfo + 用户别名兜底
            aliasTreeNodeMap = this.buildAliasMapNoTree(fromClause);
            FromEntity primaryEntity = fromClause.getPrimaryEntity();
            fromTableName = (primaryEntity != null && primaryEntity.getEntityInfo() != null)
                    ? primaryEntity.getEntityInfo().getTableName() : null;
            fromTableAlias = (primaryEntity != null) ? primaryEntity.getAlias() : null;
        }

        PlainSelect plainSelect = new PlainSelect();
        this.buildSelectItems(plainSelect, selectStatement, aliasTreeNodeMap, rootRelation);
        this.buildFromItem(plainSelect, fromTableName, fromTableAlias);
        this.buildJoins(plainSelect, fromClause, aliasTreeNodeMap);
        return plainSelect;
    }

    /**
     * 判断 SELECT 项中是否存在聚合函数（决定 resultType 路径）。
     */
    public boolean hasAggregate(SelectStatement selectStatement) {
        if (selectStatement == null || selectStatement.getSelectItems() == null) {
            return false;
        }
        for (com.mybatisgx.dsl.mgxql.model.SelectItem selectItem : selectStatement.getSelectItems()) {
            if (selectItem.getType() != null && selectItem.getType().hasAggregateFunction()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 建立 MGXQL 用户别名 → 注解树节点 映射。
     * <p>
     * 主实体映射到树根；各 JOIN 实体沿 onLeftAlias 找到左实体树节点，在其 composites 中
     * 按 relationColumnInfo 身份（优先）或 entityInfo.clazz 匹配对应子节点。匹配不到时
     * 保留 null（渲染回退到用户别名，该实体不在 result map 中）。
     */
    private Map<String, ColumnEntityRelation> buildAliasTreeNodeMap(FromClause fromClause, ColumnEntityRelation rootRelation) {
        Map<String, ColumnEntityRelation> aliasMap = new LinkedHashMap<>();
        if (fromClause == null) {
            return aliasMap;
        }
        FromEntity primaryEntity = fromClause.getPrimaryEntity();
        if (primaryEntity != null && StringUtils.isNotBlank(primaryEntity.getAlias())) {
            aliasMap.put(primaryEntity.getAlias(), rootRelation);
        }
        if (fromClause.getJoinEntities() == null) {
            return aliasMap;
        }
        for (JoinEntity joinEntity : fromClause.getJoinEntities()) {
            if (StringUtils.isBlank(joinEntity.getAlias())) {
                continue;
            }
            ColumnEntityRelation rightNode = null;
            String leftAlias = joinEntity.getOnLeftAlias();
            ColumnEntityRelation leftNode = StringUtils.isNotBlank(leftAlias) ? aliasMap.get(leftAlias) : null;
            if (leftNode != null) {
                rightNode = this.findChildTreeNode(leftNode, joinEntity);
            }
            if (rightNode == null && joinEntity.getEntityInfo() != null) {
                rightNode = this.findTreeNodeByClazz(rootRelation, joinEntity.getEntityInfo().getClazz());
            }
            aliasMap.put(joinEntity.getAlias(), rightNode);
        }
        return aliasMap;
    }

    /**
     * 无注解树场景（聚合等）的别名映射：所有别名映射到 null，
     * {@link #resolveTableAlias} 回退到 MGXQL 用户别名。
     */
    private Map<String, ColumnEntityRelation> buildAliasMapNoTree(FromClause fromClause) {
        Map<String, ColumnEntityRelation> aliasMap = new LinkedHashMap<>();
        if (fromClause == null) {
            return aliasMap;
        }
        FromEntity primaryEntity = fromClause.getPrimaryEntity();
        if (primaryEntity != null && StringUtils.isNotBlank(primaryEntity.getAlias())) {
            aliasMap.put(primaryEntity.getAlias(), null);
        }
        if (fromClause.getJoinEntities() != null) {
            for (JoinEntity joinEntity : fromClause.getJoinEntities()) {
                if (StringUtils.isNotBlank(joinEntity.getAlias())) {
                    aliasMap.put(joinEntity.getAlias(), null);
                }
            }
        }
        return aliasMap;
    }

    @SuppressWarnings("unchecked")
    private ColumnEntityRelation findChildTreeNode(ColumnEntityRelation leftNode, JoinEntity joinEntity) {
        if (leftNode == null || leftNode.getComposites() == null) {
            return null;
        }
        RelationColumnInfo bound = joinEntity.getRelationColumnInfo();
        // 优先身份匹配：findRelation 在左实体关系列表命中时，与树子节点 columnInfo 为同一对象
        if (bound != null) {
            for (Object child : leftNode.getComposites()) {
                ColumnEntityRelation childNode = (ColumnEntityRelation) child;
                if (childNode.getColumnInfo() == bound) {
                    return childNode;
                }
            }
        }
        // 回退：按实体类型匹配
        if (joinEntity.getEntityInfo() != null) {
            Class<?> joinClazz = joinEntity.getEntityInfo().getClazz();
            for (Object child : leftNode.getComposites()) {
                ColumnEntityRelation childNode = (ColumnEntityRelation) child;
                if (childNode.getEntityInfo() != null && joinClazz.equals(childNode.getEntityInfo().getClazz())) {
                    return childNode;
                }
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private ColumnEntityRelation findTreeNodeByClazz(ColumnEntityRelation node, Class<?> clazz) {
        if (node == null || clazz == null) {
            return null;
        }
        if (node.getEntityInfo() != null && clazz.equals(node.getEntityInfo().getClazz())) {
            return node;
        }
        if (node.getComposites() != null) {
            for (Object child : node.getComposites()) {
                ColumnEntityRelation found = this.findTreeNodeByClazz((ColumnEntityRelation) child, clazz);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    /**
     * 解析渲染使用的表别名：树节点存在用注解 tableNameAlias，否则回退 MGXQL 用户别名。
     */
    private String resolveTableAlias(String alias, Map<String, ColumnEntityRelation> aliasMap) {
        if (StringUtils.isBlank(alias)) {
            return alias;
        }
        ColumnEntityRelation node = aliasMap.get(alias);
        if (node != null && StringUtils.isNotBlank(node.getTableNameAlias())) {
            return node.getTableNameAlias();
        }
        return alias;
    }

    private void buildSelectItems(PlainSelect plainSelect, SelectStatement selectStatement,
                                  Map<String, ColumnEntityRelation> aliasMap, ColumnEntityRelation rootRelation) {
        if (selectStatement.getSelectItems() == null) {
            return;
        }
        for (com.mybatisgx.dsl.mgxql.model.SelectItem selectItem : selectStatement.getSelectItems()) {
            SelectItemType type = selectItem.getType();
            if (type == null) {
                continue;
            }
            if (type == SelectItemType.COLUMN_ALL) {
                this.addColumnAllSelectItems(plainSelect, selectItem, aliasMap, rootRelation);
            } else if (type == SelectItemType.COLUMN) {
                this.addColumnSelectItem(plainSelect, selectItem, aliasMap);
            } else if (type.hasAggregateFunction()) {
                this.addAggregateSelectItem(plainSelect, selectItem, aliasMap);
            }
        }
    }

    /**
     * COLUMN_ALL 投影：复用 {@link SelectColumnSqlTemplateHandler#buildSimpleSelectSql}
     * 的列展开逻辑（含 id 复合列、外键列），保证与 result map 列集一致，仅取其 select items。
     */
    @SuppressWarnings("unchecked")
    private void addColumnAllSelectItems(PlainSelect plainSelect, com.mybatisgx.dsl.mgxql.model.SelectItem selectItem,
                                          Map<String, ColumnEntityRelation> aliasMap, ColumnEntityRelation rootRelation) {
        String entityAlias = selectItem.getEntityAlias();
        ColumnEntityRelation node;
        if (StringUtils.isBlank(entityAlias)) {
            // select *：单实体场景取树根
            node = rootRelation;
        } else {
            node = aliasMap.get(entityAlias);
        }
        if (node == null) {
            plainSelect.addSelectItems(new AllColumns());
            return;
        }
        PlainSelect temp = selectColumnSqlTemplateHandler.buildSimpleSelectSql(node);
        List<SelectItem<?>> selectItemList = temp.getSelectItems();
        if (ObjectUtils.isNotEmpty(selectItemList)) {
            plainSelect.addSelectItems(selectItemList);
        }
    }

    /**
     * COLUMN 投影：发 {@code tableNameAlias.dbColumnName AS tableNameAlias_dbColumnNameAlias}，
     * 别名经 {@link ColumnInfo#getTableColumnNameAlias(ColumnEntityRelation)} 与 result map 对齐。
     */
    private void addColumnSelectItem(PlainSelect plainSelect, com.mybatisgx.dsl.mgxql.model.SelectItem selectItem,
                                     Map<String, ColumnEntityRelation> aliasMap) {
        ColumnInfo columnInfo = selectItem.getColumnInfo();
        if (columnInfo == null) {
            return;
        }
        String entityAlias = selectItem.getEntityAlias();
        ColumnEntityRelation treeNode = aliasMap.get(entityAlias);
        String tableAlias = this.resolveTableAlias(entityAlias, aliasMap);
        String columnAlias;
        if (treeNode != null) {
            columnAlias = columnInfo.getTableColumnNameAlias(treeNode);
        } else {
            String dbAlias = StringUtils.isNotBlank(columnInfo.getDbColumnNameAlias())
                    ? columnInfo.getDbColumnNameAlias() : columnInfo.getDbColumnName();
            columnAlias = tableAlias + "_" + dbAlias;
        }
        Table table = new Table(tableAlias);
        SelectItem<Column> item = new SelectItem<Column>();
        item.withExpression(new Column(table, columnInfo.getDbColumnName()));
        item.setAlias(new Alias(columnAlias));
        plainSelect.addSelectItems(item);
    }

    /**
     * 聚合投影：COUNT/SUM/MAX/MIN/AVG，走 resultType 路径，无 result map。
     * 有字段引用时发 {@code FUNC(tableNameAlias.dbColumnName)}，无字段引用时发 {@code FUNC(*)}。
     */
    private void addAggregateSelectItem(PlainSelect plainSelect, com.mybatisgx.dsl.mgxql.model.SelectItem selectItem,
                                        Map<String, ColumnEntityRelation> aliasMap) {
        String funcName;
        switch (selectItem.getType()) {
            case COUNT:
                funcName = "COUNT";
                break;
            case SUM:
                funcName = "SUM";
                break;
            case MAX:
                funcName = "MAX";
                break;
            case MIN:
                funcName = "MIN";
                break;
            case AVG:
                funcName = "AVG";
                break;
            default:
                funcName = "COUNT";
                break;
        }
        Function function = new Function();
        function.setName(funcName);
        FieldReference fieldRef = selectItem.getAggregateFieldRef();
        if (fieldRef != null && fieldRef.getColumnInfo() != null) {
            String tableAlias = this.resolveTableAlias(fieldRef.getEntityAlias(), aliasMap);
            Column col = new Column(new Table(tableAlias), fieldRef.getColumnInfo().getDbColumnName());
            function.setParameters(new ExpressionList(Arrays.asList((Expression) col)));
        } else {
            function.setParameters(new ExpressionList(Arrays.asList((Expression) new AllColumns())));
        }
        Expression aggExpression = new TableFunction(function);
        plainSelect.addSelectItems(aggExpression);
    }

    private Table buildFromItem(PlainSelect plainSelect, String mainTableName, String mainTableNameAlias) {
        Table mainTable = new Table(mainTableName);
        if (StringUtils.isNotBlank(mainTableNameAlias)) {
            mainTable.setAlias(new Alias(mainTableNameAlias));
        }
        plainSelect.setFromItem(mainTable);
        return mainTable;
    }

    private void buildJoins(PlainSelect plainSelect, FromClause fromClause, Map<String, ColumnEntityRelation> aliasMap) {
        if (fromClause == null || fromClause.getJoinEntities() == null) {
            return;
        }
        for (JoinEntity joinEntity : fromClause.getJoinEntities()) {
            RelationColumnInfo relationColumnInfo = joinEntity.getRelationColumnInfo();
            String rightTableAlias = this.resolveTableAlias(joinEntity.getAlias(), aliasMap);
            ColumnEntityRelation rightTreeNode = aliasMap.get(joinEntity.getAlias());
            String leftTableAlias = this.resolveTableAlias(joinEntity.getOnLeftAlias(), aliasMap);

            if (relationColumnInfo != null
                    && relationColumnInfo.getRelationType() == RelationType.MANY_TO_MANY
                    && rightTreeNode != null) {
                // 多对多：自动补充中间表 + 实体表两次 join
                this.buildManyToManyJoin(plainSelect, rightTreeNode, leftTableAlias, rightTableAlias);
            } else if (relationColumnInfo != null) {
                // 一对一 / 一对多 / 多对一
                String rightTableName = joinEntity.getEntityInfo() != null
                        ? joinEntity.getEntityInfo().getTableName() : rightTreeNode.getTableName();
                Join join = this.buildLeftJoin(rightTableName, rightTableAlias);
                this.buildOnExpression(join, relationColumnInfo, leftTableAlias, rightTableAlias);
                plainSelect.addJoins(join);
            } else {
                // 无绑定关系（校验阶段应已报错），仅发裸 join 容错
                String rightTableName = joinEntity.getEntityInfo() != null
                        ? joinEntity.getEntityInfo().getTableName() : null;
                plainSelect.addJoins(this.buildLeftJoin(rightTableName, rightTableAlias));
            }
        }
    }

    /**
     * 多对多自动补充中间表：
     * <code>
     * LEFT JOIN user_role ON leftAlias.user_id_col = user_role.fk
     * LEFT JOIN role AS rightAlias ON user_role.fk = rightAlias.role_id_col
     * </code>
     */
    private void buildManyToManyJoin(PlainSelect plainSelect, ColumnEntityRelation rightTreeNode,
                                      String leftTableAlias, String rightTableAlias) {
        String middleTableName = rightTreeNode.getMiddleTableName();
        String entityTableName = rightTreeNode.getTableName();

        // 第一次 join：中间表
        Join middleJoin = this.buildLeftJoin(middleTableName, null);
        List<ForeignKeyInfo> middleFkList = rightTreeNode.isMappedBy()
                ? rightTreeNode.getInverseForeignKeyColumnInfoList()
                : rightTreeNode.getForeignKeyColumnInfoList();
        this.buildEntityTableOnMiddleTable(leftTableAlias, middleTableName, middleFkList, middleJoin);
        plainSelect.addJoins(middleJoin);

        // 第二次 join：实体表
        Join entityJoin = this.buildLeftJoin(entityTableName, rightTableAlias);
        List<ForeignKeyInfo> entityFkList = rightTreeNode.isMappedBy()
                ? rightTreeNode.getForeignKeyColumnInfoList()
                : rightTreeNode.getInverseForeignKeyColumnInfoList();
        this.buildMiddleTableOnEntityTable(middleTableName, rightTableAlias, entityFkList, entityJoin);
        plainSelect.addJoins(entityJoin);
    }

    private Join buildLeftJoin(String rightTableName, String rightTableNameAlias) {
        Table table = new Table(rightTableName);
        if (StringUtils.isNotBlank(rightTableNameAlias)) {
            table.setAlias(new Alias(rightTableNameAlias));
        }
        Join join = new Join();
        join.setLeft(true);
        join.setRightItem(table);
        return join;
    }

    /**
     * ON 条件由 relationColumnInfo 的外键自动推导，复刻
     * {@link SelectColumnSqlTemplateHandler} 的 isMappedBy 分支方向。
     */
    private void buildOnExpression(Join join, RelationColumnInfo relationColumnInfo,
                                   String leftTableAlias, String rightTableAlias) {
        RelationColumnInfo mappedByRelation = relationColumnInfo.getMappedByRelationColumnInfo();
        boolean isMappedBy = mappedByRelation != null;
        List<ForeignKeyInfo> fkList = isMappedBy
                ? mappedByRelation.getInverseForeignKeyInfoList()
                : relationColumnInfo.getInverseForeignKeyInfoList();
        List<Expression> onExpressionList = new ArrayList<>();
        for (ForeignKeyInfo fkInfo : fkList) {
            ColumnInfo foreignKeyColumnInfo = fkInfo.getColumnInfo();
            ColumnInfo referencedColumnInfo = fkInfo.getReferencedColumnInfo();
            if (ObjectUtils.isNotEmpty(referencedColumnInfo.getComposites())) {
                referencedColumnInfo = referencedColumnInfo.getComposites().get(0);
            }
            String leftExpression;
            String rightExpression;
            if (isMappedBy) {
                leftExpression = leftTableAlias + "." + referencedColumnInfo.getDbColumnName();
                rightExpression = rightTableAlias + "." + foreignKeyColumnInfo.getDbColumnName();
            } else {
                leftExpression = leftTableAlias + "." + foreignKeyColumnInfo.getDbColumnName();
                rightExpression = rightTableAlias + "." + referencedColumnInfo.getDbColumnName();
            }
            EqualsTo onCondition = MybatisgxSqlBuilder.eq(leftExpression, rightExpression);
            onExpressionList.add(onCondition);
        }
        Expression expression = null;
        for (Expression onExpression : onExpressionList) {
            if (expression == null) {
                expression = onExpression;
            } else {
                expression = MybatisgxSqlBuilder.and(expression, onExpression);
            }
        }
        join.addOnExpression(expression);
    }

    private void buildEntityTableOnMiddleTable(String entityTableNameAlias, String middleTableName,
                                               List<ForeignKeyInfo> foreignKeyColumnInfoList, Join join) {
        List<Expression> onExpressionList = new ArrayList<Expression>();
        for (ForeignKeyInfo foreignKeyInfo : foreignKeyColumnInfoList) {
            ColumnInfo foreignKeyColumnInfo = foreignKeyInfo.getColumnInfo();
            ColumnInfo referencedColumnInfo = foreignKeyInfo.getReferencedColumnInfo();
            String leftExpression = entityTableNameAlias + "." + referencedColumnInfo.getDbColumnName();
            String rightExpression = middleTableName + "." + foreignKeyColumnInfo.getDbColumnName();
            onExpressionList.add(MybatisgxSqlBuilder.eq(leftExpression, rightExpression));
        }
        join.addOnExpression(this.combineAnd(onExpressionList));
    }

    private void buildMiddleTableOnEntityTable(String middleTableName, String entityTableNameAlias,
                                              List<ForeignKeyInfo> foreignKeyColumnInfoList, Join join) {
        List<Expression> onExpressionList = new ArrayList<Expression>();
        for (ForeignKeyInfo foreignKeyInfo : foreignKeyColumnInfoList) {
            ColumnInfo foreignKeyColumnInfo = foreignKeyInfo.getColumnInfo();
            ColumnInfo referencedColumnInfo = foreignKeyInfo.getReferencedColumnInfo();
            String leftExpression = middleTableName + "." + foreignKeyColumnInfo.getDbColumnName();
            String rightExpression = entityTableNameAlias + "." + referencedColumnInfo.getDbColumnName();
            onExpressionList.add(MybatisgxSqlBuilder.eq(leftExpression, rightExpression));
        }
        join.addOnExpression(this.combineAnd(onExpressionList));
    }

    private Expression combineAnd(List<Expression> onExpressionList) {
        Expression expression = null;
        for (Expression onExpression : onExpressionList) {
            if (expression == null) {
                expression = onExpression;
            } else {
                expression = MybatisgxSqlBuilder.and(expression, onExpression);
            }
        }
        return expression;
    }
}
