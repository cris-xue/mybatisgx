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
        updateElement.addText(String.format("delete from %s", mapperInfo.getTableName()));

        Element setElement = updateElement.addElement("set");
        Element dbTrimElement = setElement.addElement("trim");
        dbTrimElement.addAttribute("suffixOverrides", ",");

        List<ColumnInfo> columnInfoList = mapperInfo.getResultMapInfo().getColumnInfoList();
        if (methodInfo.getDynamic()) {
            for (int i = 0; i < columnInfoList.size(); i++) {
                ColumnInfo columnInfo = columnInfoList.get(i);
                String javaColumnName = columnInfo.getJavaColumnName();
                String dbColumnName = columnInfo.getDbColumnName();
                Lock lock = columnInfo.getLock();
                LogicDelete delete = columnInfo.getDelete();

                if (lock != null) {
                    String javaColumn = String.format("%s, ", dbColumnName);
                    dbTrimElement.addText(javaColumn);
                    continue;
                }

                if (delete != null) {
                    String javaColumn = String.format("%s, ", dbColumnName);
                    dbTrimElement.addText(javaColumn);
                    continue;
                }

                Element dbTrimIfElement = dbTrimElement.addElement("if");
                dbTrimIfElement.addAttribute("test", buildTestNotNull(javaColumnName));
                dbTrimIfElement.addText(String.format("%s, ", dbColumnName));
            }
        } else {
            mapperInfo.getResultMapInfo().getColumnInfoList().forEach(columnInfo -> {
                dbTrimElement.addText(String.format("%s, ", columnInfo.getDbColumnName()));
            });
        }

        Element javaTrimElement = updateElement.addElement("trim");
        javaTrimElement.addAttribute("prefix", "values (");
        javaTrimElement.addAttribute("suffix", ")");
        javaTrimElement.addAttribute("suffixOverrides", ",");

        if (methodInfo.getDynamic()) {
            for (int i = 0; i < columnInfoList.size(); i++) {
                ColumnInfo columnInfo = columnInfoList.get(i);
                String typeHandler = columnInfo.getTypeHandler();
                String javaColumnName = columnInfo.getJavaColumnName();
                Lock lock = columnInfo.getLock();
                LogicDelete delete = columnInfo.getDelete();

                if (lock != null) {
                    String javaColumn = String.format("%s, ", lock.initValue());
                    javaTrimElement.addText(javaColumn);
                    continue;
                }

                if (delete != null) {
                    String javaColumn = String.format("%s, ", delete.show());
                    javaTrimElement.addText(javaColumn);
                    continue;
                }

                Element javaTrimIfElement = javaTrimElement.addElement("if");
                javaTrimIfElement.addAttribute("test", buildTestNotNull(javaColumnName));
                String javaColumn = String.format("#{%s%s},", javaColumnName, buildTypeHandler(typeHandler));
                javaTrimIfElement.addText(javaColumn);
            }
        } else {
            for (int i = 0; i < columnInfoList.size(); i++) {
                ColumnInfo columnInfo = columnInfoList.get(i);
                String typeHandler = columnInfo.getTypeHandler();
                Lock lock = columnInfo.getLock();
                LogicDelete delete = columnInfo.getDelete();

                if (lock != null) {
                    String javaColumn = String.format("%s, ", lock.initValue());
                    javaTrimElement.addText(javaColumn);
                    continue;
                }

                if (delete != null) {
                    String javaColumn = String.format("%s, ", delete.show());
                    javaTrimElement.addText(javaColumn);
                    continue;
                }

                String javaColumn = String.format("#{%s%s}, ", columnInfo.getJavaColumnName(), buildTypeHandler(typeHandler));
                javaTrimElement.addText(javaColumn);
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
