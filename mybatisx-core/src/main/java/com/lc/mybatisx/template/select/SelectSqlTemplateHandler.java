package com.lc.mybatisx.template.select;

import com.lc.mybatisx.annotation.ManyToMany;
import com.lc.mybatisx.model.*;
import com.lc.mybatisx.template.ConditionBuilder;
import com.lc.mybatisx.template.MybatisgxSqlBuilder;
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
import java.util.Arrays;
import java.util.List;

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
        Boolean isManyToMany = entityRelationSelectInfo.getManyToMany();
        String mainTableName = isManyToMany ? entityRelationSelectInfo.getMiddleTableName() : entityRelationSelectInfo.getEntityTableName();
        List<EntityInfo> entityInfoList = this.getEntityInfoList(entityRelationSelectInfo);
        PlainSelect plainSelect = this.buildMainSelect(mainTableName, entityInfoList);
        this.buildLeftJoinOn(plainSelect, entityRelationSelectInfo, entityRelationSelectInfo.getEntityRelationSelectInfoList());
        return plainSelect.toString();
    }

    /**
     * 构建单表查询，如：select * from user
     * @param entityInfo
     * @return
     */
    public PlainSelect buildSelectSql(EntityInfo entityInfo) {
        PlainSelect plainSelect = this.buildMainSelect(entityInfo.getTableName(), Arrays.asList(entityInfo));
        return plainSelect;
    }

    /**
     * 构建主表查询，如：select * from user_role
     * @param mainTableName
     * @param entityInfoList
     * @return
     */
    private PlainSelect buildMainSelect(String mainTableName, List<EntityInfo> entityInfoList) {
        PlainSelect plainSelect = new PlainSelect();
        for (EntityInfo entityInfo : entityInfoList) {
            List<SelectItem<?>> selectItemList = this.buildSelectItemList(entityInfo);
            plainSelect.addSelectItems(selectItemList);
        }
        Table mainTable = new Table(mainTableName);
        plainSelect.setFromItem(mainTable);
        return plainSelect;
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
        Boolean leftManyToMany = leftEntityRelationSelectInfo.getManyToMany();
        if (leftManyToMany) {
            // 左表是多对多的处理【user_role left join role on user_role.user_id = role.id】
            String middleTableName = leftEntityRelationSelectInfo.getMiddleTableName();
            String entityTableName = leftEntityRelationSelectInfo.getEntityTableName();
            Join join = this.buildLeftJoin(entityTableName);

            List<ForeignKeyColumnInfo> foreignKeyColumnInfoList;
            Boolean isMappedBy = leftEntityRelationSelectInfo.getMappedBy();
            if (isMappedBy) {
                foreignKeyColumnInfoList = leftEntityRelationSelectInfo.getForeignKeyColumnInfoList();
            } else {
                foreignKeyColumnInfoList = leftEntityRelationSelectInfo.getInverseForeignKeyColumnInfoList();
            }
            this.buildMiddleTableOnEntityTable(middleTableName, entityTableName, foreignKeyColumnInfoList, join);
            plainSelect.addJoins(join);
        }
        for (EntityRelationSelectInfo rightEntityRelationSelectInfo : rightEntityRelationSelectInfoList) {
            Boolean rightManyToMany = rightEntityRelationSelectInfo.getManyToMany();
            if (rightManyToMany) {
                // 右表是多对多的处理【role left join role_menu on role.id = role_menu.role_id】
                String entityTableName = leftEntityRelationSelectInfo.getEntityTableName();
                String middleTableName = rightEntityRelationSelectInfo.getMiddleTableName();
                Join join = this.buildLeftJoin(middleTableName);

                List<ForeignKeyColumnInfo> foreignKeyColumnInfoList;
                Boolean isMappedBy = rightEntityRelationSelectInfo.getMappedBy();
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
        for (ColumnInfo columnInfo : entityInfo.getTableColumnInfoList()) {
            // 外键不存在，只需要添加字段。外键存在，则需要添加字段和外键
            ColumnRelationInfo columnRelationInfo = columnInfo.getColumnRelationInfo();
            if (columnRelationInfo == null) {
                selectItemList.add(this.getSelectItem(table, columnInfo.getDbColumnName(), columnInfo.getDbColumnNameAlias()));
            } else {
                ManyToMany manyToMany = columnRelationInfo.getManyToMany();
                if (manyToMany == null) {
                    // 只有一对一、一对多、多对一的时候关联字段才需要作为表字段。多对多存在中间表，关联字段在中间中表，不需要作为实体表字段
                    List<ForeignKeyColumnInfo> inverseForeignKeyColumnInfoList = columnRelationInfo.getInverseForeignKeyColumnInfoList();
                    for (ForeignKeyColumnInfo inverseForeignKeyColumnInfo : inverseForeignKeyColumnInfoList) {
                        selectItemList.add(this.getSelectItem(table, inverseForeignKeyColumnInfo.getName(), inverseForeignKeyColumnInfo.getNameAlias()));
                    }
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
        Boolean isMappedBy = rightEntityRelationSelectInfo.getMappedBy();
        List<ForeignKeyColumnInfo> inverseForeignKeyColumnInfoList = rightEntityRelationSelectInfo.getInverseForeignKeyColumnInfoList();
        if (isMappedBy) {
            for (ForeignKeyColumnInfo inverseForeignKeyColumnInfo : inverseForeignKeyColumnInfoList) {
                String leftExpression = String.format("%s.%s", leftEntityTableName, inverseForeignKeyColumnInfo.getReferencedColumnName());
                String rightExpression = String.format("%s.%s", rightEntityTableName, inverseForeignKeyColumnInfo.getName());
                EqualsTo onCondition = ConditionBuilder.eq(leftExpression, rightExpression);
                onExpressionList.add(onCondition);
            }
        } else {
            for (ForeignKeyColumnInfo inverseForeignKeyColumnInfo : inverseForeignKeyColumnInfoList) {
                String leftExpression = String.format("%s.%s", leftEntityTableName, inverseForeignKeyColumnInfo.getName());
                String rightExpression = String.format("%s.%s", rightEntityTableName, inverseForeignKeyColumnInfo.getReferencedColumnName());
                EqualsTo onCondition = ConditionBuilder.eq(leftExpression, rightExpression);
                onExpressionList.add(onCondition);
            }
        }
        join.setOnExpressions(onExpressionList);
    }

    private void buildEntityTableOnMiddleTable(String entityTableName, String middleTableName, List<ForeignKeyColumnInfo> foreignKeyColumnInfoList, Join join) {
        List<Expression> onExpressionList = new ArrayList<>();
        for (int i = 0; i < foreignKeyColumnInfoList.size(); i++) {
            ForeignKeyColumnInfo foreignKeyColumnInfo = foreignKeyColumnInfoList.get(i);
            String leftExpression = String.format("%s.%s", entityTableName, foreignKeyColumnInfo.getReferencedColumnName());
            String rightExpression = String.format("%s.%s", middleTableName, foreignKeyColumnInfo.getName());
            EqualsTo onCondition = ConditionBuilder.eq(leftExpression, rightExpression);
            onExpressionList.add(onCondition);
        }
        join.setOnExpressions(onExpressionList);
    }

    private void buildMiddleTableOnEntityTable(String middleTableName, String entityTableName, List<ForeignKeyColumnInfo> foreignKeyColumnInfoList, Join join) {
        List<Expression> onExpressionList = new ArrayList<>();
        for (int i = 0; i < foreignKeyColumnInfoList.size(); i++) {
            ForeignKeyColumnInfo foreignKeyColumnInfo = foreignKeyColumnInfoList.get(i);
            String leftExpression = String.format("%s.%s", middleTableName, foreignKeyColumnInfo.getName());
            String rightExpression = String.format("%s.%s", entityTableName, foreignKeyColumnInfo.getReferencedColumnName());
            EqualsTo onCondition = ConditionBuilder.eq(leftExpression, rightExpression);
            onExpressionList.add(onCondition);
        }
        join.setOnExpressions(onExpressionList);
    }

    private void buildLeftTableOnRightTable(String leftTableName, String rightTableName, List<ForeignKeyColumnInfo> foreignKeyColumnInfoList, Join join) {
        List<Expression> onExpressionList = new ArrayList<>();
        for (int i = 0; i < foreignKeyColumnInfoList.size(); i++) {
            ForeignKeyColumnInfo foreignKeyColumnInfo = foreignKeyColumnInfoList.get(i);
            Column leftColumn = MybatisgxSqlBuilder.ColumnBuilder.builder().with(leftTableName, foreignKeyColumnInfo.getName()).build();
            Column rightColumn = MybatisgxSqlBuilder.ColumnBuilder.builder().with(rightTableName, foreignKeyColumnInfo.getReferencedColumnName()).build();
            ComparisonOperator comparisonOperator = MybatisgxSqlBuilder.buildComparisonOperator(leftColumn, rightColumn);
            onExpressionList.add(comparisonOperator);
        }
        join.setOnExpressions(onExpressionList);
    }
}