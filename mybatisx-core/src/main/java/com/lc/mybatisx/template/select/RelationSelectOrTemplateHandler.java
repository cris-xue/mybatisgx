package com.lc.mybatisx.template.select;

import com.lc.mybatisx.model.*;
import com.lc.mybatisx.template.ConditionBuilder;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class RelationSelectOrTemplateHandler {

    private static final Logger logger = LoggerFactory.getLogger(RelationSelectOrTemplateHandler.class);

    public Expression buildSelectSqlXNode(EntityRelationSelectInfo entityRelationSelectInfo) {
        EntityInfo relationEntityInfo = entityRelationSelectInfo.getEntityInfo();
        ColumnRelationInfo columnRelationInfo = entityRelationSelectInfo.getColumnInfo().getColumnRelationInfo();
        String mappedBy = columnRelationInfo.getMappedBy();
        if (StringUtils.isNotBlank(mappedBy)) {
            ColumnInfo mappedByColumnInfo = relationEntityInfo.getColumnInfo(mappedBy);
            ColumnRelationInfo mappedByColumnRelationInfo = mappedByColumnInfo.getColumnRelationInfo();
            List<ForeignKeyColumnInfo> inverseForeignKeyColumnInfoList = mappedByColumnRelationInfo.getInverseForeignKeyColumnInfoList();
            Expression whereCondition = null;
            // FROM user_detail WHERE user_detail.user_id = ? or user_detail.user_id = ?
            for (ForeignKeyColumnInfo inverseForeignKeyColumnInfo : inverseForeignKeyColumnInfoList) {
                String leftEq = String.format("%s.%s", relationEntityInfo.getTableName(), inverseForeignKeyColumnInfo.getName());
                String rightEq = inverseForeignKeyColumnInfo.getName();
                EqualsTo eqCondition = ConditionBuilder.eq(leftEq, String.format("#{%s.%s}", "item", rightEq));
                // 将表达式添加到条件树
                if (whereCondition == null) {
                    whereCondition = eqCondition;
                } else {
                    whereCondition = new AndExpression(whereCondition, eqCondition);
                }
            }
            return whereCondition;
            // RelationSelectHelper.buildForeachElement(whereElement, whereCondition);
        } else {
            List<ForeignKeyColumnInfo> inverseForeignKeyColumnInfoList = columnRelationInfo.getInverseForeignKeyColumnInfoList();
            Expression whereCondition = null;
            for (ForeignKeyColumnInfo inverseForeignKeyColumnInfo : inverseForeignKeyColumnInfoList) {
                String leftEq = String.format("%s.%s", relationEntityInfo.getTableName(), inverseForeignKeyColumnInfo.getReferencedColumnName());
                String rightEq = inverseForeignKeyColumnInfo.getName();
                EqualsTo eqCondition = ConditionBuilder.eq(leftEq, String.format("#{%s.%s}", "item", rightEq));
                // 将表达式添加到条件树
                if (whereCondition == null) {
                    whereCondition = eqCondition;
                } else {
                    whereCondition = new AndExpression(whereCondition, eqCondition);
                }
            }
            return whereCondition;
            // RelationSelectHelper.buildForeachElement(whereElement, whereCondition);
        }
    }

    private void buildManyToManySelectSqlXNode(Element selectElement, EntityRelationSelectInfo entityRelationSelectInfo, String sql) {
        selectElement.addText(sql);
        Element whereElement = selectElement.addElement("where");

        EntityInfo relationEntityInfo = entityRelationSelectInfo.getEntityInfo();
        String middleTableName = entityRelationSelectInfo.getMiddleTableName();
        ColumnRelationInfo columnRelationInfo = entityRelationSelectInfo.getColumnInfo().getColumnRelationInfo();
        String mappedBy = columnRelationInfo.getMappedBy();
        if (StringUtils.isNotBlank(mappedBy)) {
            // user_role left join role on() user_role.role_id = role.id where user_role.user_id = user.id
            ColumnInfo mappedByColumnInfo = relationEntityInfo.getColumnInfo(mappedBy);
            ColumnRelationInfo mappedByColumnRelationInfo = mappedByColumnInfo.getColumnRelationInfo();
            List<ForeignKeyColumnInfo> inverseForeignKeyColumnInfoList = mappedByColumnRelationInfo.getInverseForeignKeyColumnInfoList();
            Expression whereCondition = null;
            for (ForeignKeyColumnInfo inverseForeignKeyColumnInfo : inverseForeignKeyColumnInfoList) {
                String leftEq = String.format("%s.%s", middleTableName, inverseForeignKeyColumnInfo.getName());
                String rightEq = inverseForeignKeyColumnInfo.getName();
                EqualsTo eqCondition = ConditionBuilder.eq(leftEq, String.format("#{%s.%s}", "item", rightEq));
                // 将表达式添加到条件树
                if (whereCondition == null) {
                    whereCondition = eqCondition;
                } else {
                    whereCondition = new AndExpression(whereCondition, eqCondition);
                }
            }
            RelationSelectHelper.buildForeachElement(whereElement, whereCondition);
        } else {
            // user_role left join user on() user_role.user_id = user.id where user_role.role_id = role.id
            List<ForeignKeyColumnInfo> foreignKeyColumnInfoList = columnRelationInfo.getForeignKeyColumnInfoList();
            Expression whereCondition = null;
            for (ForeignKeyColumnInfo foreignKeyColumnInfo : foreignKeyColumnInfoList) {
                String leftEq = String.format("%s.%s", middleTableName, foreignKeyColumnInfo.getName());
                String rightEq = foreignKeyColumnInfo.getName();
                EqualsTo eqCondition = ConditionBuilder.eq(leftEq, String.format("#{%s.%s}", "item", rightEq));
                // 将表达式添加到条件树
                if (whereCondition == null) {
                    whereCondition = eqCondition;
                } else {
                    whereCondition = new AndExpression(whereCondition, eqCondition);
                }
            }
            RelationSelectHelper.buildForeachElement(whereElement, whereCondition);
        }
    }
}