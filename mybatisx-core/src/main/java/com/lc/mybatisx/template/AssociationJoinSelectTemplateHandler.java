package com.lc.mybatisx.template;

import com.lc.mybatisx.annotation.LoadStrategy;
import com.lc.mybatisx.context.EntityInfoContextHolder;
import com.lc.mybatisx.model.*;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.AllTableColumns;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectItem;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class AssociationJoinSelectTemplateHandler {

    private static final Logger logger = LoggerFactory.getLogger(AssociationJoinSelectTemplateHandler.class);

    public void buildSql(ResultMapInfo resultMapInfo) throws JSQLParserException {
        List<ResultMapAssociationInfo> resultMapAssociationInfoList = resultMapInfo.getResultMapAssociationInfoList();
        List<ResultMapAssociationInfo> joinInfoList = new ArrayList<>();
        resultMapAssociationInfoList.forEach(resultMapAssociationInfo -> {
            AssociationEntityInfo associationEntityInfo = resultMapAssociationInfo.getColumnInfo().getAssociationEntityInfo();
            LoadStrategy loadStrategy = associationEntityInfo.getLoadStrategy();
            if (loadStrategy == LoadStrategy.JOIN) {
                joinInfoList.add(resultMapAssociationInfo);
            }
        });

        if (ObjectUtils.isEmpty(joinInfoList)) {

        } else {
            // 1. 创建主表 (user)
            Class<?> clazz = resultMapInfo.getType();
            EntityInfo entityInfo = EntityInfoContextHolder.get(clazz);
            Table mainTable = new Table(entityInfo.getTableName());

            // 5. 构建 SELECT 主体
            PlainSelect select = new PlainSelect();
            // SELECT *
            List<SelectItem<?>> list = new ArrayList<>();
            SelectItem<AllTableColumns> selectItem = new SelectItem();
            list.add(selectItem);
            select.setSelectItems(list);
            select.setFromItem(mainTable); // FROM user

            // 添加多个 JOIN
            List<Join> joinList = this.buildLeftJoin(mainTable.getName(), joinInfoList);
            select.addJoins(joinList);

            // 7. 生成 SQL 字符串
            String selectSQL = select.toString();
            String generatedSql = selectSQL.toString();

            System.out.println("Generated SQL:\n" + generatedSql);
        }
    }

    private List<Join> buildLeftJoin(String mainTableName, List<ResultMapAssociationInfo> joinInfoList) throws JSQLParserException {
        List<Join> joinList = new ArrayList<>();
        for (int i = 0; i < joinInfoList.size(); i++) {
            ResultMapAssociationInfo resultMapAssociationInfo = joinInfoList.get(i);
            ColumnInfo columnInfo = resultMapAssociationInfo.getColumnInfo();
            AssociationEntityInfo associationEntityInfo = columnInfo.getAssociationEntityInfo();
            String mappedBy = associationEntityInfo.getMappedBy();

            EntityInfo entityInfo = EntityInfoContextHolder.get(columnInfo.getJavaType());
            ColumnInfo mappedByColumnInfo = entityInfo.getColumnInfo(mappedBy);

            List<ForeignKeyColumnInfo> foreignKeyColumnInfoList = mappedByColumnInfo.getAssociationEntityInfo().getForeignKeyColumnInfoList();
            String joinTableName = entityInfo.getTableName();
            Table joinTable = new Table(joinTableName);
            List<Expression> onExpressionList = this.buildLeftJoinOn(mainTableName, joinTableName, foreignKeyColumnInfoList);

            Join join = new Join();
            join.setLeft(true);
            join.setRightItem(joinTable);
            join.setOnExpressions(onExpressionList);
            joinList.add(join);
        }
        return joinList;
    }

    private List<Expression> buildLeftJoinOn(String mainTableName, String joinTableName, List<ForeignKeyColumnInfo> foreignKeyColumnInfoList) throws JSQLParserException {
        List<Expression> onExpressionList = new ArrayList<>();
        for (int i = 0; i < foreignKeyColumnInfoList.size(); i++) {
            ForeignKeyColumnInfo foreignKeyColumnInfo = foreignKeyColumnInfoList.get(i);
            EqualsTo onCondition = new EqualsTo(
                    CCJSqlParserUtil.parseExpression(mainTableName + "." + foreignKeyColumnInfo.getReferencedColumnName()),
                    CCJSqlParserUtil.parseExpression(joinTableName + "." + foreignKeyColumnInfo.getName())
            );
            onExpressionList.add(onCondition);
        }
        return onExpressionList;
    }
}