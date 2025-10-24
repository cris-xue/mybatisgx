package com.lc.mybatisx.template.select;

import com.lc.mybatisx.model.*;
import com.lc.mybatisx.template.ConditionBuilder;
import com.lc.mybatisx.utils.TypeUtils;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectItem;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 查询sql模板处理
 *
 * @author ccxuef
 * @date 2025/9/6 14:05
 */
public class SelectSqlTemplateHandler {

    private static final Logger logger = LoggerFactory.getLogger(SelectSqlTemplateHandler.class);

    /**
     * 构建关联查询
     * <code>
     * select * from user_role left join role on user_role.user_id = role.id
     * </code>
     *
     * @param entityRelationSelectInfo
     * @return
     * @throws JSQLParserException
     */
    public String buildSelectSql(ResultMapInfo entityRelationSelectInfo) throws JSQLParserException {
        List<EntityContext> entityContextList = this.getEntityInfoList(entityRelationSelectInfo);
        PlainSelect plainSelect = this.buildMainSelect(entityContextList);
        if (TypeUtils.typeEquals(entityRelationSelectInfo, ResultMapInfo.class)) {
            this.buildFromItem(plainSelect, entityRelationSelectInfo.getEntityTableName(), entityRelationSelectInfo.getEntityTableNameAlias());
            this.buildLeftJoinOn(plainSelect, entityRelationSelectInfo, entityRelationSelectInfo.getComposites());
        }
        if (TypeUtils.typeEquals(entityRelationSelectInfo, SimpleNestedResultMapInfo.class)) {
            RelationColumnInfo relationColumnInfo = (RelationColumnInfo) entityRelationSelectInfo.getColumnInfo();
            RelationType relationType = relationColumnInfo.getRelationType();
            String mainTableName;
            String mainTableNameAlias = "";
            if (relationType == RelationType.MANY_TO_MANY) {
                mainTableName = entityRelationSelectInfo.getMiddleTableName();
            } else {
                mainTableName = entityRelationSelectInfo.getEntityTableName();
                mainTableNameAlias = entityRelationSelectInfo.getEntityTableNameAlias();
            }
            this.buildFromItem(plainSelect, mainTableName, mainTableNameAlias);
            this.buildLeftJoinOn(plainSelect, entityRelationSelectInfo, entityRelationSelectInfo.getComposites());
        }
        if (TypeUtils.typeEquals(entityRelationSelectInfo, BatchNestedResultMapInfo.class)) {
            this.buildFromItem(plainSelect, entityRelationSelectInfo.getEntityTableName(), entityRelationSelectInfo.getEntityTableNameAlias());
            this.buildLeftJoinOn(plainSelect, entityRelationSelectInfo, entityRelationSelectInfo.getComposites());
        }
        return plainSelect.toString();
    }

    /**
     * 构建单表查询，如：select * from user
     *
     * @param entityInfo
     * @return
     */
    public PlainSelect buildSelectSql(EntityInfo entityInfo) {
        PlainSelect plainSelect = this.buildMainSelect(new EntityContext(entityInfo, false));
        this.buildFromItem(plainSelect, entityInfo.getTableName(), entityInfo.getTableNameAlias());
        return plainSelect;
    }

    /**
     * 构建主表查询，如：select * from user_role
     *
     * @param entityContextList
     * @return
     */
    private PlainSelect buildMainSelect(List<EntityContext> entityContextList) {
        PlainSelect plainSelect = new PlainSelect();
        for (EntityContext entityContext : entityContextList) {
            List<SelectItem<?>> selectItemList = this.buildSelectItemList(entityContext);
            plainSelect.addSelectItems(selectItemList);
        }
        return plainSelect;
    }

    private PlainSelect buildMainSelect(EntityContext entityContext) {
        List<SelectItem<?>> mainSelectItemList = this.buildSelectItemList(entityContext);
        PlainSelect plainSelect = new PlainSelect();
        plainSelect.addSelectItems(mainSelectItemList);
        return plainSelect;
    }

    private Table buildFromItem(PlainSelect plainSelect, String mainTableName, String mainTableNameAlias) {
        Table mainTable = new Table(mainTableName);
        if (StringUtils.isNotBlank(mainTableNameAlias)) {
            mainTable.setAlias(new Alias(mainTableNameAlias));
        }
        plainSelect.setFromItem(mainTable);
        return mainTable;
    }

    private List<EntityContext> getEntityInfoList(ResultMapInfo resultMapInfo) {
        List<EntityContext> entityContextList = new ArrayList();
        EntityInfo entityInfo = resultMapInfo.getEntityInfo();
        if (entityInfo != null) {
            Boolean isBatch = TypeUtils.typeEquals(resultMapInfo, BatchNestedResultMapInfo.class);
            entityContextList.add(new EntityContext(entityInfo, isBatch));
        }
        for (ResultMapInfo composite : resultMapInfo.getComposites()) {
            List<EntityContext> childrenEntityContextList = this.getEntityInfoList(composite);
            if (ObjectUtils.isNotEmpty(childrenEntityContextList)) {
                entityContextList.addAll(childrenEntityContextList);
            }
        }
        return entityContextList;
    }

    /**
     * 构建join查询sql【一对一、一对多、多对一、多对多】
     *
     * @param plainSelect
     * @param leftEntityRelationSelectInfo
     * @param rightEntityRelationSelectInfoList
     */
    private void buildLeftJoinOn(PlainSelect plainSelect, ResultMapInfo leftEntityRelationSelectInfo, List<ResultMapInfo> rightEntityRelationSelectInfoList) {
        MiddleEntityInfo middleEntityInfo = leftEntityRelationSelectInfo.getMiddleEntityInfo();
        if (middleEntityInfo != null) {
            Boolean leftManyToMany = leftEntityRelationSelectInfo.isManyToMany();
            if (leftManyToMany) {
                // 左表是多对多的处理【user_role left join role on user_role.user_id = role.id】
                String middleTableName = leftEntityRelationSelectInfo.getMiddleTableName();
                String entityTableName = leftEntityRelationSelectInfo.getEntityTableName();
                String entityTableNameAlias = leftEntityRelationSelectInfo.getEntityTableNameAlias();
                Join join = this.buildLeftJoin(entityTableName, entityTableNameAlias);

                List<ForeignKeyColumnInfo> foreignKeyColumnInfoList;
                Boolean isMappedBy = leftEntityRelationSelectInfo.isMappedBy();
                if (isMappedBy) {
                    foreignKeyColumnInfoList = leftEntityRelationSelectInfo.getForeignKeyColumnInfoList();
                } else {
                    foreignKeyColumnInfoList = leftEntityRelationSelectInfo.getInverseForeignKeyColumnInfoList();
                }
                this.buildMiddleTableOnEntityTable(middleTableName, entityTableNameAlias, foreignKeyColumnInfoList, join);
                plainSelect.addJoins(join);
            }
        }
        for (ResultMapInfo rightEntityRelationSelectInfo : rightEntityRelationSelectInfoList) {
            ResultMapInfo.NestedSelect nestedSelect = rightEntityRelationSelectInfo.getNestedSelect();
            if (nestedSelect != null) {
                continue;
            }
            Boolean rightManyToMany = rightEntityRelationSelectInfo.isManyToMany();
            if (rightManyToMany) {
                // 右表是多对多的处理【role left join role_menu on role.id = role_menu.role_id】
                String entityTableNameAlias = leftEntityRelationSelectInfo.getEntityTableNameAlias();
                String middleTableName = rightEntityRelationSelectInfo.getMiddleTableName();
                Join join = this.buildLeftJoin(middleTableName, null);

                List<ForeignKeyColumnInfo> foreignKeyColumnInfoList;
                Boolean isMappedBy = rightEntityRelationSelectInfo.isMappedBy();
                if (isMappedBy) {
                    foreignKeyColumnInfoList = rightEntityRelationSelectInfo.getInverseForeignKeyColumnInfoList();
                } else {
                    foreignKeyColumnInfoList = rightEntityRelationSelectInfo.getForeignKeyColumnInfoList();
                }
                this.buildEntityTableOnMiddleTable(entityTableNameAlias, middleTableName, foreignKeyColumnInfoList, join);
                plainSelect.addJoins(join);
            } else {
                // 一对一、一对多、多对一的处理【user left join user_detail on user.id = user_detail.user_id】
                String leftEntityTableName = leftEntityRelationSelectInfo.getEntityTableName();
                String rightEntityTableName = rightEntityRelationSelectInfo.getEntityTableName();
                String rightEntityTableNameAlias = rightEntityRelationSelectInfo.getEntityTableNameAlias();
                Join join = this.buildLeftJoin(rightEntityTableName, rightEntityTableNameAlias);

                this.buildEntityTableOnEntityTable(leftEntityRelationSelectInfo, rightEntityRelationSelectInfo, join);
                plainSelect.addJoins(join);
            }
            this.buildLeftJoinOn(plainSelect, rightEntityRelationSelectInfo, rightEntityRelationSelectInfo.getComposites());
        }
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
     * 构建查询字段列
     *
     * @param entityContext
     * @return
     */
    private List<SelectItem<?>> buildSelectItemList(EntityContext entityContext) {
        EntityInfo entityInfo = entityContext.getEntityInfo();
        Boolean isBatch = entityContext.getBatch();

        List<SelectItem<?>> selectItemList = new ArrayList();
        Table table = new Table(entityInfo.getTableNameAlias());
        // 添加非外键表字段
        for (ColumnInfo columnInfo : entityInfo.getTableColumnInfoList()) {
            if (TypeUtils.typeEquals(columnInfo, IdColumnInfo.class)) {
                IdColumnInfo idColumnInfo = (IdColumnInfo) columnInfo;
                List<ColumnInfo> compositeList = idColumnInfo.getComposites();
                if (ObjectUtils.isEmpty(compositeList)) {
                    SelectItem<?> selectItem = this.getSelectItem(table, idColumnInfo.getDbColumnName(), idColumnInfo.getDbColumnNameAlias());
                    selectItemList.add(selectItem);
                } else {
                    for (ColumnInfo composite : compositeList) {
                        SelectItem<?> selectItem = this.getSelectItem(table, composite.getDbColumnName(), composite.getDbColumnNameAlias());
                        selectItemList.add(selectItem);
                    }
                }
            }
            // 批量结果集节点是不需要查询字段的，只需要查询出主键最终能够合并数据即可
            if (!isBatch && TypeUtils.typeEquals(columnInfo, ColumnInfo.class)) {
                SelectItem<?> selectItem = this.getSelectItem(table, columnInfo.getDbColumnName(), columnInfo.getDbColumnNameAlias());
                selectItemList.add(selectItem);
            }
        }
        // 添加外键表字段
        for (RelationColumnInfo relationColumnInfo : entityInfo.getRelationColumnInfoList()) {
            RelationType relationType = relationColumnInfo.getRelationType();
            RelationColumnInfo mappedByRelationColumnInfo = relationColumnInfo.getMappedByRelationColumnInfo();
            if (relationType != RelationType.MANY_TO_MANY && mappedByRelationColumnInfo == null) {
                // 只有一对一、一对多、多对一的时候关联字段才需要作为表字段。多对多存在中间表，关联字段在中间中表，不需要作为实体表字段
                List<ForeignKeyColumnInfo> inverseForeignKeyColumnInfoList = relationColumnInfo.getInverseForeignKeyColumnInfoList();
                for (ForeignKeyInfo inverseForeignKeyInfo : inverseForeignKeyColumnInfoList) {
                    ColumnInfo foreignKeyColumnInfo = inverseForeignKeyInfo.getColumnInfo();
                    SelectItem<?> selectItem = this.getSelectItem(table, foreignKeyColumnInfo.getDbColumnName(), foreignKeyColumnInfo.getDbColumnNameAlias());
                    selectItemList.add(selectItem);
                }
            }
        }
        return selectItemList;
    }

    private SelectItem<Column> getSelectItem(Table table, String columnName, String columnNameAlias) {
        SelectItem<Column> selectItem = new SelectItem();
        Column column = new Column(table, columnName);
        selectItem.withExpression(column);
        Alias alias = new Alias(columnNameAlias);
        selectItem.setAlias(alias);
        return selectItem;
    }

    private void buildEntityTableOnEntityTable(ResultMapInfo leftEntityRelationSelectInfo, ResultMapInfo rightEntityRelationSelectInfo, Join join) {
        List<Expression> onExpressionList = new ArrayList<>();
        String leftEntityTableNameAlias = leftEntityRelationSelectInfo.getEntityTableNameAlias();
        String rightEntityTableNameAlias = rightEntityRelationSelectInfo.getEntityTableNameAlias();
        Boolean isMappedBy = rightEntityRelationSelectInfo.isMappedBy();
        List<ForeignKeyColumnInfo> inverseForeignKeyColumnInfoList = rightEntityRelationSelectInfo.getInverseForeignKeyColumnInfoList();
        if (isMappedBy) {
            for (ForeignKeyInfo inverseForeignKeyInfo : inverseForeignKeyColumnInfoList) {
                ColumnInfo foreignKeyColumnInfo = inverseForeignKeyInfo.getColumnInfo();
                ColumnInfo referencedColumnInfo = inverseForeignKeyInfo.getReferencedColumnInfo();
                String leftExpression = String.format("%s.%s", leftEntityTableNameAlias, referencedColumnInfo.getDbColumnName());
                String rightExpression = String.format("%s.%s", rightEntityTableNameAlias, foreignKeyColumnInfo.getDbColumnName());
                EqualsTo onCondition = ConditionBuilder.eq(leftExpression, rightExpression);
                onExpressionList.add(onCondition);
            }
        } else {
            for (ForeignKeyInfo inverseForeignKeyInfo : inverseForeignKeyColumnInfoList) {
                ColumnInfo foreignKeyColumnInfo = inverseForeignKeyInfo.getColumnInfo();
                ColumnInfo referencedColumnInfo = inverseForeignKeyInfo.getReferencedColumnInfo();
                String leftExpression = String.format("%s.%s", leftEntityTableNameAlias, foreignKeyColumnInfo.getDbColumnName());
                String rightExpression = String.format("%s.%s", rightEntityTableNameAlias, referencedColumnInfo.getDbColumnName());
                EqualsTo onCondition = ConditionBuilder.eq(leftExpression, rightExpression);
                onExpressionList.add(onCondition);
            }
        }
        Expression expression = null;
        for (Expression onExpression : onExpressionList) {
            if (expression == null) {
                expression = onExpression;
            } else {
                expression = ConditionBuilder.and(expression, onExpression);
            }
        }
        join.addOnExpression(expression);
    }

    private void buildEntityTableOnMiddleTable(String entityTableNameAlias, String middleTableName, List<ForeignKeyColumnInfo> foreignKeyColumnInfoList, Join join) {
        List<Expression> onExpressionList = new ArrayList<>();
        for (ForeignKeyInfo foreignKeyInfo : foreignKeyColumnInfoList) {
            ColumnInfo foreignKeyColumnInfo = foreignKeyInfo.getColumnInfo();
            ColumnInfo referencedColumnInfo = foreignKeyInfo.getReferencedColumnInfo();
            String leftExpression = String.format("%s.%s", entityTableNameAlias, referencedColumnInfo.getDbColumnName());
            String rightExpression = String.format("%s.%s", middleTableName, foreignKeyColumnInfo.getDbColumnName());
            EqualsTo onCondition = ConditionBuilder.eq(leftExpression, rightExpression);
            onExpressionList.add(onCondition);
        }
        Expression expression = null;
        for (Expression onExpression : onExpressionList) {
            if (expression == null) {
                expression = onExpression;
            } else {
                expression = ConditionBuilder.and(expression, onExpression);
            }
        }
        join.addOnExpression(expression);
    }

    private void buildMiddleTableOnEntityTable(String middleTableName, String entityTableNameAlias, List<ForeignKeyColumnInfo> foreignKeyColumnInfoList, Join join) {
        List<Expression> onExpressionList = new ArrayList<>();
        for (ForeignKeyInfo foreignKeyInfo : foreignKeyColumnInfoList) {
            ColumnInfo foreignKeyColumnInfo = foreignKeyInfo.getColumnInfo();
            ColumnInfo referencedColumnInfo = foreignKeyInfo.getReferencedColumnInfo();
            String leftExpression = String.format("%s.%s", middleTableName, foreignKeyColumnInfo.getDbColumnName());
            String rightExpression = String.format("%s.%s", entityTableNameAlias, referencedColumnInfo.getDbColumnName());
            EqualsTo onCondition = ConditionBuilder.eq(leftExpression, rightExpression);
            onExpressionList.add(onCondition);
        }
        Expression expression = null;
        for (Expression onExpression : onExpressionList) {
            if (expression == null) {
                expression = onExpression;
            } else {
                expression = ConditionBuilder.and(expression, onExpression);
            }
        }
        join.addOnExpression(expression);
    }

    private static class EntityContext {

        private EntityInfo entityInfo;

        private Boolean isBatch;

        public EntityContext(EntityInfo entityInfo, Boolean isBatch) {
            this.entityInfo = entityInfo;
            this.isBatch = isBatch;
        }

        public EntityInfo getEntityInfo() {
            return entityInfo;
        }

        public Boolean getBatch() {
            return isBatch;
        }
    }
}