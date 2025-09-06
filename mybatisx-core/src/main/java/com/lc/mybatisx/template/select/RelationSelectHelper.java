package com.lc.mybatisx.template.select;

import com.lc.mybatisx.model.ColumnInfo;
import com.lc.mybatisx.model.EntityRelationSelectInfo;
import net.sf.jsqlparser.expression.Expression;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;

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
}
