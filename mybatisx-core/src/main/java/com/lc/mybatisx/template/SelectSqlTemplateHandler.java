package com.lc.mybatisx.template;

import com.lc.mybatisx.model.*;
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
        MiddleTableInfo middleTableInfo = entityRelationSelectInfo.getMiddleTableInfo();
        String mainTableName;
        if (middleTableInfo != null) {
            mainTableName = middleTableInfo.getTableName();
        } else {
            mainTableName = entityRelationSelectInfo.getTableName();
        }
        plainSelect.addSelectItems(this.getSelectItemList(entityInfo));
        Table mainTable = new Table(mainTableName);
        plainSelect.setFromItem(mainTable);
    }

    private void buildSelectSql(PlainSelect plainSelect, EntityRelationSelectInfo prevEntityRelationSelectInfo, List<EntityRelationSelectInfo> childrenEntityRelationSelectInfoList) throws JSQLParserException {
        MiddleTableInfo middleTableInfo = prevEntityRelationSelectInfo.getMiddleTableInfo();
        if (middleTableInfo != null) {
            // user_role
            // left join role on user_role.user_id = role.id
            // left join role_menu on role.id = role_menu.role_id
            String middleTableName = middleTableInfo.getTableName();
            String entityTableName = prevEntityRelationSelectInfo.getTableName();
            List<ForeignKeyColumnInfo> foreignKeyColumnInfoList = prevEntityRelationSelectInfo.getForeignKeyColumnInfoList();

            Join join = this.buildLeftJoin(entityTableName);
            this.buildLeftJoinOn(middleTableName, entityTableName, foreignKeyColumnInfoList, join);
            plainSelect.addJoins(join);
        }
        for (EntityRelationSelectInfo childrenEntityRelationSelectInfo : childrenEntityRelationSelectInfoList) {
            MiddleTableInfo childrenMiddleTableInfo = childrenEntityRelationSelectInfo.getMiddleTableInfo();
            if (childrenMiddleTableInfo != null) {
                String entityTableName = prevEntityRelationSelectInfo.getTableName();
                String middleTableName = childrenMiddleTableInfo.getTableName();
                List<ForeignKeyColumnInfo> foreignKeyColumnInfoList = childrenMiddleTableInfo.getForeignKeyColumnInfoList();

                Join join = this.buildLeftJoin(middleTableName);
                this.buildLeftJoinOn(entityTableName, middleTableName, foreignKeyColumnInfoList, join);
                plainSelect.addJoins(join);
            } else {
                // user_detail
                // left join user on user.id = user_detail.user_id
                // left join order on user.id = order.user_id
                String prevEntityTableName = prevEntityRelationSelectInfo.getTableName();
                String entityTableName = childrenEntityRelationSelectInfo.getTableName();
                List<ForeignKeyColumnInfo> foreignKeyColumnInfoList = childrenEntityRelationSelectInfo.getForeignKeyColumnInfoList();

                Join join = this.buildLeftJoin(entityTableName);
                this.buildLeftJoinOn(prevEntityTableName, entityTableName, foreignKeyColumnInfoList, join);
                plainSelect.addJoins(join);
            }
            this.buildSelectSql(plainSelect, childrenEntityRelationSelectInfo, childrenEntityRelationSelectInfo.getEntityRelationSelectInfoList());
        }
    }

    private Join buildLeftJoin(String rightTableName) {
        Join join = new Join();
        join.setLeft(true);
        join.setRightItem(new Table(rightTableName));
        return join;
    }

    /**
     * user
     * left join user_role on user.id = user_role.user_id
     * left join role on user_role.role_id = role.id
     * left join role_menu on role.id = role_menu.role_id
     * @param rightTableName
     * @return
     * @throws JSQLParserException
     */
    private void buildLeftJoinOn(String leftTableName, String rightTableName, List<ForeignKeyColumnInfo> foreignKeyColumnInfoList, Join join) throws JSQLParserException {
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