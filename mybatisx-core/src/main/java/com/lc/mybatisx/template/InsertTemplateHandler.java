package com.lc.mybatisx.template;

import com.lc.mybatisx.annotation.Lock;
import com.lc.mybatisx.annotation.LogicDelete;
import com.lc.mybatisx.model.ColumnInfo;
import com.lc.mybatisx.model.MapperInfo;
import com.lc.mybatisx.model.MethodInfo;
import com.lc.mybatisx.model.MethodParamInfo;
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

public class InsertTemplateHandler {

    private static final Logger logger = LoggerFactory.getLogger(InsertTemplateHandler.class);

    public XNode execute(MapperInfo mapperInfo, MethodInfo methodInfo) {
        return simpleTemplateHandle(mapperInfo, methodInfo);
    }

    private XNode simpleTemplateHandle(MapperInfo mapperInfo, MethodInfo methodInfo) {
        return buildInsertSelectiveXNode(mapperInfo, methodInfo);
    }

    private XNode buildInsertSelectiveXNode(MapperInfo mapperInfo, MethodInfo methodInfo) {
        Document document = DocumentHelper.createDocument();
        Element mapperElement = document.addElement("mapper");
        Element insertElement = mapperElement.addElement("insert");
        insertElement.addAttribute("id", methodInfo.getMethodName());
        insertElement.addAttribute("keyProperty", "id");
        insertElement.addAttribute("useGeneratedKeys", "true");
        insertElement.addText(String.format("insert into %s", mapperInfo.getEntityInfo().getTableName()));

        Element dbTrimElement = insertElement.addElement("trim");
        dbTrimElement.addAttribute("prefix", "(");
        dbTrimElement.addAttribute("suffix", ")");
        dbTrimElement.addAttribute("suffixOverrides", ",");

        this.setColumn(methodInfo, dbTrimElement);

        Element javaTrimElement = insertElement.addElement("trim");
        javaTrimElement.addAttribute("prefix", "values (");
        javaTrimElement.addAttribute("suffix", ")");
        javaTrimElement.addAttribute("suffixOverrides", ",");

        this.setValue(methodInfo, javaTrimElement);

        String insertXmlString = document.asXML();
        logger.info(insertXmlString);
        XPathParser xPathParser = XmlUtils.processXml(insertXmlString);
        XNode xNode = xPathParser.evalNode("/mapper/insert");
        return xNode;
    }

    private void setColumn(MethodInfo methodInfo, Element dbTrimElement) {
        List<MethodParamInfo> methodParamInfoList = methodInfo.getMethodParamInfoList();
        for (int i = 0; i < methodParamInfoList.size(); i++) {
            MethodParamInfo methodParamInfo = methodParamInfoList.get(i);
            List<ColumnInfo> columnInfoList = methodParamInfo.getColumnInfoList();
            for (int j = 0; j < columnInfoList.size(); j++) {
                ColumnInfo columnInfo = columnInfoList.get(j);
                String javaColumnName = columnInfo.getJavaColumnName();
                String dbColumnName = columnInfo.getDbColumnName();
                Lock lock = columnInfo.getLock();

                LogicDelete logicDelete = columnInfo.getLogicDelete();
                if (logicDelete != null) {
                    String javaColumn = String.format("%s,", dbColumnName);
                    dbTrimElement.addText(javaColumn);
                    continue;
                }

                if (methodInfo.getDynamic()) {
                    Element javaTrimIfElement = dbTrimElement.addElement("if");
                    javaTrimIfElement.addAttribute("test", buildTestNotNull(javaColumnName));
                    String javaColumn = String.format("%s,", dbColumnName);
                    javaTrimIfElement.addText(javaColumn);
                } else {
                    String javaColumn = String.format("%s,", dbColumnName);
                    dbTrimElement.addText(javaColumn);
                }
            }
        }
    }

    private void setValue(MethodInfo methodInfo, Element javaTrimElement) {
        List<MethodParamInfo> methodParamInfoList = methodInfo.getMethodParamInfoList();
        for (int i = 0; i < methodParamInfoList.size(); i++) {
            MethodParamInfo methodParamInfo = methodParamInfoList.get(i);
            List<ColumnInfo> columnInfoList = methodParamInfo.getColumnInfoList();
            for (int j = 0; j < columnInfoList.size(); j++) {
                ColumnInfo columnInfo = columnInfoList.get(j);
                String javaColumnName = columnInfo.getJavaColumnName();
                String typeHandler = columnInfo.getTypeHandler();
                Lock lock = columnInfo.getLock();

                LogicDelete logicDelete = columnInfo.getLogicDelete();
                if (logicDelete != null) {
                    String javaColumn = String.format("'%s'%s,", logicDelete.show(), buildTypeHandler(typeHandler));
                    javaTrimElement.addText(javaColumn);
                    continue;
                }

                if (methodInfo.getDynamic()) {
                    Element javaTrimIfElement = javaTrimElement.addElement("if");
                    javaTrimIfElement.addAttribute("test", buildTestNotNull(javaColumnName));
                    String javaColumn = String.format("#{%s%s},", javaColumnName, buildTypeHandler(typeHandler));
                    javaTrimIfElement.addText(javaColumn);
                } else {
                    String javaColumn = String.format("#{%s%s},", javaColumnName, buildTypeHandler(typeHandler));
                    javaTrimElement.addText(javaColumn);
                }
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
