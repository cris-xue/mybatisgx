package com.lc.mybatisx.template.select;

import com.lc.mybatisx.model.ColumnInfo;
import com.lc.mybatisx.model.EntityRelationSelectInfo;
import com.lc.mybatisx.model.ForeignKeyColumnInfo;
import com.lc.mybatisx.template.ConditionBuilder;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;

import java.util.List;

public class RelationSelectHelper {

    public static Element buildSelectElement(Element mapperElement, EntityRelationSelectInfo entityRelationSelectInfo, String sql) {
        Element selectElement = mapperElement.addElement("select");
        selectElement.addAttribute("id", entityRelationSelectInfo.getId());
        selectElement.addAttribute("resultMap", entityRelationSelectInfo.getResultMapId());
        ColumnInfo columnInfo = entityRelationSelectInfo.getColumnInfo();
        String fetchSize = columnInfo.getColumnRelationInfo().getFetchSize();
        if (StringUtils.isNotBlank(fetchSize)) {
            selectElement.addAttribute("fetchSize", fetchSize);
        }
        selectElement.addText(sql);
        return selectElement;
    }

    public static Element buildWhereElement(Element selectElement, Expression whereCondition) {
        Element whereElement = selectElement.addElement("where");
        whereElement.addText(whereCondition.toString());
        return whereElement;
    }

    public static Element buildForeachElement(Element whereElement, Expression whereCondition) {
        Element foreachElement = whereElement.addElement("foreach");
        foreachElement.addAttribute("item", "item");
        foreachElement.addAttribute("index", "index");
        foreachElement.addAttribute("collection", "nested_select_collection");
        foreachElement.addAttribute("open", "(");
        foreachElement.addAttribute("separator", " or ");
        foreachElement.addAttribute("close", ")");
        foreachElement.addText(whereCondition.toString());
        return foreachElement;
    }

    /**
     * 将表达式添加到条件树
     * @param middleTableName 中间表名称
     * @param foreignKeyColumnInfoList 外键字段列表
     * @return
     */
    public static Expression buildManyToManyWhere(String middleTableName, List<ForeignKeyColumnInfo> foreignKeyColumnInfoList) {
        Expression whereCondition = null;
        for (ForeignKeyColumnInfo foreignKeyColumnInfo : foreignKeyColumnInfoList) {
            String leftEq = String.format("%s.%s", middleTableName, foreignKeyColumnInfo.getName());
            String rightEq = foreignKeyColumnInfo.getName();
            whereCondition = buildWhereCondition(whereCondition, leftEq, rightEq);
        }
        return whereCondition;
    }

    public static Expression buildWhereCondition(Expression whereCondition, String leftEq, String rightEq) {
        EqualsTo eqCondition = ConditionBuilder.eq(leftEq, String.format("#{%s}", rightEq));
        return whereCondition == null ? eqCondition : new AndExpression(whereCondition, eqCondition);
    }
}
