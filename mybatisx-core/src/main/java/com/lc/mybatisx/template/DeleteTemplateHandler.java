package com.lc.mybatisx.template;

import com.lc.mybatisx.annotation.Lock;
import com.lc.mybatisx.annotation.LogicDelete;
import com.lc.mybatisx.model.ColumnInfo;
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

import javax.persistence.Id;
import java.util.List;

public class DeleteTemplateHandler {

    private static final Logger logger = LoggerFactory.getLogger(DeleteTemplateHandler.class);

    public XNode execute(MapperInfo mapperInfo, MethodInfo methodInfo) {
        return buildDeleteXNode(mapperInfo, methodInfo);
    }

    private XNode buildDeleteXNode(MapperInfo mapperInfo, MethodInfo methodInfo) {
        Document document = DocumentHelper.createDocument();
        Element mapperElement = document.addElement("mapper");
        Element updateElement = mapperElement.addElement("delete");
        updateElement.addAttribute("id", methodInfo.getMethodName());
        updateElement.addText(String.format("delete from %s", mapperInfo.getTableInfo().getTableName()));

        Element setElement = updateElement.addElement("set");
        Element dbTrimElement = setElement.addElement("trim");
        dbTrimElement.addAttribute("suffixOverrides", ",");

        List<ColumnInfo> columnInfoList = mapperInfo.getResultMapInfo().getColumnInfoList();
        for (int i = 0; i < columnInfoList.size(); i++) {
            ColumnInfo columnInfo = columnInfoList.get(i);
            String javaColumnName = columnInfo.getJavaColumnName();
            String dbColumnName = columnInfo.getDbColumnName();
            Id id = columnInfo.getId();
            Lock lock = columnInfo.getLock();
            LogicDelete delete = columnInfo.getDelete();

            if ("deleteById".equals(methodInfo.getMethodName())) {
                dbTrimElement.addText(String.format("%s, ", columnInfo.getDbColumnName()));
            } else {
                throw new RuntimeException("暂未实现");
            }
        }

        String insertXmlString = document.asXML();
        logger.info(insertXmlString);
        XPathParser xPathParser = XmlUtils.processXml(insertXmlString);
        XNode xNode = xPathParser.evalNode("/mapper/update");
        return xNode;
    }

    private String buildTypeHandler(String typeHandler) {
        if (StringUtils.isNotBlank(typeHandler)) {
            return String.format(", typeHandler=%s", typeHandler);
        }
        return "";
    }

    private String buildTestNotNull(String javaColumnName) {
        return String.format("%s != null", javaColumnName);
    }

}
