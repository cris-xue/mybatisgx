package com.lc.mybatisx.template;

import com.lc.mybatisx.context.EntityInfoContextHolder;
import com.lc.mybatisx.model.ColumnInfoAnnotationInfo;
import com.lc.mybatisx.model.EntityInfo;
import com.lc.mybatisx.model.MapperInfo;
import com.lc.mybatisx.model.MethodInfo;
import com.lc.mybatisx.utils.XmlUtils;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.parsing.XPathParser;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SelectTemplateHandler {

    private static final Logger logger = LoggerFactory.getLogger(SelectTemplateHandler.class);

    private WhereTemplateHandler whereTemplateHandler = new WhereTemplateHandler();

    public XNode execute(MapperInfo mapperInfo, MethodInfo methodInfo) {
        return buildSelectXNode(mapperInfo, methodInfo);
    }

    private XNode buildSelectXNode(MapperInfo mapperInfo, MethodInfo methodInfo) {
        Document document = DocumentHelper.createDocument();
        Element mapperElement = document.addElement("mapper");
        Element selectElement = mapperElement.addElement("select");
        selectElement.addAttribute("id", methodInfo.getMethodName());
        selectElement.addAttribute("resultMap", methodInfo.getResultMapId());
        selectElement.addText("select");

        Element dbTrimElement = selectElement.addElement("trim");
        dbTrimElement.addAttribute("prefix", "");
        dbTrimElement.addAttribute("suffix", "");
        dbTrimElement.addAttribute("suffixOverrides", ",");

        Class<?> methodReturnType = methodInfo.getMethodReturnInfo().getType();
        EntityInfo entityInfo = EntityInfoContextHolder.get(methodReturnType);
        entityInfo.getTableColumnInfoList().forEach(columnInfo -> {
            // 外键不存在，只需要添加字段。外键存在，则需要添加字段和外键
            ColumnInfoAnnotationInfo associationEntityInfo = columnInfo.getColumnInfoAnnotationInfo();
            if (associationEntityInfo == null) {
                dbTrimElement.addText(String.format("%s, ", columnInfo.getDbColumnName()));
            } else {
                associationEntityInfo.getForeignKeyColumnInfoList().forEach(foreignKeyColumnInfo -> {
                    dbTrimElement.addText(String.format("%s, ", foreignKeyColumnInfo.getName()));
                });
            }
        });

        selectElement.addText(String.format("from %s", mapperInfo.getEntityInfo().getTableName()));

        whereTemplateHandler.execute(selectElement, mapperInfo.getEntityInfo(), methodInfo);

        String insertXmlString = document.asXML();
        logger.debug(insertXmlString);
        XPathParser xPathParser = XmlUtils.processXml(insertXmlString);
        XNode xNode = xPathParser.evalNode("/mapper/select");
        return xNode;
    }
}
