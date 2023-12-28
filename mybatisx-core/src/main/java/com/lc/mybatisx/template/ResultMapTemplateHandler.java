package com.lc.mybatisx.template;

import com.lc.mybatisx.model.ColumnInfo;
import com.lc.mybatisx.model.ResultMapInfo;
import com.lc.mybatisx.model.YesOrNo;
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

public class ResultMapTemplateHandler {

    private static final Logger logger = LoggerFactory.getLogger(ResultMapTemplateHandler.class);

    public XNode execute(ResultMapInfo resultMapInfo) {
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
        return xNode;
    }

    private void addElement(Element resultMapElement, List<ColumnInfo> columnInfoList) {
        columnInfoList.forEach(columnInfo -> {
            YesOrNo yesOrNo = columnInfo.getPrimaryKey();
            Element element;
            if (yesOrNo == YesOrNo.YES) {
                element = resultMapElement.addElement("id");
            } else if (yesOrNo == YesOrNo.NO) {
                element = resultMapElement.addElement("result");
            } else {
                throw new RuntimeException();
            }
            element.addAttribute("property", columnInfo.getJavaColumnName());
            element.addAttribute("column", columnInfo.getDbColumnName());
            String dbTypeName = columnInfo.getDbTypeName();
            element.addAttribute("jdbcType", StringUtils.isNotBlank(dbTypeName) ? dbTypeName.toUpperCase() : null);
            element.addAttribute("typeHandler", columnInfo.getTypeHandler());
        });
    }

}
