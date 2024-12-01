package com.lc.mybatisx.template;

import com.lc.mybatisx.annotation.Id;
import com.lc.mybatisx.model.AssociationTableInfo;
import com.lc.mybatisx.model.ColumnInfo;
import com.lc.mybatisx.model.MethodInfo;
import com.lc.mybatisx.model.ResultMapInfo;
import com.lc.mybatisx.utils.XmlUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.parsing.XPathParser;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ResultMapTemplateHandler {

    private static final Logger logger = LoggerFactory.getLogger(ResultMapTemplateHandler.class);

    public List<XNode> execute(List<MethodInfo> methodInfoList) {
        Map<String, ResultMapInfo> resultMapInfoMap = new LinkedHashMap<>();
        for (int i = 0; i < methodInfoList.size(); i++) {
            MethodInfo methodInfo = methodInfoList.get(i);
            ResultMapInfo resultMapInfo = methodInfo.getResultMapInfo();
            if (resultMapInfo == null) {
                continue;
            }
            resultMapInfoMap.put(resultMapInfo.getId(), resultMapInfo);
        }

        List<XNode> xNodeList = new ArrayList<>();
        resultMapInfoMap.forEach((k, resultMapInfo) -> {
            Document document = DocumentHelper.createDocument();
            Element mapperElement = document.addElement("mapper");
            Element resultMapElement = mapperElement.addElement("resultMap");
            resultMapElement.addAttribute("id", resultMapInfo.getId());
            resultMapElement.addAttribute("type", resultMapInfo.getType());
            addColumnElement(resultMapElement, resultMapInfo.getColumnInfoList());
            addAssociationElement(resultMapElement, resultMapInfo.getAssociationTableInfoList());
            String resultMapXmlString = XmlUtils.writeString(document);
            logger.info(resultMapXmlString);

            XPathParser xPathParser = XmlUtils.processXml(resultMapXmlString);
            XNode xNode = xPathParser.evalNode("/mapper/resultMap");
            xNodeList.add(xNode);
        });

        return xNodeList;
    }

    private void addColumnElement(Element resultMapElement, List<ColumnInfo> columnInfoList) {
        columnInfoList.forEach(columnInfo -> {
            Id id = columnInfo.getId();
            Element element = resultMapElement.addElement(id != null ? "id" : "result");
            element.addAttribute("property", columnInfo.getJavaColumnName());
            element.addAttribute("column", columnInfo.getDbColumnName());
            String dbTypeName = columnInfo.getDbTypeName();
            element.addAttribute("jdbcType", StringUtils.isNotBlank(dbTypeName) ? dbTypeName.toUpperCase() : null);
            element.addAttribute("typeHandler", columnInfo.getTypeHandler());
        });
    }

    private void addAssociationElement(Element resultMapElement, List<AssociationTableInfo> associationTableInfoList) {
        associationTableInfoList.forEach(associationTableInfo -> {
            Element resultMapCollectionElement = resultMapElement.addElement("collection");
            resultMapCollectionElement.addAttribute("property", associationTableInfo.getJavaColumnName());
            resultMapCollectionElement.addAttribute("javaType", "ArrayList");
            resultMapCollectionElement.addAttribute("ofType", associationTableInfo.targetEntity.getTypeName());
            resultMapCollectionElement.addAttribute("select", associationTableInfo.getSelect());
            resultMapCollectionElement.addAttribute("column", "id");
        });
    }

}
