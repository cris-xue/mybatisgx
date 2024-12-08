package com.lc.mybatisx.template;

import com.lc.mybatisx.annotation.Lock;
import com.lc.mybatisx.annotation.LogicDelete;
import com.lc.mybatisx.model.ColumnInfo;
import com.lc.mybatisx.model.EntityInfo;
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

import java.util.List;

public class UpdateTemplateHandler {

    private static final Logger logger = LoggerFactory.getLogger(UpdateTemplateHandler.class);

    private WhereTemplateHandler whereTemplateHandler = new WhereTemplateHandler();

    public XNode execute(MapperInfo mapperInfo, MethodInfo methodInfo) {
        return buildUpdateXNode(mapperInfo, methodInfo);
    }

    private XNode buildUpdateXNode(MapperInfo mapperInfo, MethodInfo methodInfo) {
        EntityInfo entityInfo = mapperInfo.getEntityInfo();

        Document document = DocumentHelper.createDocument();
        Element mapperElement = document.addElement("mapper");
        Element updateElement = mapperElement.addElement("update");
        updateElement.addAttribute("id", methodInfo.getMethodName());
        updateElement.addText(String.format("update %s", entityInfo.getTableName()));

        Element setElement = updateElement.addElement("set");
        Element setTrimElement = setElement.addElement("trim");
        setTrimElement.addAttribute("suffixOverrides", ",");

        this.setValue(entityInfo, methodInfo, setTrimElement);
        whereTemplateHandler.execute(updateElement, mapperInfo.getEntityInfo(), methodInfo);

        String insertXmlString = document.asXML();
        logger.info(insertXmlString);
        XPathParser xPathParser = XmlUtils.processXml(insertXmlString);
        XNode xNode = xPathParser.evalNode("/mapper/update");
        return xNode;
    }

    private void setValue(EntityInfo entityInfo, MethodInfo methodInfo, Element setTrimElement) {
        List<ColumnInfo> columnInfoList = entityInfo.getColumnInfoList();
        for (int i = 0; i < columnInfoList.size(); i++) {
            ColumnInfo columnInfo = columnInfoList.get(i);

            String javaColumnName = columnInfo.getJavaColumnName();
            String dbColumnName = columnInfo.getDbColumnName();
            String typeHandler = columnInfo.getTypeHandler();
            Lock lock = columnInfo.getLock();
            LogicDelete logicDelete = columnInfo.getLogicDelete();

            if (lock != null) {
                String javaColumn = String.format("%s = #{%s} + %s, ", dbColumnName, javaColumnName, lock.increment());
                setTrimElement.addText(javaColumn);
                continue;
            }

            if (logicDelete != null) {
                String javaColumn = String.format("%s = %s, ", dbColumnName, logicDelete.show());
                setTrimElement.addText(javaColumn);
                continue;
            }

            if (methodInfo.getDynamic()) {
                Element setTrimIfElement = setTrimElement.addElement("if");
                setTrimIfElement.addAttribute("test", buildTestNotNull(javaColumnName));
                String javaColumn = String.format("#{%s%s},", javaColumnName, buildTypeHandler(typeHandler));
                setTrimIfElement.addText(String.format("%s = %s", dbColumnName, javaColumn));
            } else {
                String javaColumn = String.format("#{%s%s},", javaColumnName, buildTypeHandler(typeHandler));
                setTrimElement.addText(String.format("%s = %s", dbColumnName, javaColumn));
            }
        }
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
