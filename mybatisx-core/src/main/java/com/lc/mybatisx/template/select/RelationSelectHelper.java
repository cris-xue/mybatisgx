package com.lc.mybatisx.template.select;

import com.lc.mybatisx.model.ColumnInfo;
import com.lc.mybatisx.model.ForeignKeyColumnInfo;
import com.lc.mybatisx.model.RelationColumnInfo;
import com.lc.mybatisx.model.ResultMapInfo;
import com.lc.mybatisx.template.ConditionBuilder;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;

public class RelationSelectHelper {

    public static Element buildSelectElement(Element mapperElement, ResultMapInfo resultMapInfo, String sql) {
        Element selectElement = mapperElement.addElement("select");
        selectElement.addAttribute("id", resultMapInfo.getNestedSelectId());
        selectElement.addAttribute("resultMap", resultMapInfo.getId());
        RelationColumnInfo relationColumnInfo = (RelationColumnInfo) resultMapInfo.getColumnInfo();
        String fetchSize = relationColumnInfo.getFetchSize();
        if (StringUtils.isNotBlank(fetchSize)) {
            selectElement.addAttribute("fetchSize", fetchSize);
        }
        selectElement.addText(sql);
        return selectElement;
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
        foreachElement.addAttribute("collection", "nested_select_collection");
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

    /**
     * 用户关联用户详情：关联查询用户详情的时候需要获取【userDetail -> user.id】
     * <code>
     * user: {
     * userDetail: {
     * user: {
     * id: user.id
     * }
     * }
     * }
     * // column中的key会映射userDetail中的user对象的id
     * <association property="userDetail" column="{user.id=id}"></association>
     * where user_detail.user_id = #{user.id}
     * </code>
     *
     * @param mappedByColumnInfo
     * @return
     */
    public static String buildRelationSelectParam(ColumnInfo mappedByColumnInfo, ForeignKeyColumnInfo inverseForeignKeyColumnInfo) {
        return mappedByColumnInfo.getJavaColumnName() + "." + inverseForeignKeyColumnInfo.getReferencedColumnName();
    }
}
