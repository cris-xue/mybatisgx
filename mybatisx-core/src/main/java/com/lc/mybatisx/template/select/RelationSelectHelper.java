package com.lc.mybatisx.template.select;

import com.lc.mybatisx.model.ColumnInfo;
import com.lc.mybatisx.model.EntityRelationSelectInfo;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;

public class RelationSelectHelper {

    public static Element buildSelectElement(Element mapperElement, EntityRelationSelectInfo entityRelationSelectInfo) {
        Element selectElement = mapperElement.addElement("select");
        selectElement.addAttribute("id", entityRelationSelectInfo.getId());
        selectElement.addAttribute("resultMap", entityRelationSelectInfo.getResultMapId());
        ColumnInfo columnInfo = entityRelationSelectInfo.getColumnInfo();
        String fetchSize = columnInfo.getColumnRelationInfo().getFetchSize();
        if (StringUtils.isNotBlank(fetchSize)) {
            selectElement.addAttribute("fetchSize", fetchSize);
        }
        return selectElement;
    }
}
