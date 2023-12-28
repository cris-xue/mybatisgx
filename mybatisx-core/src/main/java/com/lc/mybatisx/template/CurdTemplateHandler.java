package com.lc.mybatisx.template;

import com.lc.mybatisx.model.MapperInfo;
import com.lc.mybatisx.model.MethodInfo;
import com.lc.mybatisx.model.MethodNameInfo;
import com.lc.mybatisx.utils.FreeMarkerUtils;
import com.lc.mybatisx.utils.XmlUtils;
import freemarker.template.Template;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.parsing.XPathParser;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CurdTemplateHandler {

    private static final Logger logger = LoggerFactory.getLogger(ResultMapTemplateHandler.class);

    public List<XNode> execute(MapperInfo mapperInfo) {
        List<MethodInfo> methodInfoList = mapperInfo.getMethodInfoList();
        List<XNode> xNodeList = new ArrayList<>(15);
        for (int i = 0; i < methodInfoList.size(); i++) {
            MethodInfo methodInfo = methodInfoList.get(i);
            MethodNameInfo methodNameInfo = methodInfo.getMethodNameInfo();
            if (methodNameInfo == null) {
                XNode xNode = simpleTemplateHandle(mapperInfo, methodInfo);
                xNodeList.add(xNode);
            } else {
                XNode xNode = complexTemplateHandle(mapperInfo, methodInfo);
                xNodeList.add(xNode);
            }
        }
        return xNodeList;
    }

    private XNode simpleTemplateHandle(MapperInfo mapperInfo, MethodInfo methodInfo) {
        String methodName = methodInfo.getMethodName();
        if ("insert".equals(methodName)) {
            return buildInsertXNode(mapperInfo);
        } else if ("insertSelective".equals(methodName)) {
            return buildInsertSelectiveXNode(mapperInfo);
        } else {
            String templatePath = String.format("mapper/mysql/simple_mapper/%s.ftl", methodInfo.getMethodName());
            Template template = FreeMarkerUtils.getTemplate(templatePath);
            return generateSql(template, mapperInfo, methodInfo);
        }
    }

    private XNode buildInsertXNode(MapperInfo mapperInfo) {
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

    private XNode buildInsertSelectiveXNode(MapperInfo mapperInfo) {
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

    private XNode complexTemplateHandle(MapperInfo mapperInfo, MethodInfo methodInfo) {
        MethodNameInfo methodNameInfo = methodInfo.getMethodNameInfo();
        String path = methodInfo.getDynamic() ? "mapper/mysql/%s_mapper_dynamic.ftl" : "mapper/mysql/%s_mapper.ftl";
        String templatePath = String.format(path, methodNameInfo.getAction());
        Template template = FreeMarkerUtils.getTemplate(templatePath);
        return generateSql(template, mapperInfo, methodInfo);
    }

    private XNode generateSql(Template template, MapperInfo mapperInfo, MethodInfo methodInfo) {
        Map<String, Object> templateData = new HashMap<>();
        templateData.put("mapperInfo", mapperInfo);
        templateData.put("methodInfo", methodInfo);
        templateData.put("resultMapInfo", mapperInfo.getResultMapInfo());

        String methodXml = FreeMarkerUtils.processTemplate(templateData, template);
        XPathParser xPathParser = XmlUtils.processXml(methodXml);
        XNode xNode = xPathParser.evalNode("/mapper/*");
        return xNode;
    }

}
