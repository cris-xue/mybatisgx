package com.lc.mybatisx.template;

import com.lc.mybatisx.annotation.LogicDelete;
import com.lc.mybatisx.context.EntityInfoContextHolder;
import com.lc.mybatisx.model.*;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class InsertTemplateHandler {

    private static final Logger logger = LoggerFactory.getLogger(InsertTemplateHandler.class);

    public String execute(MapperInfo mapperInfo, MethodInfo methodInfo) {
        return simpleTemplateHandle(mapperInfo, methodInfo);
    }

    private String simpleTemplateHandle(MapperInfo mapperInfo, MethodInfo methodInfo) {
        return buildInsertSelectiveXNode(mapperInfo, methodInfo);
    }

    private String buildInsertSelectiveXNode(MapperInfo mapperInfo, MethodInfo methodInfo) {
        Document document = DocumentHelper.createDocument();
        Element mapperElement = document.addElement("mapper");
        Element insertElement = mapperElement.addElement("insert");
        insertElement.addAttribute("id", methodInfo.getMethodName());
        String keyProperty = this.getKeyProperty(methodInfo);
        insertElement.addAttribute("keyProperty", keyProperty);
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

        this.setValue(mapperInfo, methodInfo, javaTrimElement);
        return document.asXML();
    }

    private String getKeyProperty(MethodInfo methodInfo) {
        if (methodInfo.getBatch()) {
            MethodParamInfo dataMethodParamInfo = null;
            List<MethodParamInfo> methodParamInfoList = methodInfo.getMethodParamInfoList();
            for (MethodParamInfo methodParamInfo : methodParamInfoList) {
                if (!methodParamInfo.getBatchSize()) {
                    dataMethodParamInfo = methodParamInfo;
                }
            }
            if (dataMethodParamInfo != null) {
                return String.format("%s.id", dataMethodParamInfo.getBatchItemName());
            } else {
                throw new RuntimeException("新增批量方法必须有数据列表");
            }
        } else {
            return "id";
        }
    }

    private void setColumn(MethodInfo methodInfo, Element dbTrimElement) {
        List<MethodParamInfo> methodParamInfoList = methodInfo.getMethodParamInfoList();
        for (MethodParamInfo methodParamInfo : methodParamInfoList) {
            if (methodParamInfo.getBasicType()) {
                continue;
            }
            this.setColumn(methodInfo, methodParamInfo, dbTrimElement);
        }
    }

    private void setColumn(MethodInfo methodInfo, MethodParamInfo methodParamInfo, Element dbTrimElement) {
        List<ColumnInfo> tableColumnInfoList = this.getTableColumnInfoList(methodParamInfo.getType());
        for (ColumnInfo columnInfo : tableColumnInfoList) {
            String javaColumnName = columnInfo.getJavaColumnName();
            String dbColumnName = columnInfo.getDbColumnName();

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

    private void setValue(MapperInfo mapperInfo, MethodInfo methodInfo, Element javaTrimElement) {
        Boolean isBatch = methodInfo.getBatch();
        List<MethodParamInfo> methodParamInfoList = methodInfo.getMethodParamInfoList();
        for (MethodParamInfo methodParamInfo : methodParamInfoList) {
            if (isBatch && methodParamInfo.getBatchSize()) {
                continue;
            }
            Boolean isBasicType = methodParamInfo.getBasicType();
            if (isBasicType) {
                throw new RuntimeException("新增方法参数不支持定义基础类型");
            }

            if (methodParamInfoList.size() == 1) {
                Param param = methodParamInfo.getParam();
                if (param == null) {
                    this.handleSingleBusinessObjectParam(methodInfo, methodParamInfo, javaTrimElement);
                } else {
                    this.handleSingleBusinessObjectParamAnnotation(methodInfo, methodParamInfo, javaTrimElement);
                }
            } else {
                this.handleSingleBusinessObjectParamAnnotation(methodInfo, methodParamInfo, javaTrimElement);
            }
        }
    }

    private void handleSingleBusinessObjectParam(MethodInfo methodInfo, MethodParamInfo methodParamInfo, Element javaTrimElement) {
        List<ColumnInfo> tableColumnInfoList = this.getTableColumnInfoList(methodParamInfo.getType());
        if (ObjectUtils.isEmpty(tableColumnInfoList)) {
            throw new RuntimeException("实体表字段不存在");
        }
        for (ColumnInfo tableColumnInfo : tableColumnInfoList) {
            String javaColumnName = tableColumnInfo.getJavaColumnName();
            String typeHandler = tableColumnInfo.getTypeHandler();

            LogicDelete logicDelete = tableColumnInfo.getLogicDelete();
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

    private void handleSingleBusinessObjectParamAnnotation(MethodInfo methodInfo, MethodParamInfo methodParamInfo, Element javaTrimElement) {
        Boolean isBatch = methodInfo.getBatch();
        List<ColumnInfo> tableColumnInfoList = this.getTableColumnInfoList(methodParamInfo.getType());
        if (ObjectUtils.isEmpty(tableColumnInfoList)) {
            throw new RuntimeException("实体表字段不存在");
        }
        for (ColumnInfo tableColumnInfo : tableColumnInfoList) {
            String javaColumnName = tableColumnInfo.getJavaColumnName();
            String typeHandler = tableColumnInfo.getTypeHandler();

            LogicDelete logicDelete = tableColumnInfo.getLogicDelete();
            if (logicDelete != null) {
                String javaColumn = String.format("'%s'%s,", logicDelete.show(), buildTypeHandler(typeHandler));
                javaTrimElement.addText(javaColumn);
                continue;
            }

            String paramName = isBatch ? methodParamInfo.getBatchItemName() : methodParamInfo.getParamName();
            if (methodInfo.getDynamic()) {
                Element javaTrimIfElement = javaTrimElement.addElement("if");
                javaTrimIfElement.addAttribute("test", buildTestNotNull(javaColumnName));
                String javaColumn = String.format("#{%s.%s%s},", paramName, javaColumnName, buildTypeHandler(typeHandler));
                javaTrimIfElement.addText(javaColumn);
            } else {
                String javaColumn = String.format("#{%s.%s%s},", paramName, javaColumnName, buildTypeHandler(typeHandler));
                javaTrimElement.addText(javaColumn);
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

    private List<ColumnInfo> getTableColumnInfoList(Class<?> entityClass) {
        EntityInfo entityInfo = EntityInfoContextHolder.get(entityClass);
        return entityInfo.getTableColumnInfoList();
    }
}