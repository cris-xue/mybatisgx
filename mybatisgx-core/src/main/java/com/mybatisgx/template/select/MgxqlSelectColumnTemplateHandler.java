package com.mybatisgx.template.select;

import com.mybatisgx.dsl.mgxql.model.SelectItemType;
import com.mybatisgx.dsl.mgxql.model.*;
import com.mybatisgx.model.*;
import com.mybatisgx.template.MybatisgxSqlBuilder;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.select.*;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

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
 * <p>
 * 本类为纯编排器，渲染流程分四阶段：先由 {@link AliasContext} 准备别名上下文，
 * 再依次交由 {@link SelectItemsRenderer}、{@link FromRenderer}、{@link JoinRenderer} 渲染。
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
        AliasContext ctx = AliasContext.build(selectStatement, rootRelation);
        PlainSelect plainSelect = new PlainSelect();
        new SelectItemsRenderer(ctx, this.selectColumnSqlTemplateHandler).render(plainSelect, selectStatement);
        new FromRenderer(ctx).render(plainSelect);
        new JoinRenderer(ctx).render(plainSelect, selectStatement.getFromClause());
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
     * SELECT 列渲染器：按 {@link com.mybatisgx.dsl.mgxql.model.SelectItem} 投影渲染 SELECT 列。
     */
    private static class SelectItemsRenderer {

        private final AliasContext ctx;
        private final SelectColumnSqlTemplateHandler selectColumnSqlTemplateHandler;

        SelectItemsRenderer(AliasContext ctx, SelectColumnSqlTemplateHandler selectColumnSqlTemplateHandler) {
            this.ctx = ctx;
            this.selectColumnSqlTemplateHandler = selectColumnSqlTemplateHandler;
        }

        void render(PlainSelect plainSelect, SelectStatement selectStatement) {
            if (selectStatement.getSelectItems() == null) {
                return;
            }
            for (com.mybatisgx.dsl.mgxql.model.SelectItem selectItem : selectStatement.getSelectItems()) {
                SelectItemType type = selectItem.getType();
                if (type == null) {
                    continue;
                }
                if (type == SelectItemType.COLUMN_ALL) {
                    this.addColumnAllSelectItems(plainSelect, selectItem);
                } else if (type == SelectItemType.COLUMN) {
                    this.addColumnSelectItem(plainSelect, selectItem);
                } else if (type.hasAggregateFunction()) {
                    this.addAggregateSelectItem(plainSelect, selectItem);
                }
            }
        }

        /**
         * COLUMN_ALL 投影：复用 {@link SelectColumnSqlTemplateHandler#buildSimpleSelectSql}
         * 的列展开逻辑（含 id 复合列、外键列），保证与 result map 列集一致，仅取其 select items。
         */
        @SuppressWarnings("unchecked")
        private void addColumnAllSelectItems(PlainSelect plainSelect, com.mybatisgx.dsl.mgxql.model.SelectItem selectItem) {
            String entityAlias = selectItem.getEntityAlias();
            ColumnEntityRelation node;
            if (StringUtils.isBlank(entityAlias)) {
                // select *：单实体场景取树根
                node = this.ctx.getRootRelation();
            } else {
                node = this.ctx.getNode(entityAlias);
            }
            if (node == null) {
                plainSelect.addSelectItems(new AllColumns());
                return;
            }
            PlainSelect temp = this.selectColumnSqlTemplateHandler.buildSimpleSelectSql(node);
            List<SelectItem<?>> selectItemList = temp.getSelectItems();
            if (ObjectUtils.isNotEmpty(selectItemList)) {
                plainSelect.addSelectItems(selectItemList);
            }
        }

        /**
         * COLUMN 投影：发 {@code tableNameAlias.dbColumnName AS tableNameAlias_dbColumnNameAlias}，
         * 别名经 {@link ColumnInfo#getTableColumnNameAlias(ColumnEntityRelation)} 与 result map 对齐。
         */
        private void addColumnSelectItem(PlainSelect plainSelect, com.mybatisgx.dsl.mgxql.model.SelectItem selectItem) {
            ColumnInfo columnInfo = selectItem.getColumnInfo();
            if (columnInfo == null) {
                return;
            }
            String entityAlias = selectItem.getEntityAlias();
            ColumnEntityRelation treeNode = this.ctx.getNode(entityAlias);
            String tableAlias = this.ctx.resolveTableAlias(entityAlias);
            String columnAlias;
            if (treeNode != null) {
                columnAlias = columnInfo.getTableColumnNameAlias(treeNode);
            } else {
                String dbAlias = StringUtils.isNotBlank(columnInfo.getDbColumnNameAlias()) ? columnInfo.getDbColumnNameAlias() : columnInfo.getDbColumnName();
                columnAlias = tableAlias + "_" + dbAlias;
            }
            Table table = new Table(tableAlias);
            SelectItem<Column> item = new SelectItem<>();
            item.withExpression(new Column(table, columnInfo.getDbColumnName()));
            item.setAlias(new Alias(columnAlias));
            plainSelect.addSelectItems(item);
        }

        /**
         * 聚合投影：COUNT/SUM/MAX/MIN/AVG，走 resultType 路径，无 result map。
         * 有字段引用时发 {@code FUNC(tableNameAlias.dbColumnName)}，无字段引用时发 {@code FUNC(*)}。
         */
        private void addAggregateSelectItem(PlainSelect plainSelect, com.mybatisgx.dsl.mgxql.model.SelectItem selectItem) {
            String funcName = selectItem.getType().getValue();
            Function function = new Function();
            function.setName(funcName);
            FieldReference fieldReference = selectItem.getFieldRef();
            if (fieldReference != null && fieldReference.getColumnInfo() != null) {
                String tableAlias = this.ctx.resolveTableAlias(fieldReference.getEntityAlias());
                Column column = new Column(new Table(tableAlias), fieldReference.getColumnInfo().getDbColumnName());
                function.setParameters(new ExpressionList(Arrays.asList((Expression) column)));
            } else {
                function.setParameters(new ExpressionList(Arrays.asList((Expression) new AllColumns())));
            }
            Expression aggExpression = new TableFunction(function);
            plainSelect.addSelectItems(aggExpression);
        }
    }

    /**
     * FROM 渲染器：渲染主表 FROM 及其别名。
     */
    private static class FromRenderer {

        private final AliasContext ctx;

        FromRenderer(AliasContext ctx) {
            this.ctx = ctx;
        }

        void render(PlainSelect plainSelect) {
            Table mainTable = new Table(this.ctx.getMainTableName());
            if (StringUtils.isNotBlank(this.ctx.getMainTableAlias())) {
                mainTable.setAlias(new Alias(this.ctx.getMainTableAlias()));
            }
            plainSelect.setFromItem(mainTable);
        }
    }

    /**
     * JOIN 渲染器：渲染 LEFT JOIN 及 ON 条件（含多对多中间表补 join）。
     */
    private static class JoinRenderer {

        private final AliasContext ctx;

        JoinRenderer(AliasContext ctx) {
            this.ctx = ctx;
        }

        void render(PlainSelect plainSelect, FromClause fromClause) {
            if (fromClause == null || fromClause.getJoinEntities() == null) {
                return;
            }
            for (JoinEntity joinEntity : fromClause.getJoinEntities()) {
                RelationColumnInfo relationColumnInfo = joinEntity.getRelationColumnInfo();
                String rightTableAlias = this.ctx.resolveTableAlias(joinEntity.getAlias());
                ColumnEntityRelation rightTreeNode = this.ctx.getNode(joinEntity.getAlias());
                String leftTableAlias = this.ctx.resolveTableAlias(joinEntity.getOnLeftAlias());

                if (relationColumnInfo != null && relationColumnInfo.getRelationType() == RelationType.MANY_TO_MANY && rightTreeNode != null) {
                    // 多对多：自动补充中间表 + 实体表两次 join
                    this.buildManyToManyJoin(plainSelect, rightTreeNode, leftTableAlias, rightTableAlias);
                } else if (relationColumnInfo != null) {
                    // 一对一 / 一对多 / 多对一
                    String rightTableName = joinEntity.getEntityInfo() != null ? joinEntity.getEntityInfo().getTableName() : rightTreeNode.getTableName();
                    Join join = this.buildLeftJoin(rightTableName, rightTableAlias);
                    this.buildOnExpression(join, relationColumnInfo, leftTableAlias, rightTableAlias);
                    plainSelect.addJoins(join);
                } else {
                    // 无绑定关系（校验阶段应已报错），仅发裸 join 容错
                    String rightTableName = joinEntity.getEntityInfo() != null ? joinEntity.getEntityInfo().getTableName() : null;
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
        private void buildManyToManyJoin(PlainSelect plainSelect, ColumnEntityRelation rightTreeNode, String leftTableAlias, String rightTableAlias) {
            String middleTableName = rightTreeNode.getMiddleTableName();
            String entityTableName = rightTreeNode.getTableName();

            // 第一次 join：中间表
            Join middleJoin = this.buildLeftJoin(middleTableName, null);
            List<ForeignKeyInfo> middleFkList = rightTreeNode.isMappedBy() ? rightTreeNode.getInverseForeignKeyColumnInfoList() : rightTreeNode.getForeignKeyColumnInfoList();
            this.buildEntityTableOnMiddleTable(leftTableAlias, middleTableName, middleFkList, middleJoin);
            plainSelect.addJoins(middleJoin);

            // 第二次 join：实体表
            Join entityJoin = this.buildLeftJoin(entityTableName, rightTableAlias);
            List<ForeignKeyInfo> entityFkList = rightTreeNode.isMappedBy() ? rightTreeNode.getForeignKeyColumnInfoList() : rightTreeNode.getInverseForeignKeyColumnInfoList();
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
        private void buildOnExpression(Join join, RelationColumnInfo relationColumnInfo, String leftTableAlias, String rightTableAlias) {
            RelationColumnInfo mappedByRelation = relationColumnInfo.getMappedByRelationColumnInfo();
            boolean isMappedBy = mappedByRelation != null;
            List<ForeignKeyInfo> fkList = isMappedBy ? mappedByRelation.getInverseForeignKeyInfoList() : relationColumnInfo.getInverseForeignKeyInfoList();
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

        private void buildEntityTableOnMiddleTable(String entityTableNameAlias, String middleTableName, List<ForeignKeyInfo> foreignKeyColumnInfoList, Join join) {
            List<Expression> onExpressionList = new ArrayList<>();
            for (ForeignKeyInfo foreignKeyInfo : foreignKeyColumnInfoList) {
                ColumnInfo foreignKeyColumnInfo = foreignKeyInfo.getColumnInfo();
                ColumnInfo referencedColumnInfo = foreignKeyInfo.getReferencedColumnInfo();
                String leftExpression = entityTableNameAlias + "." + referencedColumnInfo.getDbColumnName();
                String rightExpression = middleTableName + "." + foreignKeyColumnInfo.getDbColumnName();
                onExpressionList.add(MybatisgxSqlBuilder.eq(leftExpression, rightExpression));
            }
            join.addOnExpression(this.combineAnd(onExpressionList));
        }

        private void buildMiddleTableOnEntityTable(String middleTableName, String entityTableNameAlias, List<ForeignKeyInfo> foreignKeyColumnInfoList, Join join) {
            List<Expression> onExpressionList = new ArrayList<>();
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

    /**
     * 别名上下文：构建并查询 MGXQL 用户别名 → 注解树节点 映射，封装无树时回退用户别名的约定。
     * <p>
     * 主表名/别名与树根关系也在准备阶段一并收敛，渲染器不再直接访问 aliasMap 或 rootRelation。
     */
    private static class AliasContext {

        private final Map<String, ColumnEntityRelation> aliasMap;
        private final String mainTableName;
        private final String mainTableAlias;
        private final ColumnEntityRelation rootRelation;

        private AliasContext(Map<String, ColumnEntityRelation> aliasMap, String mainTableName, String mainTableAlias, ColumnEntityRelation rootRelation) {
            this.aliasMap = aliasMap;
            this.mainTableName = mainTableName;
            this.mainTableAlias = mainTableAlias;
            this.rootRelation = rootRelation;
        }

        static AliasContext build(SelectStatement selectStatement, ColumnEntityRelation rootRelation) {
            FromClause fromClause = selectStatement.getFromClause();
            Map<String, ColumnEntityRelation> aliasMap;
            String mainTableName;
            String mainTableAlias;
            if (rootRelation != null) {
                aliasMap = buildAliasTreeNodeMap(fromClause, rootRelation);
                mainTableName = rootRelation.getTableName();
                mainTableAlias = rootRelation.getTableNameAlias();
            } else {
                // 无注解树（聚合等场景未构建树）：以主实体 entityInfo + 用户别名兜底
                aliasMap = buildAliasMapNoTree(fromClause);
                FromEntity primaryEntity = fromClause.getPrimaryEntity();
                mainTableName = (primaryEntity != null && primaryEntity.getEntityInfo() != null) ? primaryEntity.getEntityInfo().getTableName() : null;
                mainTableAlias = (primaryEntity != null) ? primaryEntity.getAlias() : null;
            }
            return new AliasContext(aliasMap, mainTableName, mainTableAlias, rootRelation);
        }

        /**
         * 解析渲染使用的表别名：树节点存在用注解 tableNameAlias，否则回退 MGXQL 用户别名。
         */
        String resolveTableAlias(String alias) {
            if (StringUtils.isBlank(alias)) {
                return alias;
            }
            ColumnEntityRelation node = this.aliasMap.get(alias);
            if (node != null && StringUtils.isNotBlank(node.getTableNameAlias())) {
                return node.getTableNameAlias();
            }
            return alias;
        }

        ColumnEntityRelation getNode(String alias) {
            return this.aliasMap.get(alias);
        }

        String getMainTableName() {
            return this.mainTableName;
        }

        String getMainTableAlias() {
            return this.mainTableAlias;
        }

        ColumnEntityRelation getRootRelation() {
            return this.rootRelation;
        }

        /**
         * 建立 MGXQL 用户别名 → 注解树节点 映射。
         * <p>
         * 主实体映射到树根；各 JOIN 实体沿 onLeftAlias 找到左实体树节点，在其 composites 中
         * 按 relationColumnInfo 身份（优先）或 entityInfo.clazz 匹配对应子节点。匹配不到时
         * 保留 null（渲染回退到用户别名，该实体不在 result map 中）。
         */
        private static Map<String, ColumnEntityRelation> buildAliasTreeNodeMap(FromClause fromClause, ColumnEntityRelation rootRelation) {
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
                    rightNode = findChildTreeNode(leftNode, joinEntity);
                }
                if (rightNode == null && joinEntity.getEntityInfo() != null) {
                    rightNode = findTreeNodeByClazz(rootRelation, joinEntity.getEntityInfo().getClazz());
                }
                aliasMap.put(joinEntity.getAlias(), rightNode);
            }
            return aliasMap;
        }

        /**
         * 无注解树场景（聚合等）的别名映射：所有别名映射到 null，
         * {@link #resolveTableAlias} 回退到 MGXQL 用户别名。
         */
        private static Map<String, ColumnEntityRelation> buildAliasMapNoTree(FromClause fromClause) {
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
        private static ColumnEntityRelation findChildTreeNode(ColumnEntityRelation leftNode, JoinEntity joinEntity) {
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
        private static ColumnEntityRelation findTreeNodeByClazz(ColumnEntityRelation node, Class<?> clazz) {
            if (node == null || clazz == null) {
                return null;
            }
            if (node.getEntityInfo() != null && clazz.equals(node.getEntityInfo().getClazz())) {
                return node;
            }
            if (node.getComposites() != null) {
                for (Object child : node.getComposites()) {
                    ColumnEntityRelation found = findTreeNodeByClazz((ColumnEntityRelation) child, clazz);
                    if (found != null) {
                        return found;
                    }
                }
            }
            return null;
        }
    }
}
