package com.lc.mybatisx.template;

import com.lc.mybatisx.model.*;
import com.lc.mybatisx.utils.XmlUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.parsing.XPathParser;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

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
        selectElement.addAttribute("resultMap", methodInfo.getResultMapInfo().getId());
        selectElement.addText("select");

        Element dbTrimElement = selectElement.addElement("trim");
        dbTrimElement.addAttribute("prefix", "");
        dbTrimElement.addAttribute("suffix", "");
        dbTrimElement.addAttribute("suffixOverrides", ",");
        methodInfo.getResultMapInfo().getColumnInfoList().forEach(columnInfo -> {
            dbTrimElement.addText(String.format("%s, ", columnInfo.getDbColumnName()));
        });

        selectElement.addText(String.format("from %s", mapperInfo.getTableInfo().getTableName()));

        whereTemplateHandler.execute(selectElement, mapperInfo.getTableInfo(), methodInfo);

        String insertXmlString = document.asXML();
        logger.info(insertXmlString);
        XPathParser xPathParser = XmlUtils.processXml(insertXmlString);
        XNode xNode = xPathParser.evalNode("/mapper/select");
        return xNode;
    }

    private void buildCondition(Element parentElement, ColumnInfo columnInfo, ConditionInfo conditionInfo) {
        String op = conditionInfo.getOp();
        List<MethodParamInfo> methodParamInfoList = conditionInfo.getMethodParamInfoList();
        parentElement.addText(String.format(" %s %s %s ", conditionInfo.getLinkOp(), conditionInfo.getDbColumnName(), op));
        if ("in".equals(op)) {
            MethodParamInfo methodParamInfo = methodParamInfoList.get(0);
            Element foreachElement = parentElement.addElement("foreach");
            foreachElement.addAttribute("item", "item");
            foreachElement.addAttribute("index", "index");
            foreachElement.addAttribute("collection", methodParamInfo.getParamName());
            foreachElement.addAttribute("open", "(");
            foreachElement.addAttribute("separator", ",");
            foreachElement.addAttribute("close", ")");
            foreachElement.addText("#{item}");
        } else if ("between".equals(op)) {
        } else {
            String typeHandler = columnInfo.getTypeHandler();
            String typeHandlerTemplate = "";
            if (StringUtils.isNotBlank(typeHandler)) {
                typeHandlerTemplate = String.format(", typeHandler=%s", typeHandler);
            }
            MethodParamInfo methodParamInfo = methodParamInfoList.get(0);
            parentElement.addText(String.format("#{%s%s}", methodParamInfo.getParamName(), typeHandlerTemplate));
        }
    }

}
