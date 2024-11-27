package com.lc.mybatisx.template;

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

import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;

public class ResultMapTemplateHandler {

    private static final Logger logger = LoggerFactory.getLogger(ResultMapTemplateHandler.class);

    public List<XNode> execute(List<MethodInfo> methodInfoList) {
        List<XNode> xNodeList = new ArrayList<>();
        for (int i = 0; i < methodInfoList.size(); i++) {
            MethodInfo methodInfo = methodInfoList.get(i);
            ResultMapInfo resultMapInfo = methodInfo.getResultMapInfo();

            Document document = DocumentHelper.createDocument();
            Element mapperElement = document.addElement("mapper");
            Element resultMapElement = mapperElement.addElement("resultMap");
            resultMapElement.addAttribute("id", resultMapInfo.getId());
            resultMapElement.addAttribute("type", resultMapInfo.getType());
            addElement(resultMapElement, resultMapInfo.getColumnInfoList());
            String resultMapXmlString = XmlUtils.writeString(document);
            logger.info(resultMapXmlString);
            /*Template template = FreeMarkerUtils.getTemplate("mapper/result_map.ftl");
            Map<String, Object> templateData = new HashMap<>();
            templateData.put("resultMapInfo", resultMapInfo);
            templateData.put("resultMapXmlString", resultMapXmlString);
            String resultMapXml = FreeMarkerUtils.processTemplate(templateData, template);*/

            XPathParser xPathParser = XmlUtils.processXml(resultMapXmlString);
            XNode xNode = xPathParser.evalNode("/mapper/resultMap");
            xNodeList.add(xNode);
        }

        return xNodeList;
    }

    private void addElement(Element resultMapElement, List<ColumnInfo> columnInfoList) {
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

}
