package com.lc.mybatisx.template;

import com.lc.mybatisx.model.MapperInfo;
import com.lc.mybatisx.model.MethodInfo;
import com.lc.mybatisx.utils.XmlUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.parsing.XPathParser;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InsertTemplateHandler {

    private static final Logger logger = LoggerFactory.getLogger(InsertTemplateHandler.class);

    public XNode execute(MapperInfo mapperInfo, MethodInfo methodInfo) {
        return simpleTemplateHandle(mapperInfo, methodInfo);
    }

    private XNode simpleTemplateHandle(MapperInfo mapperInfo, MethodInfo methodInfo) {
        String methodName = methodInfo.getMethodName();
        if ("insert".equals(methodName)) {
            return buildInsertXNode(mapperInfo, methodInfo);
        } else if ("insertSelective".equals(methodName)) {
            return buildInsertSelectiveXNode(mapperInfo, methodInfo);
        } else {
            throw new RuntimeException("暂未实现");
        }
    }

    private XNode buildInsertXNode(MapperInfo mapperInfo, MethodInfo methodInfo) {
        /*InsertNode insertNode = new InsertNode();
        insertNode.id("");
        insertNode.keyProperty("");
        insertNode.useGeneratedKeys(Boolean.TRUE);
        insertNode.tableName(mapperInfo.getTableName());
        insertNode.dbColumn(mapperInfo.getResultMapInfo().getColumnInfoList());
        insertNode.javaColumn(mapperInfo.getResultMapInfo().getColumnInfoList());
        String insertXmlString = insertNode.getXml();*/

        Document document = DocumentHelper.createDocument();
        Element mapperElement = document.addElement("mapper");
        Element insertElement = mapperElement.addElement("insert");
        insertElement.addAttribute("id", "insert");
        insertElement.addAttribute("keyProperty", "id");
        insertElement.addAttribute("useGeneratedKeys", "true");
        insertElement.addText(String.format("insert into %s", mapperInfo.getTableName()));

        Element dbTrimElement = insertElement.addElement("trim");
        dbTrimElement.addAttribute("prefix", "(");
        dbTrimElement.addAttribute("suffix", ")");
        dbTrimElement.addAttribute("suffixOverrides", ",");
        mapperInfo.getResultMapInfo().getColumnInfoList().forEach(columnInfo -> {
            dbTrimElement.addText(String.format("%s, ", columnInfo.getDbColumnName()));
        });

        Element javaTrimElement = insertElement.addElement("trim");
        javaTrimElement.addAttribute("prefix", "values (");
        javaTrimElement.addAttribute("suffix", ")");
        javaTrimElement.addAttribute("suffixOverrides", ",");
        mapperInfo.getResultMapInfo().getColumnInfoList().forEach(columnInfo -> {
            String typeHandler = columnInfo.getTypeHandler();
            String typeHandlerTemplate = "";
            if (StringUtils.isNotBlank(typeHandler)) {
                typeHandlerTemplate = String.format(", typeHandler=%s", typeHandler);
            }
            String javaColumn = String.format("#{%s%s}, ", columnInfo.getJavaColumnName(), typeHandlerTemplate);
            javaTrimElement.addText(javaColumn);
        });

        String insertXmlString = document.asXML();
        logger.info(insertXmlString);
        XPathParser xPathParser = XmlUtils.processXml(insertXmlString);
        XNode xNode = xPathParser.evalNode("/mapper/insert");
        return xNode;
    }

    private XNode buildInsertSelectiveXNode(MapperInfo mapperInfo, MethodInfo methodInfo) {
        Document document = DocumentHelper.createDocument();
        Element mapperElement = document.addElement("mapper");
        Element insertElement = mapperElement.addElement("insert");
        insertElement.addAttribute("id", "insertSelective");
        insertElement.addAttribute("keyProperty", "id");
        insertElement.addAttribute("useGeneratedKeys", "true");
        insertElement.addText(String.format("insert into %s", mapperInfo.getTableName()));

        Element dbTrimElement = insertElement.addElement("trim");
        dbTrimElement.addAttribute("prefix", "(");
        dbTrimElement.addAttribute("suffix", ")");
        dbTrimElement.addAttribute("suffixOverrides", ",");
        mapperInfo.getResultMapInfo().getColumnInfoList().forEach(columnInfo -> {
            Element dbTrimIfElement = dbTrimElement.addElement("if");
            dbTrimIfElement.addAttribute("test", String.format("%s != null", columnInfo.getJavaColumnName()));
            dbTrimIfElement.addText(String.format("%s, ", columnInfo.getDbColumnName()));
        });

        Element javaTrimElement = insertElement.addElement("trim");
        javaTrimElement.addAttribute("prefix", "values (");
        javaTrimElement.addAttribute("suffix", ")");
        javaTrimElement.addAttribute("suffixOverrides", ",");
        mapperInfo.getResultMapInfo().getColumnInfoList().forEach(columnInfo -> {
            Element javaTrimIfElement = javaTrimElement.addElement("if");
            javaTrimIfElement.addAttribute("test", String.format("%s != null", columnInfo.getJavaColumnName()));
            String typeHandler = columnInfo.getTypeHandler();
            String typeHandlerTemplate = "";
            if (StringUtils.isNotBlank(typeHandler)) {
                typeHandlerTemplate = String.format(", typeHandler=%s", typeHandler);
            }
            String javaColumn = String.format("#{%s%s},", columnInfo.getJavaColumnName(), typeHandlerTemplate);
            javaTrimIfElement.addText(javaColumn);
        });

        String insertXmlString = document.asXML();
        logger.info(insertXmlString);
        XPathParser xPathParser = XmlUtils.processXml(insertXmlString);
        XNode xNode = xPathParser.evalNode("/mapper/insert");
        return xNode;
    }

}
