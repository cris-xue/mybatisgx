package com.lc.mybatisx.template;

import com.lc.mybatisx.annotation.Lock;
import com.lc.mybatisx.annotation.LogicDelete;
import com.lc.mybatisx.model.*;
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
        Document document = DocumentHelper.createDocument();
        Element mapperElement = document.addElement("mapper");
        Element updateElement = mapperElement.addElement("update");
        updateElement.addAttribute("id", methodInfo.getMethodName());
        updateElement.addText(String.format("update %s", mapperInfo.getTableInfo().getTableName()));

        Element setElement = updateElement.addElement("set");
        Element dbTrimElement = setElement.addElement("trim");
        dbTrimElement.addAttribute("suffixOverrides", ",");

        this.setValue(methodInfo, dbTrimElement);
        whereTemplateHandler.execute(updateElement, mapperInfo.getTableInfo(), methodInfo);

        String insertXmlString = document.asXML();
        logger.info(insertXmlString);
        XPathParser xPathParser = XmlUtils.processXml(insertXmlString);
        XNode xNode = xPathParser.evalNode("/mapper/update");
        return xNode;
    }

    private void setValue(MethodInfo methodInfo, Element dbTrimElement) {
        List<MethodParamInfo> methodParamInfoList = methodInfo.getMethodParamInfoList();
        for (int i = 0; i < methodParamInfoList.size(); i++) {
            MethodParamInfo methodParamInfo = methodParamInfoList.get(i);
            List<ColumnInfo> columnInfoList = methodParamInfo.getColumnInfoList();
            for (int j = 0; j < columnInfoList.size(); j++) {
                ColumnInfo columnInfo = columnInfoList.get(j);

                String javaColumnName = columnInfo.getJavaColumnName();
                String dbColumnName = columnInfo.getDbColumnName();
                String typeHandler = columnInfo.getTypeHandler();
                Lock lock = columnInfo.getLock();
                LogicDelete delete = columnInfo.getDelete();

                if (methodInfo.getDynamic()) {
                    if (lock != null) {
                        String javaColumn = String.format("%s = #{%s} + %s, ", dbColumnName, javaColumnName, lock.increment());
                        dbTrimElement.addText(javaColumn);
                        continue;
                    }

                    if (delete != null) {
                        String javaColumn = String.format("%s = %s, ", dbColumnName, delete.show());
                        dbTrimElement.addText(javaColumn);
                        continue;
                    }

                    Element dbTrimIfElement = dbTrimElement.addElement("if");
                    dbTrimIfElement.addAttribute("test", buildTestNotNull(javaColumnName));
                    String javaColumn = String.format("#{%s%s},", javaColumnName, buildTypeHandler(typeHandler));
                    dbTrimIfElement.addText(String.format("%s = %s", dbColumnName, javaColumn));
                } else {
                    if (lock != null) {
                        String javaColumn = String.format("%s = #{%s} + %s, ", dbColumnName, javaColumnName, lock.increment());
                        dbTrimElement.addText(javaColumn);
                        continue;
                    }

                    if (delete != null) {
                        String javaColumn = String.format("%s = %s, ", dbColumnName, delete.show());
                        dbTrimElement.addText(javaColumn);
                        continue;
                    }

                    String javaColumn = String.format("#{%s%s},", javaColumnName, buildTypeHandler(typeHandler));
                    dbTrimElement.addText(String.format("%s = %s", dbColumnName, javaColumn));
                }
            }
        }
    }

    private void setWhere(TableInfo tableInfo, MethodInfo methodInfo, Element updateElement) {
        // 更新操作的条件不设置动态，因为动态有可能确实条件的时候修改所有数据，比较危险
        Element whereElement = updateElement.addElement("where");

        methodInfo.getConditionInfoList().forEach(conditionInfo -> {
            ColumnInfo columnInfo = tableInfo.getColumnInfo(conditionInfo.getJavaColumnName());
            buildCondition(whereElement, columnInfo, conditionInfo);
        });

        // 逻辑删除
        tableInfo.getColumnInfoMap().forEach((k, columnInfo) -> {
            LogicDelete logicDelete = columnInfo.getDelete();
            if (logicDelete != null) {
                whereElement.addText(String.format(" and %s = %s", columnInfo.getDbColumnName(), logicDelete.show()));
            }
        });

        // 乐观锁版本号
        tableInfo.getColumnInfoMap().forEach((k, columnInfo) -> {
            Lock lock = columnInfo.getLock();
            if (lock != null) {
                whereElement.addText(String.format(" and %s = #{%s}", columnInfo.getDbColumnName(), columnInfo.getJavaColumnName()));
            }
        });
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

    private void buildCondition(Element parentElement, ColumnInfo columnInfo, ConditionInfo conditionInfo) {
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
            String typeHandler = columnInfo.getTypeHandler();
            String typeHandlerTemplate = "";
            if (StringUtils.isNotBlank(typeHandler)) {
                typeHandlerTemplate = String.format(", typeHandler=%s", typeHandler);
            }
            parentElement.addText(String.format("#{%s%s}", paramNameList.get(0), typeHandlerTemplate));
        }
    }

    private void buildIdCondition(Element whereElement, String dbColumnName, String javaColumnName) {
        whereElement.addText(String.format("%s = #{%s}", dbColumnName, javaColumnName));
    }

}
