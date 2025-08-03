package com.lc.mybatisx.template;

import com.lc.mybatisx.model.ColumnInfoAnnotationInfo;
import com.lc.mybatisx.model.EntityInfo;
import com.lc.mybatisx.model.EntityRelationSelectInfo;
import com.lc.mybatisx.model.ForeignKeyColumnInfo;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class SelectSqlTemplateHandler {

    private static final Logger logger = LoggerFactory.getLogger(SelectSqlTemplateHandler.class);

    /**
     * @param entityRelationSelectInfo
     * @return
     * @throws JSQLParserException
     */
    public String buildSelectSql(EntityRelationSelectInfo entityRelationSelectInfo) throws JSQLParserException {
        PlainSelect plainSelect = new PlainSelect();
        EntityInfo entityInfo = entityRelationSelectInfo.getEntityInfo();
        this.buildMainSelect(plainSelect, entityRelationSelectInfo, entityInfo);
        this.buildSelectSql(plainSelect, entityRelationSelectInfo, entityRelationSelectInfo.getEntityRelationSelectInfoList());
        logger.info("build select sql: \n{}", plainSelect);
        return plainSelect.toString();
    }

    private void buildMainSelect(PlainSelect plainSelect, EntityRelationSelectInfo entityRelationSelectInfo, EntityInfo entityInfo) {
        Boolean isMiddleTable = entityRelationSelectInfo.getExistMiddleTable();
        String mainTableName = isMiddleTable ? entityRelationSelectInfo.getMiddleTableName() : entityRelationSelectInfo.getEntityTableName();
        plainSelect.addSelectItems(this.getSelectItemList(entityInfo));
        Table mainTable = new Table(mainTableName);
        plainSelect.setFromItem(mainTable);
    }

    private void buildSelectSql(PlainSelect plainSelect, EntityRelationSelectInfo leftEntityRelationSelectInfo, List<EntityRelationSelectInfo> rightEntityRelationSelectInfoList) throws JSQLParserException {
        Boolean isMiddleTable = leftEntityRelationSelectInfo.getExistMiddleTable();
        if (isMiddleTable) {
            // user_role
            // left join role on user_role.user_id = role.id
            String middleTableName = leftEntityRelationSelectInfo.getMiddleTableName();
            String entityTableName = leftEntityRelationSelectInfo.getEntityTableName();
            Join join = this.buildLeftJoin(entityTableName);

            List<ForeignKeyColumnInfo> foreignKeyColumnInfoList = leftEntityRelationSelectInfo.getForeignKeyColumnInfoList(entityTableName);
            this.buildMiddleTableOnEntityTable(middleTableName, entityTableName, foreignKeyColumnInfoList, join);
            plainSelect.addJoins(join);
        }
        for (EntityRelationSelectInfo rightEntityRelationSelectInfo : rightEntityRelationSelectInfoList) {
            Boolean isChildrenMiddleTable = rightEntityRelationSelectInfo.getExistMiddleTable();
            if (isChildrenMiddleTable) {
                // left join role_menu on role.id = role_menu.role_id
                String entityTableName = leftEntityRelationSelectInfo.getEntityTableName();
                String middleTableName = rightEntityRelationSelectInfo.getMiddleTableName();
                Join join = this.buildLeftJoin(middleTableName);

                List<ForeignKeyColumnInfo> foreignKeyColumnInfoList = leftEntityRelationSelectInfo.getForeignKeyColumnInfoList(entityTableName);
                this.buildEntityTableOnMiddleTable(entityTableName, middleTableName, foreignKeyColumnInfoList, join);
                plainSelect.addJoins(join);
            } else {
                // user_detail
                // left join user on user.id = user_detail.user_id
                // left join order on user.id = order.user_id
                String leftEntityTableName = leftEntityRelationSelectInfo.getEntityTableName();
                String rightEntityTableName = rightEntityRelationSelectInfo.getEntityTableName();
                Join join = this.buildLeftJoin(rightEntityTableName);

                List<ForeignKeyColumnInfo> foreignKeyColumnInfoList = rightEntityRelationSelectInfo.getForeignKeyColumnInfoList(rightEntityTableName);
                this.buildEntityTableOnEntityTable(leftEntityTableName, rightEntityTableName, foreignKeyColumnInfoList, join);
                plainSelect.addJoins(join);
            }
            this.buildSelectSql(plainSelect, rightEntityRelationSelectInfo, rightEntityRelationSelectInfo.getEntityRelationSelectInfoList());
        }
    }

    private Join buildLeftJoin(String rightTableName) {
        Join join = new Join();
        join.setLeft(true);
        join.setRightItem(new Table(rightTableName));
        return join;
    }

    private void buildEntityTableOnEntityTable(String leftTableName, String rightTableName, List<ForeignKeyColumnInfo> foreignKeyColumnInfoList, Join join) throws JSQLParserException {
        List<Expression> onExpressionList = new ArrayList<>();
        for (int i = 0; i < foreignKeyColumnInfoList.size(); i++) {
            ForeignKeyColumnInfo foreignKeyColumnInfo = foreignKeyColumnInfoList.get(i);
            EqualsTo onCondition = new EqualsTo(
                    CCJSqlParserUtil.parseExpression(leftTableName + "." + foreignKeyColumnInfo.getReferencedColumnName()),
                    CCJSqlParserUtil.parseExpression(rightTableName + "." + foreignKeyColumnInfo.getName())
            );
            onExpressionList.add(onCondition);
        }
        join.setOnExpressions(onExpressionList);
    }

    /**
     * user
     * left join user_role on user.id = user_role.user_id
     * left join role on user_role.role_id = role.id
     * left join role_menu on role.id = role_menu.role_id
     * @param entityTableName
     * @return
     * @throws JSQLParserException
     */
    private void buildEntityTableOnMiddleTable(String entityTableName, String middleTableName, List<ForeignKeyColumnInfo> foreignKeyColumnInfoList, Join join) throws JSQLParserException {
        List<Expression> onExpressionList = new ArrayList<>();
        for (int i = 0; i < foreignKeyColumnInfoList.size(); i++) {
            ForeignKeyColumnInfo foreignKeyColumnInfo = foreignKeyColumnInfoList.get(i);
            EqualsTo onCondition = new EqualsTo(
                    CCJSqlParserUtil.parseExpression(entityTableName + "." + foreignKeyColumnInfo.getReferencedColumnName()),
                    CCJSqlParserUtil.parseExpression(middleTableName + "." + foreignKeyColumnInfo.getName())
            );
            onExpressionList.add(onCondition);
        }
        join.setOnExpressions(onExpressionList);
    }

    private void buildMiddleTableOnEntityTable(String middleTableName, String entityTableName, List<ForeignKeyColumnInfo> foreignKeyColumnInfoList, Join join) throws JSQLParserException {
        List<Expression> onExpressionList = new ArrayList<>();
        for (int i = 0; i < foreignKeyColumnInfoList.size(); i++) {
            ForeignKeyColumnInfo foreignKeyColumnInfo = foreignKeyColumnInfoList.get(i);
            EqualsTo onCondition = new EqualsTo(
                    CCJSqlParserUtil.parseExpression(middleTableName + "." + foreignKeyColumnInfo.getName()),
                    CCJSqlParserUtil.parseExpression(entityTableName + "." + foreignKeyColumnInfo.getReferencedColumnName())
            );
            onExpressionList.add(onCondition);
        }
        join.setOnExpressions(onExpressionList);
    }

    private List<SelectItem<?>> getSelectItemList(EntityInfo queryEntityInfo) {
        List<SelectItem<?>> list = new ArrayList<>();
        queryEntityInfo.getTableColumnInfoList().forEach(queryColumnInfo -> {
            // 外键不存在，只需要添加字段。外键存在，则需要添加字段和外键
            ColumnInfoAnnotationInfo queryAssociationEntityInfo = queryColumnInfo.getColumnInfoAnnotationInfo();
            if (queryAssociationEntityInfo == null) {
                list.add(this.getSelectItem(queryColumnInfo.getDbColumnName()));
            } else {
                queryAssociationEntityInfo.getForeignKeyColumnInfoList().forEach(foreignKeyColumnInfo -> {
                    list.add(this.getSelectItem(foreignKeyColumnInfo.getName()));
                });
            }
        });
        return list;
    }

    private SelectItem<Column> getSelectItem(String columnName) {
        SelectItem<Column> selectItem = new SelectItem();
        Column column = new Column(columnName);
        selectItem.withExpression(column);
        return selectItem;
    }
}