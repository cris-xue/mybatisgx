package com.mybatisgx.template.select;

import com.mybatisgx.dsl.mgxql.model.*;
import com.mybatisgx.exception.MybatisgxException;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
public class MgxqlSelectTemplateHandler {

    private static final Logger logger = LoggerFactory.getLogger(MgxqlSelectTemplateHandler.class);

    /**
     * 构建 MGXQL 查询 SQL。
     *
     * @param selectStatement MGXQL SELECT 语句（fromClause 必须存在）
     * @param aliasContext 别名上下文（统一提供节点查询、别名解析、FROM 实体查询）
     * @return 渲染完成的 PlainSelect
     */
    public PlainSelect buildSelectSql(SelectStatement selectStatement, AliasContext aliasContext) {
        PlainSelect plainSelect = new PlainSelect();
        new SelectItemsRenderer(aliasContext).render(plainSelect, selectStatement);
        FromJoinRenderer fromJoinRenderer = new FromJoinRenderer(aliasContext);
        fromJoinRenderer.renderFrom(plainSelect);
        fromJoinRenderer.renderJoin(plainSelect, selectStatement.getFromClause());
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

        private final AliasContext aliasContext;
        private SelectItemClauseBuilder selectItemClauseBuilder = new SelectItemClauseBuilder();

        SelectItemsRenderer(AliasContext aliasContext) {
            this.aliasContext = aliasContext;
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
         * COLUMN_ALL 投影：基于 FROM 实体 {@link EntityInfo} 展开，不依赖投影树节点。
         * <p>
         * select alias.* → 查找别名为 alias 的 FROM 实体，展开其全部字段；
         * select * → 遍历 FROM/JOIN 所有实体，逐个展开全部字段。
         */
        private void addColumnAllSelectItems(PlainSelect plainSelect, com.mybatisgx.dsl.mgxql.model.SelectItem mgxqlSelectItem) {
            String entityAlias = mgxqlSelectItem.getEntityAlias();
            if (StringUtils.isNotBlank(entityAlias)) {
                // select alias.*：基于 FROM 实体展开
                FromEntity fromEntity = this.aliasContext.getFromEntity(entityAlias);
                if (fromEntity != null && fromEntity.getEntityInfo() != null) {
                    String tableNameAlias = this.aliasContext.resolveTableAlias(fromEntity);

                    ColumnEntityRelation tempRelation = new ColumnEntityRelation<>();
                    tempRelation.setEntityInfo(fromEntity.getEntityInfo());
                    tempRelation.setTableNameAlias(tableNameAlias);
                    this.expandEntityColumns(plainSelect, tempRelation, tableNameAlias);
                }
                return;
            }
            // select *：遍历 fromClause 全部实体（主 + JOIN）
            FromClause fromClause = this.aliasContext.getFromClause();
            if (fromClause == null) {
                return;
            }
            FromEntity primaryEntity = fromClause.getPrimaryEntity();
            if (primaryEntity != null && primaryEntity.getEntityInfo() != null) {
                String tableNameAlias = this.aliasContext.resolveTableAlias(primaryEntity);

                ColumnEntityRelation tempRelation = new ColumnEntityRelation<>();
                tempRelation.setEntityInfo(primaryEntity.getEntityInfo());
                tempRelation.setTableNameAlias(tableNameAlias);

                this.expandEntityColumns(plainSelect, tempRelation, tableNameAlias);
            }
            if (fromClause.getJoinEntities() != null) {
                for (JoinEntity joinEntity : fromClause.getJoinEntities()) {
                    if (joinEntity.getEntityInfo() != null) {
                        String tableNameAlias = this.aliasContext.resolveTableAlias(joinEntity);

                        ColumnEntityRelation tempRelation = new ColumnEntityRelation<>();
                        tempRelation.setEntityInfo(joinEntity.getEntityInfo());
                        tempRelation.setTableNameAlias(tableNameAlias);

                        this.expandEntityColumns(plainSelect, tempRelation, tableNameAlias);
                    }
                }
            }
        }

        /**
         * 复用 SelectColumnSqlTemplateHandler.buildSimpleSelectSql 的列展开逻辑（含 id 复合列、外键列），
         * 取其 select items 并将列引用的表前缀统一覆盖为 MGXQL 用户别名。
         */
        private void expandEntityColumns(PlainSelect plainSelect, ColumnEntityRelation columnEntityRelation, String tablePrefix) {
            List<SelectItem<?>> selectItemList = this.selectItemClauseBuilder.buildSelectItemList(columnEntityRelation, columnEntityRelation.getEntityInfo(), false);
            if (ObjectUtils.isNotEmpty(selectItemList)) {
                for (SelectItem<?> item : selectItemList) {
                    Expression expr = item.getExpression();
                    if (expr instanceof Column) {
                        ((Column) expr).setTable(new Table(tablePrefix));
                    }
                }
                plainSelect.addSelectItems(selectItemList);
            }
        }

        /**
         * COLUMN 投影：基于 FROM 实体 {@link EntityInfo} 获取 {@link ColumnInfo}，
         * 发 {@code tableAlias.dbColumnName AS tableAlias_dbColumnNameAlias}，
         * 别名经 {@link ColumnInfo#getTableColumnNameAlias(ColumnEntityRelation)} 与 result map 对齐。
         */
        private void addColumnSelectItem(PlainSelect plainSelect, com.mybatisgx.dsl.mgxql.model.SelectItem mgxqlSelectItem) {
            ColumnInfo columnInfo = mgxqlSelectItem.getColumnInfo();
            if (columnInfo == null) {
                return;
            }
            String entityAlias = mgxqlSelectItem.getEntityAlias();
            FromEntity fromEntity = this.aliasContext.getFromEntity(entityAlias);
            String tableNameAlias = this.aliasContext.resolveTableAlias(fromEntity);
            String columnAlias;
            if (fromEntity != null && fromEntity.getEntityInfo() != null) {
                // 用 FROM 实体的 EntityInfo 构造临时节点生成列别名
                ColumnEntityRelation tempRelation = new ColumnEntityRelation<>();
                tempRelation.setEntityInfo(fromEntity.getEntityInfo());
                tempRelation.setTableNameAlias(tableNameAlias);
                columnAlias = columnInfo.getTableColumnNameAlias(tempRelation);
            } else {
                String dbAlias = StringUtils.isNotBlank(columnInfo.getDbColumnNameAlias()) ? columnInfo.getDbColumnNameAlias() : columnInfo.getDbColumnName();
                columnAlias = tableNameAlias + "_" + dbAlias;
            }

            Table table = new Table(tableNameAlias);
            SelectItem<Column> selectItem = selectItemClauseBuilder.buildSelectItem(table, columnInfo.getDbColumnName(), columnAlias);
            plainSelect.addSelectItems(selectItem);
        }

        /**
         * 聚合投影：COUNT/SUM/MAX/MIN/AVG，走 resultType 路径，无 result map。
         * 有字段引用时发 {@code FUNC(tableNameAlias.dbColumnName)}，无字段引用时发 {@code FUNC(*)}。
         */
        private void addAggregateSelectItem(PlainSelect plainSelect, com.mybatisgx.dsl.mgxql.model.SelectItem mgxqlSelectItem) {
            String funcName = mgxqlSelectItem.getType().getValue();
            Function function = new Function();
            function.setName(funcName);
            FieldReference fieldReference = mgxqlSelectItem.getFieldRef();
            if (fieldReference != null && fieldReference.getColumnInfo() != null) {
                String tableAlias = this.aliasContext.resolveTableAlias(fieldReference.getEntityAlias());
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
     * JOIN 渲染器：渲染 LEFT JOIN 及 ON 条件（含多对多中间表补 join）。
     */
    private static class FromJoinRenderer {

        private SelectFromJoinClauseBuilder selectFromJoinClauseBuilder = new SelectFromJoinClauseBuilder();
        private final AliasContext aliasContext;

        FromJoinRenderer(AliasContext aliasContext) {
            this.aliasContext = aliasContext;
        }

        void renderFrom(PlainSelect plainSelect) {
            FromEntity fromEntity = this.aliasContext.getFromClause().getPrimaryEntity();

            Table mainTable = new Table(this.aliasContext.getMainTableName());
            mainTable.setAlias(new Alias(this.aliasContext.resolveTableAlias(fromEntity)));
            plainSelect.setFromItem(mainTable);
        }

        void renderJoin(PlainSelect plainSelect, FromClause fromClause) {
            if (fromClause == null || fromClause.getJoinEntities() == null) {
                return;
            }
            for (JoinEntity joinEntity : fromClause.getJoinEntities()) {
                RelationColumnInfo relationColumnInfo = joinEntity.getRelationColumnInfo();
                ColumnEntityRelation rightColumnEntityRelation = this.aliasContext.getNode(joinEntity);
                String rightTableAlias = this.aliasContext.resolveTableAlias(joinEntity);

                FromEntity leftEntity = this.aliasContext.getFromEntity(joinEntity.getOnLeftAlias());
                String leftTableAlias = this.aliasContext.resolveTableAlias(leftEntity);

                if (relationColumnInfo != null && relationColumnInfo.getRelationType() == RelationType.MANY_TO_MANY) {
                    // 多对多：自动补充中间表 + 实体表两次 join
                    this.buildManyToManyJoin(plainSelect, rightColumnEntityRelation, leftTableAlias, rightTableAlias);
                } else if (relationColumnInfo != null) {
                    // 一对一 / 一对多 / 多对一
                    String rightTableName = rightColumnEntityRelation.getTableName();
                    Join join = selectFromJoinClauseBuilder.buildLeftJoin(rightTableName, rightTableAlias);
                    this.buildOnExpression(join, relationColumnInfo, leftTableAlias, rightTableAlias);
                    plainSelect.addJoins(join);
                } else {
                    throw new MybatisgxException("不存在的关联关系");
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
            Join middleJoin = selectFromJoinClauseBuilder.buildLeftJoin(middleTableName, null);
            List<ForeignKeyInfo> middleFkList = rightTreeNode.isMappedBy() ? rightTreeNode.getInverseForeignKeyColumnInfoList() : rightTreeNode.getForeignKeyColumnInfoList();
            this.buildEntityTableOnMiddleTable(leftTableAlias, middleTableName, middleFkList, middleJoin);
            plainSelect.addJoins(middleJoin);

            // 第二次 join：实体表
            Join entityJoin = selectFromJoinClauseBuilder.buildLeftJoin(entityTableName, rightTableAlias);
            List<ForeignKeyInfo> entityFkList = rightTreeNode.isMappedBy() ? rightTreeNode.getForeignKeyColumnInfoList() : rightTreeNode.getInverseForeignKeyColumnInfoList();
            this.buildMiddleTableOnEntityTable(middleTableName, rightTableAlias, entityFkList, entityJoin);
            plainSelect.addJoins(entityJoin);
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
            Expression expression = selectFromJoinClauseBuilder.combineAnd(onExpressionList);
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
            join.addOnExpression(selectFromJoinClauseBuilder.combineAnd(onExpressionList));
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
            join.addOnExpression(selectFromJoinClauseBuilder.combineAnd(onExpressionList));
        }
    }
}
