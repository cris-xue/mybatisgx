package com.lc.mybatisx.template;

import com.lc.mybatisx.model.ConditionInfo;
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

import java.util.List;

public class SelectTemplateHandler {

    private static final Logger logger = LoggerFactory.getLogger(SelectTemplateHandler.class);

    public XNode execute(MapperInfo mapperInfo, MethodInfo methodInfo) {
        return buildSelectXNode(mapperInfo, methodInfo);
    }

    private XNode buildSelectXNode(MapperInfo mapperInfo, MethodInfo methodInfo) {
        Document document = DocumentHelper.createDocument();
        Element mapperElement = document.addElement("mapper");
        Element selectElement = mapperElement.addElement("select");
        selectElement.addAttribute("id", methodInfo.getMethodName());
        selectElement.addAttribute("resultMap", mapperInfo.getResultMapInfo().getId());
        selectElement.addText("select");

        Element dbTrimElement = selectElement.addElement("trim");
        dbTrimElement.addAttribute("prefix", "");
        dbTrimElement.addAttribute("suffix", "");
        dbTrimElement.addAttribute("suffixOverrides", ",");
        mapperInfo.getResultMapInfo().getColumnInfoList().forEach(columnInfo -> {
            dbTrimElement.addText(String.format("%s, ", columnInfo.getDbColumnName()));
        });

        selectElement.addText(String.format("from %s", mapperInfo.getTableName()));

        Element whereElement = selectElement.addElement("where");
        if (methodInfo.getDynamic()) {
            Element trimElement = whereElement.addElement("trim");
            trimElement.addAttribute("prefix", "(");
            trimElement.addAttribute("suffix", ")");
            trimElement.addAttribute("prefixOverrides", "AND|OR|and|or");
            methodInfo.getConditionInfoList().forEach(conditionInfo -> {
                Element ifElement = trimElement.addElement("if");
                List<String> paramNameList = conditionInfo.getParamName();
                ifElement.addAttribute("test", String.format("%s != null", paramNameList.get(0)));
                buildCondition(ifElement, conditionInfo);
            });
        } else {
            methodInfo.getConditionInfoList().forEach(conditionInfo -> {
                buildCondition(whereElement, conditionInfo);
            });
        }

        String insertXmlString = document.asXML();
        logger.info(insertXmlString);
        XPathParser xPathParser = XmlUtils.processXml(insertXmlString);
        XNode xNode = xPathParser.evalNode("/mapper/select");
        return xNode;
    }

    private void buildCondition(Element parentElement, ConditionInfo conditionInfo) {
        String op = conditionInfo.getOp();
        List<String> paramNameList = conditionInfo.getParamName();
        parentElement.addText(String.format(" %s %s %s ", conditionInfo.getLinkOp(), conditionInfo.getDbColumnName(), op));
        if ("in".equals(op)) {
            Element foreachElement = parentElement.addElement("foreach");
            foreachElement.addAttribute("item", "item");
            foreachElement.addAttribute("index", "index");
            foreachElement.addAttribute("collection", paramNameList.get(0));
            foreachElement.addAttribute("open", "(");
            foreachElement.addAttribute("separator", ",");
            foreachElement.addAttribute("close", ")");
            foreachElement.addText("#{item}");
        } else if ("between".equals(op)) {
            parentElement.addText(String.format("#{%s} and #{%s}", paramNameList.get(0), paramNameList.get(1)));
        } else {
            parentElement.addText(String.format("#{%s}", paramNameList.get(0)));
        }
    }

}
