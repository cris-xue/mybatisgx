package com.lc.mybatisx.template.model;

import com.lc.mybatisx.model.ColumnInfo;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.List;

public class InsertNode {

    private Document document;
    private Element insertElement;

    public InsertNode() {
        document = DocumentHelper.createDocument();
        Element mapperElement = document.addElement("mapper");
        insertElement = mapperElement.addElement("insert");
    }

    public InsertNode id(String id) {
        insertElement.addAttribute("id", "insert");
        return this;
    }

    public InsertNode keyProperty(String keyProperty) {
        insertElement.addAttribute("keyProperty", "id");
        return this;
    }

    public InsertNode useGeneratedKeys(Boolean useGeneratedKeys) {
        insertElement.addAttribute("useGeneratedKeys", "true");
        return this;
    }

    public InsertNode tableName(String tableName) {
        insertElement.addText(String.format("insert into %s", tableName));
        return this;
    }

    public InsertNode dbColumn(List<ColumnInfo> columnInfoList) {
        TrimNode trimNode = new TrimNode(insertElement);
        trimNode.prefix("(");
        trimNode.suffix(")");
        trimNode.suffixOverrides(",");
        columnInfoList.forEach(columnInfo -> trimNode.text(element -> element.addText(String.format("%s, ", columnInfo.getDbColumnName()))));
        return this;
    }

    public InsertNode javaColumn(List<ColumnInfo> columnInfoList) {
        TrimNode trimNode = new TrimNode(insertElement);
        trimNode.prefix("value (");
        trimNode.suffix(")");
        trimNode.suffixOverrides(",");
        columnInfoList.forEach(columnInfo -> {
            String typeHandlerTemplate = typeHandler(columnInfo);
            String javaColumn = String.format("#{%s%s}, ", columnInfo.getJavaColumnName(), typeHandlerTemplate);

            trimNode.text(element -> element.addText(javaColumn));
        });
        return this;
    }

    private String typeHandler(ColumnInfo columnInfo) {
        String typeHandler = columnInfo.getTypeHandler();
        String typeHandlerTemplate = "";
        if (StringUtils.isNotBlank(typeHandler)) {
            typeHandlerTemplate = String.format(", typeHandler=%s", typeHandler);
        }
        return typeHandlerTemplate;
    }

}
