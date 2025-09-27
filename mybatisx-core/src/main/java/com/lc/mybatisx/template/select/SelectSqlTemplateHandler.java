package com.lc.mybatisx.template.select;

import com.lc.mybatisx.annotation.ManyToMany;
import com.lc.mybatisx.model.*;
import com.lc.mybatisx.template.ConditionBuilder;
import com.lc.mybatisx.template.MybatisgxSqlBuilder;
import com.lc.mybatisx.utils.TypeUtils;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.ComparisonOperator;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectItem;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 查询sql模板处理
 * @author ccxuef
 * @date 2025/9/6 14:05
 */
public class SelectSqlTemplateHandler {

    private static final Logger logger = LoggerFactory.getLogger(SelectSqlTemplateHandler.class);

    /**
     * 构建关联查询
     * <code>
     *     select * from user_role left join role on user_role.user_id = role.id
     * </code>
     * @param entityRelationSelectInfo
     * @return
     * @throws JSQLParserException
     */
    public String buildSelectSql(EntityRelationSelectInfo entityRelationSelectInfo) throws JSQLParserException {
        // EntityInfo mainEntityInfo = entityRelationSelectInfo.getMainEntityInfo();
        List<EntityInfo> entityInfoList = this.getEntityInfoList(entityRelationSelectInfo);
        PlainSelect plainSelect = this.buildMainSelect(entityInfoList);
        this.buildFromItem(plainSelect, entityRelationSelectInfo.getEntityInfo());
        this.buildLeftJoinOn(plainSelect, entityRelationSelectInfo, entityRelationSelectInfo.getEntityRelationSelectInfoList());
        return plainSelect.toString();
    }

    /**
     * 构建单表查询，如：select * from user
     * @param entityInfo
     * @return
     */
    public PlainSelect buildSelectSql(EntityInfo entityInfo) {
        PlainSelect plainSelect = this.buildMainSelect(entityInfo);
        this.buildFromItem(plainSelect, entityInfo);
        return plainSelect;
    }

    /**
     * 构建主表查询，如：select * from user_role
     * @param entityInfoList
     * @return
     */
    private PlainSelect buildMainSelect(List<EntityInfo> entityInfoList) {
        PlainSelect plainSelect = new PlainSelect();
        for (EntityInfo entityInfo : entityInfoList) {
            List<SelectItem<?>> selectItemList = this.buildSelectItemList(entityInfo);
            plainSelect.addSelectItems(selectItemList);
        }
        return plainSelect;
    }

    private PlainSelect buildMainSelect(EntityInfo mainEntityInfo) {
        List<SelectItem<?>> mainSelectItemList = this.buildSelectItemList(mainEntityInfo);
        PlainSelect plainSelect = new PlainSelect();
        plainSelect.addSelectItems(mainSelectItemList);
        return plainSelect;
    }

    private Table buildFromItem(PlainSelect plainSelect, EntityInfo mainEntityInfo) {
        Table mainTable = new Table(mainEntityInfo.getTableName());
        plainSelect.setFromItem(mainTable);
        return mainTable;
    }

    private List<EntityInfo> getEntityInfoList(EntityRelationSelectInfo entityRelationSelectInfo) {
        List<EntityInfo> entityInfoList = new ArrayList();
        entityInfoList.add(entityRelationSelectInfo.getEntityInfo());
        List<EntityRelationSelectInfo> entityRelationSelectInfoList = entityRelationSelectInfo.getEntityRelationSelectInfoList();
        for (EntityRelationSelectInfo childrenEntityRelationSelectInfo : entityRelationSelectInfoList) {
            List<EntityInfo> childrenEntityInfoList = this.getEntityInfoList(childrenEntityRelationSelectInfo);
            if (ObjectUtils.isNotEmpty(childrenEntityInfoList)) {
                entityInfoList.addAll(childrenEntityInfoList);
            }
        }
        return entityInfoList;
    }

    /**
     * 构建join查询sql【一对一、一对多、多对一、多对多】
     * @param plainSelect
     * @param leftEntityRelationSelectInfo
     * @param rightEntityRelationSelectInfoList
     */
    private void buildLeftJoinOn(PlainSelect plainSelect, EntityRelationSelectInfo leftEntityRelationSelectInfo, List<EntityRelationSelectInfo> rightEntityRelationSelectInfoList) {
        MiddleEntityInfo middleEntityInfo = leftEntityRelationSelectInfo.getMiddleEntityInfo();
        if (middleEntityInfo != null) {
            Boolean leftManyToMany = leftEntityRelationSelectInfo.isManyToMany();
            if (leftManyToMany) {
                // 左表是多对多的处理【user_role left join role on user_role.user_id = role.id】
                String middleTableName = leftEntityRelationSelectInfo.getMiddleTableName();
                String entityTableName = leftEntityRelationSelectInfo.getEntityTableName();
                Join join = this.buildLeftJoin(entityTableName);

                List<ForeignKeyColumnInfo> foreignKeyColumnInfoList;
                Boolean isMappedBy = leftEntityRelationSelectInfo.isMappedBy();
                if (isMappedBy) {
                    foreignKeyColumnInfoList = leftEntityRelationSelectInfo.getForeignKeyColumnInfoList();
                } else {
                    foreignKeyColumnInfoList = leftEntityRelationSelectInfo.getInverseForeignKeyColumnInfoList();
                }
                this.buildMiddleTableOnEntityTable(middleTableName, entityTableName, foreignKeyColumnInfoList, join);
                plainSelect.addJoins(join);
            }
        }
        for (EntityRelationSelectInfo rightEntityRelationSelectInfo : rightEntityRelationSelectInfoList) {
            Boolean rightManyToMany = rightEntityRelationSelectInfo.isManyToMany();
            if (rightManyToMany) {
                // 右表是多对多的处理【role left join role_menu on role.id = role_menu.role_id】
                String entityTableName = leftEntityRelationSelectInfo.getEntityTableName();
                String middleTableName = rightEntityRelationSelectInfo.getMiddleTableName();
                Join join = this.buildLeftJoin(middleTableName);

                List<ForeignKeyColumnInfo> foreignKeyColumnInfoList;
                Boolean isMappedBy = rightEntityRelationSelectInfo.isMappedBy();
                if (isMappedBy) {
                    foreignKeyColumnInfoList = rightEntityRelationSelectInfo.getInverseForeignKeyColumnInfoList();
                } else {
                    foreignKeyColumnInfoList = rightEntityRelationSelectInfo.getForeignKeyColumnInfoList();
                }
                this.buildEntityTableOnMiddleTable(entityTableName, middleTableName, foreignKeyColumnInfoList, join);
                plainSelect.addJoins(join);
            } else {
                // 一对一、一对多、多对一的处理【user left join user_detail on user.id = user_detail.user_id】
                String leftEntityTableName = leftEntityRelationSelectInfo.getEntityTableName();
                String rightEntityTableName = rightEntityRelationSelectInfo.getEntityTableName();
                Join join = this.buildLeftJoin(rightEntityTableName);

                this.buildEntityTableOnEntityTable(leftEntityRelationSelectInfo, rightEntityRelationSelectInfo, join);
                plainSelect.addJoins(join);
            }
            this.buildLeftJoinOn(plainSelect, rightEntityRelationSelectInfo, rightEntityRelationSelectInfo.getEntityRelationSelectInfoList());
        }
    }

    private Join buildLeftJoin(String rightTableName) {
        Join join = new Join();
        join.setLeft(true);
        join.setRightItem(new Table(rightTableName));
        return join;
    }

    /**
     * 构建查询字段列
     * @param entityInfo
     * @return
     */
    private List<SelectItem<?>> buildSelectItemList(EntityInfo entityInfo) {
        List<SelectItem<?>> selectItemList = new ArrayList();
        Table table = new Table(entityInfo.getTableName());
        // 添加非外键表字段
        for (ColumnInfo columnInfo : entityInfo.getTableColumnInfoList()) {
            if (TypeUtils.typeEquals(columnInfo, IdColumnInfo.class)) {
                IdColumnInfo idColumnInfo = (IdColumnInfo) columnInfo;
                List<ColumnInfo> compositeList = idColumnInfo.getColumnInfoList();
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
            if (TypeUtils.typeEquals(columnInfo, ColumnInfo.class)) {
                SelectItem<?> selectItem = this.getSelectItem(table, columnInfo.getDbColumnName(), columnInfo.getDbColumnNameAlias());
                selectItemList.add(selectItem);
            }
        }
        // 添加外键表字段
        for (ColumnInfo columnInfo : entityInfo.getRelationColumnInfoList()) {
            RelationColumnInfo relationColumnInfo = (RelationColumnInfo) columnInfo;
            ManyToMany manyToMany = relationColumnInfo.getManyToMany();
            RelationColumnInfo mappedByRelationColumnInfo = relationColumnInfo.getMappedByRelationColumnInfo();
            if (manyToMany == null && mappedByRelationColumnInfo == null) {
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

    private void buildEntityTableOnEntityTable(EntityRelationSelectInfo leftEntityRelationSelectInfo, EntityRelationSelectInfo rightEntityRelationSelectInfo, Join join) {
        List<Expression> onExpressionList = new ArrayList<>();
        String leftEntityTableName = leftEntityRelationSelectInfo.getEntityTableName();
        String rightEntityTableName = rightEntityRelationSelectInfo.getEntityTableName();
        Boolean isMappedBy = rightEntityRelationSelectInfo.isMappedBy();
        List<ForeignKeyColumnInfo> inverseForeignKeyColumnInfoList = rightEntityRelationSelectInfo.getInverseForeignKeyColumnInfoList();
        if (isMappedBy) {
            for (ForeignKeyInfo inverseForeignKeyInfo : inverseForeignKeyColumnInfoList) {
                ColumnInfo foreignKeyColumnInfo = inverseForeignKeyInfo.getColumnInfo();
                ColumnInfo referencedColumnInfo = inverseForeignKeyInfo.getReferencedColumnInfo();
                String leftExpression = String.format("%s.%s", leftEntityTableName, referencedColumnInfo.getDbColumnName());
                String rightExpression = String.format("%s.%s", rightEntityTableName, foreignKeyColumnInfo.getDbColumnName());
                EqualsTo onCondition = ConditionBuilder.eq(leftExpression, rightExpression);
                onExpressionList.add(onCondition);
            }
        } else {
            for (ForeignKeyInfo inverseForeignKeyInfo : inverseForeignKeyColumnInfoList) {
                ColumnInfo foreignKeyColumnInfo = inverseForeignKeyInfo.getColumnInfo();
                ColumnInfo referencedColumnInfo = inverseForeignKeyInfo.getReferencedColumnInfo();
                String leftExpression = String.format("%s.%s", leftEntityTableName, foreignKeyColumnInfo.getDbColumnName());
                String rightExpression = String.format("%s.%s", rightEntityTableName, referencedColumnInfo.getDbColumnName());
                EqualsTo onCondition = ConditionBuilder.eq(leftExpression, rightExpression);
                onExpressionList.add(onCondition);
            }
        }
        join.setOnExpressions(onExpressionList);
    }

    private void buildEntityTableOnMiddleTable(String entityTableName, String middleTableName, List<ForeignKeyColumnInfo> foreignKeyColumnInfoList, Join join) {
        List<Expression> onExpressionList = new ArrayList<>();
        for (ForeignKeyInfo foreignKeyInfo : foreignKeyColumnInfoList) {
            ColumnInfo foreignKeyColumnInfo = foreignKeyInfo.getColumnInfo();
            ColumnInfo referencedColumnInfo = foreignKeyInfo.getReferencedColumnInfo();
            String leftExpression = String.format("%s.%s", entityTableName, referencedColumnInfo.getDbColumnName());
            String rightExpression = String.format("%s.%s", middleTableName, foreignKeyColumnInfo.getDbColumnName());
            EqualsTo onCondition = ConditionBuilder.eq(leftExpression, rightExpression);
            onExpressionList.add(onCondition);
        }
        join.setOnExpressions(onExpressionList);
    }

    private void buildMiddleTableOnEntityTable(String middleTableName, String entityTableName, List<ForeignKeyColumnInfo> foreignKeyColumnInfoList, Join join) {
        List<Expression> onExpressionList = new ArrayList<>();
        for (ForeignKeyInfo foreignKeyInfo : foreignKeyColumnInfoList) {
            ColumnInfo foreignKeyColumnInfo = foreignKeyInfo.getColumnInfo();
            ColumnInfo referencedColumnInfo = foreignKeyInfo.getReferencedColumnInfo();
            String leftExpression = String.format("%s.%s", middleTableName, foreignKeyColumnInfo.getDbColumnName());
            String rightExpression = String.format("%s.%s", entityTableName, referencedColumnInfo.getDbColumnName());
            EqualsTo onCondition = ConditionBuilder.eq(leftExpression, rightExpression);
            onExpressionList.add(onCondition);
        }
        join.setOnExpressions(onExpressionList);
    }

    private void buildLeftTableOnRightTable(String leftTableName, String rightTableName, List<ForeignKeyColumnInfo> foreignKeyColumnInfoList, Join join) {
        List<Expression> onExpressionList = new ArrayList<>();
        for (ForeignKeyInfo foreignKeyInfo : foreignKeyColumnInfoList) {
            ColumnInfo foreignKeyColumnInfo = foreignKeyInfo.getColumnInfo();
            ColumnInfo referencedColumnInfo = foreignKeyInfo.getReferencedColumnInfo();
            Column leftColumn = MybatisgxSqlBuilder.ColumnBuilder.builder().with(leftTableName, foreignKeyColumnInfo.getDbColumnName()).build();
            Column rightColumn = MybatisgxSqlBuilder.ColumnBuilder.builder().with(rightTableName, referencedColumnInfo.getDbColumnName()).build();
            ComparisonOperator comparisonOperator = MybatisgxSqlBuilder.buildComparisonOperator(leftColumn, rightColumn);
            onExpressionList.add(comparisonOperator);
        }
        join.setOnExpressions(onExpressionList);
    }
}