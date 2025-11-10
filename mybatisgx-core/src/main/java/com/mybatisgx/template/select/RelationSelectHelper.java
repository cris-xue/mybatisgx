package com.mybatisgx.template.select;

import com.mybatisgx.ext.BatchResultLoader;
import com.mybatisgx.model.BatchNestedResultMapInfo;
import com.mybatisgx.model.RelationColumnInfo;
import com.mybatisgx.model.ResultMapInfo;
import com.mybatisgx.model.SimpleNestedResultMapInfo;
import com.mybatisgx.template.ConditionBuilder;
import com.mybatisgx.utils.TypeUtils;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import org.dom4j.Element;

public class RelationSelectHelper {

    public static Element buildSelectElement(Element mapperElement, ResultMapInfo resultMapInfo, String sql) {
        Element selectElement = mapperElement.addElement("select");
        selectElement.addAttribute("id", resultMapInfo.getNestedSelectId());
        selectElement.addAttribute("resultMap", resultMapInfo.getId());
        selectElement.addAttribute("fetchSize", getFetchSize(resultMapInfo));
        selectElement.addText(sql);
        return selectElement;
    }

    private static String getFetchSize(ResultMapInfo resultMapInfo) {
        RelationColumnInfo relationColumnInfo = null;
        if (TypeUtils.typeEquals(resultMapInfo, ResultMapInfo.class) || TypeUtils.typeEquals(resultMapInfo, SimpleNestedResultMapInfo.class)) {
            relationColumnInfo = (RelationColumnInfo) resultMapInfo.getColumnInfo();
        }
        if (TypeUtils.typeEquals(resultMapInfo, BatchNestedResultMapInfo.class)) {
            relationColumnInfo = (RelationColumnInfo) resultMapInfo.getColumnInfo();
        }
        return relationColumnInfo != null ? relationColumnInfo.getFetchSize() : null;
    }

    public static Element buildWhereElement(Element selectElement) {
        return selectElement.addElement("where");
    }

    public static Element buildWhereElement(Element selectElement, Expression whereCondition) {
        Element whereElement = buildWhereElement(selectElement);
        whereElement.addText(whereCondition.toString());
        return whereElement;
    }

    public static Element buildForeachElement(Element whereElement, Expression whereCondition) {
        Element foreachElement = whereElement.addElement("foreach");
        foreachElement.addAttribute("item", "item");
        foreachElement.addAttribute("index", "index");
        foreachElement.addAttribute("collection", BatchResultLoader.NESTED_SELECT_PARAM_COLLECTION);
        foreachElement.addAttribute("open", "(");
        foreachElement.addAttribute("separator", " or ");
        foreachElement.addAttribute("close", ")");
        foreachElement.addText(whereCondition.toString());
        return foreachElement;
    }

    /**
     * 将表达式添加到条件树
     *
     * @param whereConditionExpression
     * @param leftEq
     * @param rightEq
     * @return
     */
    public static Expression buildWhereConditionExpression(Expression whereConditionExpression, String leftEq, String rightEq) {
        EqualsTo eqCondition = ConditionBuilder.eq(leftEq, rightEq);
        return whereConditionExpression == null ? eqCondition : new AndExpression(whereConditionExpression, eqCondition);
    }
}
